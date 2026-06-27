<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useReimbursementStore } from '../stores/reimbursement';
import { useBaseDataStore } from '../stores/baseData';
import { validateReimbursement, type ValidationErrors } from '../utils/validator';
import { getSubsidyStandard, getDatesBetween } from '../utils/subsidyCalculator';
import type { Reimbursement, Trip, SubsidyInfo, CostAllocation } from '../types';

// Child components
import BasicInfoSection from '../components/detail/BasicInfoSection.vue';
import TripSection from '../components/detail/TripSection.vue';
import SubsidyInfoSection from '../components/detail/SubsidyInfoSection.vue';
import CostSummarySection from '../components/detail/CostSummarySection.vue';
import CostAllocationSection from '../components/detail/CostAllocationSection.vue';
import RemarkSection from '../components/detail/RemarkSection.vue';
import ActionBar from '../components/detail/ActionBar.vue';
import ConfirmDialog from '../components/ConfirmDialog.vue';

const route = useRoute();
const router = useRouter();
const reimbursementStore = useReimbursementStore();
const baseDataStore = useBaseDataStore();

const model = ref<Reimbursement | null>(null);

// Collapsible status tracking
const collapsedStates = reactive({
  basicInfo: false,
  trips: false,
  subsidies: false,
  summary: false,
  allocations: false,
  remark: false
});

// Errors state
const validationErrors = reactive<ValidationErrors>({});

// Modal triggers
const showCloseConfirm = ref(false);
const showSubmitSuccess = ref(false);
const showValidationError = ref(false);
const firstErrorMsg = ref('');


const docDate = computed(() => {
  if (!model.value) return '';
  if (model.value.createTime) {
    return model.value.createTime.substring(0, 10);
  }
  return new Date().toISOString().slice(0, 10);
});

const businessTypeName = computed(() => {
  if (!model.value || !model.value.businessTypeId) return '';
  return baseDataStore.getBusinessTypeNameById(model.value.businessTypeId);
});

onMounted(() => {
  const id = route.params.id as string;
  if (id === 'new') {
    model.value = reimbursementStore.initNewDetail();
    // Default add one blank allocation line on load
    model.value.allocations = [
      {
        id: `A_INIT_${Date.now()}`,
        reimId: model.value.id,
        companyId: '',
        projectId: '',
        allocRatio: 1.0,
        allocAmount: 0.00,
        sortOrder: 1
      }
    ];
  } else {
    const detail = reimbursementStore.loadDetail(id);
    if (!detail) {
      // Redirect to list if not found
      router.push('/');
    } else {
      model.value = detail;
    }
  }
});

// Re-calculate totals
const recalculateTotals = () => {
  if (!model.value) return;
  
  let total = 0;
  let meal = 0;
  let transport = 0;
  let comm = 0;
  
  model.value.subsidies?.forEach(sub => {
    total += sub.subsidyAmount;
    
    sub.calendars?.forEach(day => {
      if (day.mealChecked === 1) meal += day.mealAmount;
      if (day.transportChecked === 1) transport += day.transportAmount;
      if (day.commChecked === 1) comm += day.commAmount;
    });
  });
  
  model.value.subsidyTotal = total;
  model.value.mealSubsidyTotal = meal;
  model.value.transportSubsidyTotal = transport;
  model.value.commSubsidyTotal = comm;
  
  // Link to allocations rebalancer
  if (model.value.allocations && model.value.allocations.length > 0) {
    const list = model.value.allocations;
    if (list.length === 1) {
      const first = list[0];
      if (first) {
        first.allocRatio = 1.0;
        first.allocAmount = total;
      }
    } else {
      // Balance Row 2+ first
      let row2PlusAmountSum = 0;
      for (let i = 1; i < list.length; i++) {
        const item = list[i];
        if (item) {
          item.allocAmount = Number((total * item.allocRatio).toFixed(2));
          row2PlusAmountSum += item.allocAmount;
        }
      }
      // Row 1 remainder
      const first = list[0];
      if (first) {
        first.allocAmount = Number((total - row2PlusAmountSum).toFixed(2));
      }
    }
  }
};

