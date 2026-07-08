<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useReimbursementStore } from '../stores/reimbursement'
import { useBaseDataStore } from '../stores/baseData'
import { validateReimbursement, type ValidationErrors } from '../utils/validator'
import { getSubsidyStandard, getDatesBetween } from '../utils/subsidyCalculator'
import type { Reimbursement, Trip, SubsidyInfo, CostAllocation } from '../types'

// 引入子组件
import BasicInfoSection from '../components/detail/BasicInfoSection.vue'
import TripSection from '../components/detail/TripSection.vue'
import SubsidyInfoSection from '../components/detail/SubsidyInfoSection.vue'
import CostSummarySection from '../components/detail/CostSummarySection.vue'
import CostAllocationSection from '../components/detail/CostAllocationSection.vue'
import RemarkSection from '../components/detail/RemarkSection.vue'
import ActionBar from '../components/detail/ActionBar.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'

const route = useRoute()
const router = useRouter()
const reimbursementStore = useReimbursementStore()
const baseDataStore = useBaseDataStore()

// 当前报销单数据
const model = ref<Reimbursement | null>(null)

// 查看模式下或者已提交（审批中）、已作废的单据不可编辑
const isReadOnly = computed(() => route.query.mode === 'view' || model.value?.status === 1 || model.value?.status === 2)

// 6个区块折叠/展开
const collapsedStates = reactive({
  basicInfo: false,
  trips: false,
  subsidies: false,
  summary: false,
  allocations: false,
  remark: false,
})

// 表单校验错误信息
const validationErrors = reactive<ValidationErrors>({})

// 弹窗控制
const showCloseConfirm = ref(false)
const showSubmitSuccess = ref(false)
const showValidationError = ref(false)
const firstErrorMsg = ref('')

const docDate = computed(() => {
  if (!model.value) return ''
  if (model.value.createTime) {
    return model.value.createTime.substring(0, 10)
  }
  return new Date().toISOString().slice(0, 10)
})

const businessTypeName = computed(() => {
  if (!model.value || !model.value.businessTypeId) return ''
  return baseDataStore.getBusinessTypeNameById(model.value.businessTypeId)
})

onMounted(async () => {
  // 确保基础数据（城市、员工、部门等）已加载，防止 HMR 或直接访问导致下拉框为空
  await baseDataStore.loadAllBaseData()

  const id = route.params.id as string
  if (!id || id === 'new') {
    model.value = reimbursementStore.initNewDetail()
    model.value.allocations = [
      {
        id: `A_INIT_${Date.now()}`,
        reimId: model.value.id,
        companyId: '',
        projectId: '',
        allocRatio: 1.0,
        allocAmount: 0.0,
        sortOrder: 1,
      },
    ]
  } else {
    const detail = await reimbursementStore.loadDetail(id)
    if (!detail) {
      router.push('/')
    } else {
      model.value = detail
    }
  }
})

// 重新计算所有金额的合计数
const recalculateTotals = () => {
  if (!model.value) return

  let total = 0
  let meal = 0
  let transport = 0
  let comm = 0

  model.value.subsidies?.forEach((sub) => {
    total += sub.subsidyAmount

    sub.calendars?.forEach((day) => {
      if (day.mealChecked === 1) meal += day.mealAmount
      if (day.transportChecked === 1) transport += day.transportAmount
      if (day.commChecked === 1) comm += day.commAmount
    })
  })

  model.value.subsidyTotal = total
  model.value.mealSubsidyTotal = meal
  model.value.transportSubsidyTotal = transport
  model.value.commSubsidyTotal = comm

  if (model.value.allocations && model.value.allocations.length > 0) {
    const list = model.value.allocations
    if (list.length === 1) {
      const first = list[0]
      if (first) {
        first.allocRatio = 1.0
        first.allocAmount = total
      }
    } else {
      let row2PlusAmountSum = 0
      for (let i = 1; i < list.length; i++) {
        const item = list[i]
        if (item) {
          item.allocAmount = Number((total * item.allocRatio).toFixed(2))
          row2PlusAmountSum += item.allocAmount
        }
      }
      const first = list[0]
      if (first) {
        first.allocAmount = Number((total - row2PlusAmountSum).toFixed(2))
      }
    }
  }
}

