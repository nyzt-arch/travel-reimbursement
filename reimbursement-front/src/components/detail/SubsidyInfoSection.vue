<script setup lang="ts">
import { ref, computed } from 'vue';
import type { SubsidyInfo, Trip, SubsidyCalendar } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import { formatAmount } from '../../utils/formatter';
import SectionHeader from '../common/SectionHeader.vue';
import SubsidyCalendarDialog from './SubsidyCalendarDialog.vue';

const props = defineProps({
  model: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    required: true
  },
  businessTypeName: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['toggle', 'update-subsidy']);

const baseDataStore = useBaseDataStore();

// Modal triggers
const showDialog = ref(false);
const activeSubsidy = ref<SubsidyInfo | null>(null);
const activeTrip = ref<Trip | null>(null);

const handleEditSubsidy = (sub: SubsidyInfo) => {
  activeSubsidy.value = sub;
  
  // Find associated trip
  const trip = props.model.trips?.find((t: Trip) => t.id === sub.tripId);
  activeTrip.value = trip || null;
  
  showDialog.value = true;
};

// Calculate summary details for header display
const headerSummaryStr = computed(() => {
  if (!props.model.subsidies || props.model.subsidies.length === 0) {
    return '0.00';
  }
  
  const total = props.model.subsidies.reduce((sum: number, s: SubsidyInfo) => sum + s.subsidyAmount, 0);
  const details = props.model.subsidies.map((s: SubsidyInfo) => {
    const name = baseDataStore.employees.find(e => e.id === s.travelerId)?.name || '未知';
    return `${name}:${s.subsidyDays}天`;
  }).join(', ');
  
  return `${formatAmount(total)} (${details})`;
});

const handleSaveCalendar = (newCalendars: SubsidyCalendar[], totalActual: number) => {
  if (activeSubsidy.value) {
    const updatedSub = {
      ...activeSubsidy.value,
      calendars: newCalendars,
      // Sum standard standard total
      applyAmount: newCalendars.reduce((sum, c) => sum + c.mealStandard + c.transportStandard + c.commStandard, 0),
      subsidyAmount: totalActual
    };
    
    emit('update-subsidy', updatedSub);
  }
  showDialog.value = false;
};
</script>

<template>
  <div class="detail-section card">
    <SectionHeader 
      title="补助信息" 
      :collapsed="collapsed" 
      @toggle="emit('toggle')"
    >
      <template #summary>
        <span class="header-summary-val font-mono">
          CNY {{ headerSummaryStr }}
        </span>
      </template>
    </SectionHeader>
    
    <transition name="collapse">
      <div v-show="!collapsed" class="section-content">
        <!-- Notice bar (yellow) -->
        <div class="warning-bar">
          <span class="warning-icon">⚠️</span>
          <div class="warning-text">
            <span>1、请根据实际出差日期选择补助</span>
            <span>2、出差期间当日有用餐安排的请自行核减当日餐补</span>
            <span>3、出差期间当日有用车的，请自行核减当日交补</span>
          </div>
        </div>

        <!-- Subsidy list table -->
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th style="width: 60px;" class="text-center">序号</th>
                <th style="width: 140px;">出行人</th>
                <th style="width: 220px;">出差日期</th>
                <th style="width: 100px;" class="text-center">补助天s</th>
                <th style="width: 160px;">行程</th>
                <th style="width: 120px;">补助城市</th>
                <th style="width: 120px;" class="text-right">申请金额</th>
                <th style="width: 120px;" class="text-right">补助金额</th>
                <th style="width: 80px;" class="text-center">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!model.subsidies || model.subsidies.length === 0">
                <td colspan="9" class="empty-cell">
                  暂无补助信息。行程信息保存后，将联动在此生成对应的补助条目
                </td>
              </tr>
              <tr v-for="(sub, idx) in model.subsidies" :key="sub.id">
                <td class="text-center font-mono">{{ Number(idx) + 1 }}</td>
                <td>{{ baseDataStore.employees.find(e => e.id === sub.travelerId)?.name }}</td>
                <td class="font-mono">{{ sub.startDate }} 至 {{ sub.endDate }}</td>
                <td class="text-center font-mono">{{ sub.subsidyDays }} 天</td>
                <td>
                  {{ baseDataStore.getCityNameByNo(props.model.trips?.find((t: Trip) => t.id === sub.tripId)?.departCityNo || '') }} - 
                  {{ baseDataStore.getCityNameByNo(sub.subsidyCityNo) }}
                </td>
                <td>
                  <span class="city-tag">📍 {{ baseDataStore.getCityNameByNo(sub.subsidyCityNo) }}</span>
                </td>
                <td class="text-right font-mono">CNY {{ formatAmount(sub.applyAmount) }}</td>
                <td class="text-right font-mono font-bold primary-text">
                  CNY {{ formatAmount(sub.subsidyAmount) }}
                </td>
                <td class="text-center">
                  <button 
                    class="edit-icon-btn" 
                    title="编辑补助日历" 
                    @click="handleEditSubsidy(sub)"
                  >
                    ✏️
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </transition>

    <!-- Subsidy Calendar Grid modal -->
    <SubsidyCalendarDialog 
      :show="showDialog"
      :subsidy="activeSubsidy"
      :trip="activeTrip"
      :business-type-name="businessTypeName"
      @save="handleSaveCalendar"
      @cancel="showDialog = false"
    />
  </div>
</template>

<style scoped>
.detail-section {
  margin-bottom: 20px;
  overflow: hidden;
}

.section-content {
  padding: 20px;
  border-top: 1px solid var(--gray-200);
}

.header-summary-val {
  font-size: 13px;
  color: var(--text-secondary);
  background-color: var(--gray-100);
  padding: 2px 10px;
  border-radius: var(--radius-round);
}

.warning-bar {
  background-color: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: var(--radius-sm);
  padding: 12px 16px;
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.warning-icon {
  font-size: 16px;
}

.warning-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 13px;
  color: #b45309;
}

.empty-cell {
  text-align: center;
  color: var(--text-muted);
  padding: 30px 0 !important;
}

.city-tag {
  background-color: var(--gray-100);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  font-size: 12px;
}

.primary-text {
  color: var(--primary-color);
}

.edit-icon-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 4px;
  border-radius: var(--radius-sm);
}

.edit-icon-btn:hover {
  background-color: var(--gray-100);
}

.font-mono {
  font-family: monospace;
}

.font-bold {
  font-weight: 600;
}

/* Section collapse animation */
.collapse-enter-active,
.collapse-leave-active {
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1), padding 0.3s ease;
  max-height: 800px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