// Handle child sections emitters
const handleUpdateTrips = (newTrips: Trip[], deletedTripId?: string | null) => {
  if (!model.value) return;
  const modelId = model.value.id;
  model.value.trips = newTrips;

  let currentSubsidies = [...(model.value.subsidies || [])];
  
  if (deletedTripId) {
    currentSubsidies = currentSubsidies.filter(s => s.tripId !== deletedTripId);
  } else {
    // If a trip was added or edited, sync it
    newTrips.forEach(trip => {
      const existingIdx = currentSubsidies.findIndex(s => s.tripId === trip.id);
      
      const tripDays = getDatesBetween(trip.departDate, trip.arriveDate).length;
      
      // Resolve city type
      const city = baseDataStore.cities.find(c => c.cityNo === trip.arriveCityNo);
      const cityType = city ? city.cityType : '3';
      const standards = getSubsidyStandard(cityType);
      
      if (existingIdx !== -1) {
        const sub = currentSubsidies[existingIdx];
        if (sub && (sub.startDate !== trip.departDate || sub.endDate !== trip.arriveDate || sub.subsidyCityNo !== trip.arriveCityNo)) {
          const dateList = getDatesBetween(trip.departDate, trip.arriveDate);
          const newCalendars = dateList.map((d, i) => ({
            id: `C_M_${trip.id}_${i}_${Date.now()}`,
            subsidyInfoId: sub.id,
            subsidyDate: d.date,
            weekDay: d.weekday,
            cityNo: trip.arriveCityNo,
            mealChecked: 1,
            mealStandard: standards.mealStandard,
            mealAmount: standards.mealStandard,
            transportChecked: 1,
            transportStandard: standards.transportStandard,
            transportAmount: standards.transportStandard,
            commChecked: 1,
            commStandard: standards.commStandard,
            commAmount: standards.commStandard
          }));
          
          sub.startDate = trip.departDate;
          sub.endDate = trip.arriveDate;
          sub.subsidyDays = tripDays;
          sub.subsidyCityNo = trip.arriveCityNo;
          sub.calendars = newCalendars;
          sub.applyAmount = tripDays * (standards.mealStandard + standards.transportStandard + standards.commStandard);
          sub.subsidyAmount = sub.applyAmount;
        }
      } else {
        const subId = `S_NEW_${Date.now()}_${trip.id}`;
        const dateList = getDatesBetween(trip.departDate, trip.arriveDate);
        const newCalendars = dateList.map((d, i) => ({
          id: `C_M_${trip.id}_${i}_${Date.now()}`,
          subsidyInfoId: subId,
          subsidyDate: d.date,
          weekDay: d.weekday,
          cityNo: trip.arriveCityNo,
          mealChecked: 1,
          mealStandard: standards.mealStandard,
          mealAmount: standards.mealStandard,
          transportChecked: 1,
          transportStandard: standards.transportStandard,
          transportAmount: standards.transportStandard,
          commChecked: 1,
          commStandard: standards.commStandard,
          commAmount: standards.commStandard
        }));
        
        currentSubsidies.push({
          id: subId,
          reimId: modelId,
          tripId: trip.id,
          travelerId: trip.travelerId,
          startDate: trip.departDate,
          endDate: trip.arriveDate,
          subsidyDays: tripDays,
          subsidyCityNo: trip.arriveCityNo,
          applyAmount: tripDays * (standards.mealStandard + standards.transportStandard + standards.commStandard),
          subsidyAmount: tripDays * (standards.mealStandard + standards.transportStandard + standards.commStandard),
          calendars: newCalendars
        });
      }
    });
  }
  
  model.value.subsidies = currentSubsidies;
  recalculateTotals();
};

const handleUpdateSubsidy = (updatedSub: SubsidyInfo) => {
  if (!model.value || !model.value.subsidies) return;
  
  const idx = model.value.subsidies.findIndex(s => s.id === updatedSub.id);
  if (idx !== -1) {
    model.value.subsidies[idx] = updatedSub;
    recalculateTotals();
  }
};

const handleUpdateAllocations = (newAlloc: CostAllocation[]) => {
  if (!model.value) return;
  model.value.allocations = newAlloc;
  recalculateTotals();
};

const handleUpdateRemark = (newRemark: string) => {
  if (!model.value) return;
  model.value.remark = newRemark;
};

// Bottom Bar actions
const handleCloseClick = () => {
  showCloseConfirm.value = true;
};

const handleSaveDraft = () => {
  if (!model.value) return;
  
  // Basic validation bypass for draft saving, just name and title
  if (!model.value.title?.trim()) {
    firstErrorMsg.value = '保存草稿时，报销标题不能为空。';
    showValidationError.value = true;
    return;
  }
  
  reimbursementStore.saveDraft(model.value);
  router.push('/');
};