const handleUpdateTrips = async (newTrips: Trip[], deletedTripId?: string | null) => {
  if (isReadOnly.value) return
  if (!model.value) return
  const modelId = model.value.id
  model.value.trips = newTrips

  let currentSubsidies = [...(model.value.subsidies || [])]

  if (deletedTripId) {
    currentSubsidies = currentSubsidies.filter((s) => s.tripId !== deletedTripId)
  } else {
    newTrips.forEach((trip) => {
      const existingIdx = currentSubsidies.findIndex((s) => s.tripId === trip.id)

      const tripDays = getDatesBetween(trip.departDate, trip.arriveDate).length

      // 解析城市类型
      const city = baseDataStore.cities.find((c) => c.cityNo === trip.arriveCityNo)
      const cityType = city ? city.cityType : '3'
      const standards = getSubsidyStandard(cityType)

      if (existingIdx !== -1) {
        const sub = currentSubsidies[existingIdx]
        if (
          sub &&
          (sub.startDate !== trip.departDate ||
            sub.endDate !== trip.arriveDate ||
            sub.subsidyCityNo !== trip.arriveCityNo)
        ) {
          const dateList = getDatesBetween(trip.departDate, trip.arriveDate)
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
            commAmount: standards.commStandard,
          }))

          sub.startDate = trip.departDate
          sub.endDate = trip.arriveDate
          sub.subsidyDays = tripDays
          sub.subsidyCityNo = trip.arriveCityNo
          sub.calendars = newCalendars
          sub.applyAmount =
            tripDays *
            (standards.mealStandard + standards.transportStandard + standards.commStandard)
          sub.subsidyAmount = sub.applyAmount
        }
      } else {
        const subId = `S_NEW_${Date.now()}_${trip.id}`
        const dateList = getDatesBetween(trip.departDate, trip.arriveDate)
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
          commAmount: standards.commStandard,
        }))

        currentSubsidies.push({
          id: subId,
          reimId: modelId,
          tripId: trip.id,
          travelerId: trip.travelerId,
          startDate: trip.departDate,
          endDate: trip.arriveDate,
          subsidyDays: tripDays,
          subsidyCityNo: trip.arriveCityNo,
          applyAmount:
            tripDays *
            (standards.mealStandard + standards.transportStandard + standards.commStandard),
          subsidyAmount:
            tripDays *
            (standards.mealStandard + standards.transportStandard + standards.commStandard),
          calendars: newCalendars,
        })
      }
    })
  }

  model.value.subsidies = currentSubsidies
  recalculateTotals()
  // 仅当该报销单已经保存到数据库（非 NEW_ 前缀）时，才自动保存变更
  if (!model.value.id.startsWith('NEW_')) {
    try {
      await reimbursementStore.saveDraft(model.value)
    } catch (error) {
      console.error('自动保存行程数据失败:', error)
    }
  }
}

const handleUpdateSubsidy = async (updatedSub: SubsidyInfo) => {
  if (isReadOnly.value) return
  if (!model.value || !model.value.subsidies) return

  const idx = model.value.subsidies.findIndex((s) => s.id === updatedSub.id)
  if (idx !== -1) {
    model.value.subsidies[idx] = updatedSub
    recalculateTotals()
    // 仅当该报销单已经保存到数据库（非 NEW_ 前缀）时，才自动保存变更
    if (!model.value.id.startsWith('NEW_')) {
      try {
        await reimbursementStore.saveDraft(model.value)
      } catch (e) {
        console.error('自动保存补助金额失败', e)
      }
    }
  }
}

const handleUpdateAllocations = async (newAlloc: CostAllocation[]) => {
  if (isReadOnly.value) return
  if (!model.value) return
  model.value.allocations = newAlloc
  recalculateTotals()

  // 仅当该报销单已经保存到数据库（非 NEW_ 前缀）时，才自动保存变更
  if (!model.value.id.startsWith('NEW_')) {
    try {
      await reimbursementStore.saveDraft(model.value)
    } catch (e) {
      console.error('自动保存分摊失败', e)
    }
  }
}

const handleUpdateRemark = (newRemark: string) => {
  if (!model.value) return
  model.value.remark = newRemark
}

// 底部操作栏
const handleCloseClick = () => {
  showCloseConfirm.value = true
}

const handleSaveDraft = async () => {
  if (!model.value) return
  if (!model.value.title?.trim()) {
    firstErrorMsg.value = '保存草稿时，报销标题不能为空。'
    showValidationError.value = true
    return
  }
  const returnedId = await reimbursementStore.saveDraft(model.value)
  // 保存成功后更新本地 id，后续自动保存才会生效
  if (model.value.id.startsWith('NEW_') && returnedId) {
    model.value.id = returnedId
  }
  router.push('/')
}

const handleSubmit = async () => {
  if (!model.value) return
  // 清除上一次的校验错误
  Object.keys(validationErrors).forEach(
    (key) => delete validationErrors[key as keyof ValidationErrors],
  )
  const res = validateReimbursement(model.value)
  if (!res.isValid) {
    Object.assign(validationErrors, res.errors)
    // 自动展开有错误的区块
    if (
      res.errors.title ||
      res.errors.reason ||
      res.errors.reimburserId ||
      res.errors.reimDepartmentId ||
      res.errors.reimCompanyId ||
      res.errors.businessTypeId
    ) {
      collapsedStates.basicInfo = false
    }
    if (res.errors.trips) {
      collapsedStates.trips = false
    }
    if (res.errors.allocations) {
      collapsedStates.allocations = false
    }
    // 拿到第一条错误信息，用于弹窗展示
    const firstKey = Object.keys(res.errors)[0] as keyof ValidationErrors
    firstErrorMsg.value = res.errors[firstKey] || '表单数据校验未通过，请检查红字提示字段。'
    showValidationError.value = true
    return
  }
  // 保证网络保存成功后，再弹出提交成功提示窗
  await reimbursementStore.submitSheet(model.value)
  showSubmitSuccess.value = true
}

const confirmClose = () => {
  showCloseConfirm.value = false
  router.push('/')
}
</script>

<template>
  <div v-if="model" class="detail-page-container">
    <header class="detail-header">
      <div class="header-content">
        <h2 class="page-title">差旅费用报销单</h2>
        <div class="header-meta font-mono">
          <span>填写日期：{{ docDate }}</span>
        </div>
      </div>
    </header>

    <div class="detail-content-area container">
    <!-- 基本信息，监听@toggle事件来切换状态 -->
      <BasicInfoSection
        :model="model"
        :collapsed="collapsedStates.basicInfo"
        :errors="validationErrors"
        :isReadOnly="isReadOnly"
        @toggle="collapsedStates.basicInfo = !collapsedStates.basicInfo"
      />

      <TripSection
        :model="model"
        :collapsed="collapsedStates.trips"
        :isReadOnly="isReadOnly"
        @toggle="collapsedStates.trips = !collapsedStates.trips"
        @update-trips="handleUpdateTrips"
      />

      <SubsidyInfoSection
        :model="model"
        :collapsed="collapsedStates.subsidies"
        :business-type-name="businessTypeName"
        :isReadOnly="isReadOnly"
        @toggle="collapsedStates.subsidies = !collapsedStates.subsidies"
        @update-subsidy="handleUpdateSubsidy"
      />

      <CostSummarySection
        :model="model"
        :collapsed="collapsedStates.summary"
        @toggle="collapsedStates.summary = !collapsedStates.summary"
      />

      <CostAllocationSection
        :model="model"
        :collapsed="collapsedStates.allocations"
        :isReadOnly="isReadOnly"
        @toggle="collapsedStates.allocations = !collapsedStates.allocations"
        @update-allocations="handleUpdateAllocations"
      />

      <RemarkSection
        :model="model"
        :collapsed="collapsedStates.remark"
        :isReadOnly="isReadOnly"
        @toggle="collapsedStates.remark = !collapsedStates.remark"
        @update-remark="handleUpdateRemark"
      />
    </div>

    <ActionBar :isReadOnly="isReadOnly" @close="handleCloseClick" @save="handleSaveDraft" @submit="handleSubmit" />

    <ConfirmDialog
      :show="showCloseConfirm"
      title="提示"
      message="确认关闭？"
      type="warning"
      @confirm="confirmClose"
      @cancel="showCloseConfirm = false"
    />

    <ConfirmDialog
      :show="showSubmitSuccess"
      title="提示"
      message="提交成功"
      type="success"
      :showCancel="false"
      confirmText="确认"
      @confirm="
        () => {
          showSubmitSuccess = false
          router.push('/')
        }
      "
    />
z
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
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background-color: var(--bg-card);
  border-bottom: 1px solid var(--gray-200);
  padding: 16px 0;
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
  padding-top: 78px;
  padding-bottom: 80px;
}

.font-mono {
  font-family: monospace;
}
</style>