const handleSubmit = () => {
  if (!model.value) return;
  
  // Clear previous errors
  Object.keys(validationErrors).forEach(key => delete validationErrors[key as keyof ValidationErrors]);
  
  const res = validateReimbursement(model.value);
  if (!res.isValid) {
    Object.assign(validationErrors, res.errors);
    
    // Auto expand sections containing errors
    if (res.errors.title || res.errors.reason || res.errors.reimburserId || res.errors.reimDepartmentId || res.errors.reimCompanyId || res.errors.businessTypeId) {
      collapsedStates.basicInfo = false;
    }
    if (res.errors.trips) {
      collapsedStates.trips = false;
    }
    if (res.errors.allocations) {
      collapsedStates.allocations = false;
    }
    
    // Get the first error message to display in the alert dialog
    const firstKey = Object.keys(res.errors)[0] as keyof ValidationErrors;
    firstErrorMsg.value = res.errors[firstKey] || '表单数据校验未通过，请检查红字提示字段。';
    showValidationError.value = true;
    return;
  }
  
  reimbursementStore.submitSheet(model.value);
  showSubmitSuccess.value = true;
};

const confirmClose = () => {
  showCloseConfirm.value = false;
  router.push('/');
};
</script>

<template>
  <div v-if="model" class="detail-page-container">
    <!-- Top Header (Fixed/Sticky) -->
    <header class="detail-header">
      <div class="header-content">
        <h2 class="page-title">差旅费用报销单</h2>
        <div class="header-meta font-mono">
          <span>撮单日期：{{ docDate }}</span>
        </div>
      </div>
    </header>

    <!-- Scrollable content area -->
    <div class="detail-content-area container">
      <!-- Section 1: Basic Info -->
      <BasicInfoSection 
        :model="model"
        :collapsed="collapsedStates.basicInfo"
        :errors="validationErrors"
        @toggle="collapsedStates.basicInfo = !collapsedStates.basicInfo"
      />

      <!-- Section 2: Trips -->
      <TripSection 
        :model="model"
        :collapsed="collapsedStates.trips"
        @toggle="collapsedStates.trips = !collapsedStates.trips"
        @update-trips="handleUpdateTrips"
      />

      <!-- Section 3: Subsidy details -->
      <SubsidyInfoSection 
        :model="model"
        :collapsed="collapsedStates.subsidies"
        :business-type-name="businessTypeName"
        @toggle="collapsedStates.subsidies = !collapsedStates.subsidies"
        @update-subsidy="handleUpdateSubsidy"
      />

      <!-- Section 4: Cost summary breakdown -->
      <CostSummarySection 
        :model="model"
        :collapsed="collapsedStates.summary"
        @toggle="collapsedStates.summary = !collapsedStates.summary"
      />

      <!-- Section 5: Cost allocations and splitting -->
      <CostAllocationSection 
        :model="model"
        :collapsed="collapsedStates.allocations"
        @toggle="collapsedStates.allocations = !collapsedStates.allocations"
        @update-allocations="handleUpdateAllocations"
      />

      <!-- Section 6: Comments -->
      <RemarkSection 
        :model="model"
        :collapsed="collapsedStates.remark"
        @toggle="collapsedStates.remark = !collapsedStates.remark"
        @update-remark="handleUpdateRemark"
      />
    </div>

    <!-- Bottom Sticky Actions -->
    <ActionBar 
      @close="handleCloseClick"
      @save="handleSaveDraft"
      @submit="handleSubmit"
    />

    <!-- Close Confirmation popup -->
    <ConfirmDialog 
      :show="showCloseConfirm"
      title="提示"
      message="确认关闭当前页面吗？未保存的修改将会丢失。"
      type="warning"
      @confirm="confirmClose"
      @cancel="showCloseConfirm = false"
    />

    <!-- Submission success popup -->
    <ConfirmDialog 
      :show="showSubmitSuccess"
      title="提示"
      message="提交成功！"
      type="success"
      :showCancel="false"
      confirmText="确定"
      @confirm="() => { showSubmitSuccess = false; router.push('/'); }"
    />

    <!-- Validation Error alert popup -->
    <ConfirmDialog 
      :show="showValidationError"
      title="校验未通过"
      :message="firstErrorMsg"
      type="danger"
      :showCancel="false"
      confirmText="好"
      @confirm="showValidationError = false"
    />
  </div>
</template>

<style scoped>
.detail-page-container {
  display: flex;
  flex-direction: column;
}

.detail-header {
  background-color: var(--bg-card);
  border-bottom: 1px solid var(--gray-200);
  padding: 16px 0;
  position: sticky;
  top: var(--header-height);
  z-index: 90;
  margin-bottom: 24px;
}

.header-content {
  max-width: var(--content-width);
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  text-align: center;
  flex: 1;
}

.header-meta {
  font-size: 14px;
  color: var(--text-secondary);
}

.detail-content-area {
  padding-bottom: 80px; /* Allow scrolling past bottom absolute toolbar */
}

.font-mono {
  font-family: monospace;
}
</style>
