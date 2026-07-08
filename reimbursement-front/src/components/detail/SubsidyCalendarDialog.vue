<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { SubsidyInfo, SubsidyCalendar, Trip } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import { formatAmount, formatDateTime } from '../../utils/formatter';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  subsidy: {
    type: Object as () => SubsidyInfo | null,
    default: null
  },
  trip: {
    type: Object as () => Trip | null,
    default: null
  },
  businessTypeName: {
    type: String,
    default: ''
  },
  isReadOnly: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['save', 'cancel']);

const baseDataStore = useBaseDataStore();

// 复制一份日历数据用于编辑，点取消时丢弃修改
const calendars = ref<SubsidyCalendar[]>([]);

watch(() => props.show, (newVal) => {
  if (newVal && props.subsidy) {
    calendars.value = JSON.parse(JSON.stringify(props.subsidy.calendars || []));
  }
});

// 城市名称
const cityLabel = computed(() => {
  if (!props.trip) return '';
  return baseDataStore.getCityNameByNo(props.trip.arriveCityNo);
});

const departCity = computed(() => {
  if (!props.trip) return '';
  return baseDataStore.getCityNameByNo(props.trip.departCityNo);
});

const arriveCity = computed(() => {
  if (!props.trip) return '';
  return baseDataStore.getCityNameByNo(props.trip.arriveCityNo);
});

// 计算标准总额（仅统计勾选中的补助项）和实际总额
const standardTotal = computed(() => {
  return calendars.value.reduce((sum, day) => {
    let daySum = 0;
    if (day.mealChecked === 1) daySum += day.mealStandard;
    if (day.transportChecked === 1) daySum += day.transportStandard;
    if (day.commChecked === 1) daySum += day.commStandard;
    return sum + daySum;
  }, 0);
});

const actualTotal = computed(() => {
  return calendars.value.reduce((sum, day) => {
    let daySum = 0;
    if (day.mealChecked === 1) daySum += day.mealAmount;
    if (day.transportChecked === 1) daySum += day.transportAmount;
    if (day.commChecked === 1) daySum += day.commAmount;
    return sum + daySum;
  }, 0);
});

// 全选/取消全选
const isAllChecked = computed({
  get() {
    if (calendars.value.length === 0) return false;
    return calendars.value.every(day =>
      day.mealChecked === 1 &&
      day.transportChecked === 1 &&
      day.commChecked === 1
    );
  },
  set(val: boolean) {
    const numVal = val ? 1 : 0;
    calendars.value.forEach(day => {
      day.mealChecked = numVal;
      day.transportChecked = numVal;
      day.commChecked = numVal;
      day.mealAmount = val ? day.mealStandard : 0;
      day.transportAmount = val ? day.transportStandard : 0;
      day.commAmount = val ? day.commStandard : 0;
    });
  }
});

// 列级别全选（餐费、交通、通讯各自独立全选）
const isMealColChecked = computed({
  get() {
    if (calendars.value.length === 0) return false;
    return calendars.value.every(day => day.mealChecked === 1);
  },
  set(val: boolean) {
    const numVal = val ? 1 : 0;
    calendars.value.forEach(day => {
      day.mealChecked = numVal;
      day.mealAmount = val ? day.mealStandard : 0;
    });
  }
});

const isTransportColChecked = computed({
  get() {
    if (calendars.value.length === 0) return false;
    return calendars.value.every(day => day.transportChecked === 1);
  },
  set(val: boolean) {
    const numVal = val ? 1 : 0;
    calendars.value.forEach(day => {
      day.transportChecked = numVal;
      day.transportAmount = val ? day.transportStandard : 0;
    });
  }
});

const isCommColChecked = computed({
  get() {
    if (calendars.value.length === 0) return false;
    return calendars.value.every(day => day.commChecked === 1);
  },
  set(val: boolean) {
    const numVal = val ? 1 : 0;
    calendars.value.forEach(day => {
      day.commChecked = numVal;
      day.commAmount = val ? day.commStandard : 0;
    });
  }
});

const isRowChecked = (day: SubsidyCalendar) => {
  return day.mealChecked === 1 && day.transportChecked === 1 && day.commChecked === 1;
};

const handleRowToggle = (day: SubsidyCalendar, event: Event) => {
  const isChecked = (event.target as HTMLInputElement).checked;
  const numVal = isChecked ? 1 : 0;

  day.mealChecked = numVal;
  day.transportChecked = numVal;
  day.commChecked = numVal;

  day.mealAmount = isChecked ? day.mealStandard : 0;
  day.transportAmount = isChecked ? day.transportStandard : 0;
  day.commAmount = isChecked ? day.commStandard : 0;
};

const handleCellToggle = (type: 'meal' | 'transport' | 'comm', day: SubsidyCalendar) => {
  if (type === 'meal') {
    day.mealChecked = day.mealChecked === 1 ? 0 : 1;
    day.mealAmount = day.mealChecked === 1 ? day.mealStandard : 0;
  } else if (type === 'transport') {
    day.transportChecked = day.transportChecked === 1 ? 0 : 1;
    day.transportAmount = day.transportChecked === 1 ? day.transportStandard : 0;
  } else if (type === 'comm') {
    day.commChecked = day.commChecked === 1 ? 0 : 1;
    day.commAmount = day.commChecked === 1 ? day.commStandard : 0;
  }
};

const validateAmountInput = (type: 'meal' | 'transport' | 'comm', day: SubsidyCalendar) => {
  let val = 0;
  if (type === 'meal') {
    val = Number(day.mealAmount);
    if (isNaN(val) || val < 0) day.mealAmount = 0;
    else if (val > day.mealStandard) day.mealAmount = day.mealStandard;
  } else if (type === 'transport') {
    val = Number(day.transportAmount);
    if (isNaN(val) || val < 0) day.transportAmount = 0;
    else if (val > day.transportStandard) day.transportAmount = day.transportStandard;
  } else if (type === 'comm') {
    val = Number(day.commAmount);
    if (isNaN(val) || val < 0) day.commAmount = 0;
    else if (val > day.commStandard) day.commAmount = day.commStandard;
  }
};

const handleConfirm = () => {
  calendars.value.forEach(day => {
    validateAmountInput('meal', day);
    validateAmountInput('transport', day);
    validateAmountInput('comm', day);
  });

  emit('save', calendars.value, actualTotal.value);
};
</script>

<template>
  <transition name="dialog-fade">
    <div v-if="show" class="dialog-backdrop" @click="emit('cancel')">
      <div class="calendar-card" @click.stop>
        <!-- Title -->
        <h2 class="card-title">补助日历</h2>

        <!-- Split Body: Left Panel + Right Panel -->
        <div class="dialog-split-body">
          <!-- ==================== LEFT PANEL ==================== -->
          <div class="left-panel">
            <!-- Business Type -->
            <div class="biz-type-row">
              <span class="biz-label">出差类型</span>
              <span class="biz-value">{{ businessTypeName || '未确定' }}</span>
            </div>

            <!-- Trip Timeline Card -->
            <div class="timeline-card">
              <!-- Row 1: Start Date -->
              <div class="tl-row tl-start">
                <span class="tl-label">开始日期</span>
                <span class="tl-date">{{ formatDateTime(trip?.departDate) || '—' }}</span>
                <span class="tl-dot tl-dot-start"></span>
              </div>

              <!-- Row 2: Trip Days (blue highlight bar) -->
              <div class="tl-bar">
                <span class="tl-bar-label">行程天数</span>
                <span class="tl-bar-route">{{ departCity }} - {{ arriveCity }}</span>
                <span class="tl-bar-days">{{ subsidy?.subsidyDays || 0 }} 天</span>
              </div>

              <!-- Row 3: End Date -->
              <div class="tl-row tl-end">
                <span class="tl-label">结束日期</span>
                <span class="tl-date">{{ formatDateTime(trip?.arriveDate) || '—' }}</span>
                <span class="tl-dot tl-dot-end"></span>
              </div>
            </div>

            <!-- Amount Statistics Card -->
            <div class="stats-card">
              <div class="stat-row">
                <span class="stat-label">补助金额</span>
                <span class="stat-currency">CNY</span>
                <span class="stat-amount">{{ formatAmount(actualTotal) }}</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">标准总额</span>
                <span class="stat-currency">CNY</span>
                <span class="stat-amount">{{ formatAmount(standardTotal) }}</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">补助金额</span>
                <span class="stat-currency">CNY</span>
                <span class="stat-amount">{{ formatAmount(actualTotal) }}</span>
              </div>
            </div>
          </div>

          <!-- ==================== RIGHT PANEL: Table (original style) ==================== -->
          <div class="right-pane">
            <div class="right-header-row">
              <span class="right-header-label">出差补助</span>
              <label v-if="!isReadOnly" class="all-select-checkbox">
                <input type="checkbox" v-model="isAllChecked" :disabled="isReadOnly" />
                <span>全选 / 取消全选</span>
              </label>
            </div>

            <div class="table-container">
              <table class="data-table calendar-table">
                <thead>
                  <tr>
                    <th style="width: 140px;">出差日期</th>
                    <th style="width: 100px;" class="text-center">补助城市</th>
                    <th style="width: 140px;">
                      <div class="header-checkbox-cell">
                        <input type="checkbox" v-model="isMealColChecked" :disabled="isReadOnly" />
                        <span>餐费补助</span>
                      </div>
                    </th>
                    <th style="width: 140px;">
                      <div class="header-checkbox-cell">
                        <input type="checkbox" v-model="isTransportColChecked" :disabled="isReadOnly" />
                        <span>交通补助</span>
                      </div>
                    </th>
                    <th style="width: 140px;">
                      <div class="header-checkbox-cell">
                        <input type="checkbox" v-model="isCommColChecked" :disabled="isReadOnly" />
                        <span>通讯补助</span>
                      </div>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="day in calendars" :key="day.id">
                    <!-- Date and row-checkbox -->
                    <td>
                      <div class="date-cell">
                        <span class="font-mono date-line">{{ formatDateTime(day.subsidyDate) }}</span>
                        <div class="date-bottom-row">
                          <span class="weekday">{{ day.weekDay }}</span>
                          <input
                            type="checkbox"
                            :checked="isRowChecked(day)"
                            :disabled="isReadOnly"
                            @change="handleRowToggle(day, $event)"
                          />
                        </div>
                      </div>
                    </td>

                    <!-- City -->
                    <td class="text-center">
                      <span class="city-tag">{{ cityLabel }}</span>
                    </td>

                    <!-- Meal allowance cell -->
                    <td>
                      <div class="allowance-cell">
                        <input
                          type="checkbox"
                          :checked="day.mealChecked === 1"
                          :disabled="isReadOnly"
                          @change="handleCellToggle('meal', day)"
                        />
                        <div class="amount-inputs">
                          <span class="std-amount" title="标准金额">标准:{{ day.mealStandard }}</span>
                          <input
                            type="number"
                            v-model.number="day.mealAmount"
                            :disabled="day.mealChecked === 0 || isReadOnly"
                            min="0"
                            :max="day.mealStandard"
                            @blur="validateAmountInput('meal', day)"
                          />
                        </div>
                      </div>
                    </td>

                    <!-- Transport allowance cell -->
                    <td>
                      <div class="allowance-cell">
                        <input
                          type="checkbox"
                          :checked="day.transportChecked === 1"
                          :disabled="isReadOnly"
                          @change="handleCellToggle('transport', day)"
                        />
                        <div class="amount-inputs">
                          <span class="std-amount" title="标准金额">标准:{{ day.transportStandard }}</span>
                          <input
                            type="number"
                            v-model.number="day.transportAmount"
                            :disabled="day.transportChecked === 0 || isReadOnly"
                            min="0"
                            :max="day.transportStandard"
                            @blur="validateAmountInput('transport', day)"
                          />
                        </div>
                      </div>
                    </td>

                    <!-- Comm allowance cell -->
                    <td>
                      <div class="allowance-cell">
                        <input
                          type="checkbox"
                          :checked="day.commChecked === 1"
                          :disabled="isReadOnly"
                          @change="handleCellToggle('comm', day)"
                        />
                        <div class="amount-inputs">
                          <span class="std-amount" title="标准金额">标准:{{ day.commStandard }}</span>
                          <input
                            type="number"
                            v-model.number="day.commAmount"
                            :disabled="day.commChecked === 0 || isReadOnly"
                            min="0"
                            :max="day.commStandard"
                            @blur="validateAmountInput('comm', day)"
                          />
                        </div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Footer Buttons -->
        <div class="card-footer">
          <button class="btn-cancel" @click="emit('cancel')">
            {{ isReadOnly ? '关闭' : '取消' }}
          </button>
          <button v-if="!isReadOnly" class="btn-confirm" @click="handleConfirm">
            确认
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
/* ========== Font & Reset ========== */
* {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ========== Backdrop ========== */
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

/* ========== Card ========== */
.calendar-card {
  width: 1060px;
  min-height: 750px;
  max-height: 95vh;
  background: #ffffff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ========== Title ========== */
.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  padding: 28px 28px 18px 28px;
}

/* ========== Split Body ========== */
.dialog-split-body {
  display: flex;
  gap: 0;
  padding: 0 28px;
  flex: 1;
  min-height: 0;
}

/* ==================== LEFT PANEL ==================== */
.left-panel {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 4px 20px 4px 0;
  border-right: 1px solid #eeeeee;
}

/* -- Business Type -- */
.biz-type-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.biz-label {
  font-size: 14px;
  color: #333333;
}

.biz-value {
  font-size: 14px;
  font-weight: 600;
  color: #ff7d00;
}

/* -- Timeline Card -- */
.timeline-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background: #ffffff;
  padding: 16px 14px;
  display: flex;
  flex-direction: column;
  gap: 0;
  position: relative;
}

.tl-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  position: relative;
}

.tl-label {
  font-size: 13px;
  color: #333333;
}

.tl-date {
  font-size: 13px;
  color: #333333;
  font-weight: 500;
  font-variant-numeric: tabular-nums;
}

/* Timeline dots */
.tl-dot {
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #008AD2;
  z-index: 2;
}

/* White center dot inside blue dot */
.tl-dot::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: #ffffff;
  transform: translate(-50%, -50%);
}

.tl-dot-start {
  left: 38%;
  top: 35%;
  transform: translate(-50%, -50%);
}

.tl-dot-end {
  left: 38%;
  top: 65%;
  transform: translate(-50%, -50%);
}

/* Line from top dot going down toward the bar */
.tl-start::after {
  content: '';
  position: absolute;
  left: 38%;
  top: 35%;
  width: 2px;
  height: 22px;
  background: #008AD2;
  opacity: 0.35;
  z-index: 1;
  transform: translateX(-50%);
}

/* Line from bottom dot going up toward the bar */
.tl-end::after {
  content: '';
  position: absolute;
  left: 38%;
  bottom: 35%;
  width: 2px;
  height: 22px;
  background: #008AD2;
  opacity: 0.35;
  z-index: 1;
  transform: translateX(-50%);
}

/* -- Blue Highlight Bar (Trip Days) -- */
.tl-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #008AD2;
  padding: 6px 14px;
  margin: 4px 0;
  color: #ffffff;
  position: relative;
  z-index: 1;
}

.tl-bar-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
}

.tl-bar-route {
  font-size: 13px;
  font-weight: 600;
  color: #ffffff;
}

.tl-bar-days {
  font-size: 13px;
  font-weight: 600;
  color: #ffffff;
}

/* -- Stats Card -- */
.stats-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background: #ffffff;
  padding: 14px 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stat-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-label {
  font-size: 13px;
  color: #333333;
  flex: 0 0 auto;
}

.stat-currency {
  font-size: 13px;
  color: #666666;
  flex: 1;
  text-align: center;
}

.stat-amount {
  font-size: 16px;
  font-weight: 700;
  color: #ff7d00;
  font-variant-numeric: tabular-nums;
  flex: 0 0 auto;
}

/* ==================== RIGHT PANE (original table style) ==================== */
.right-pane {
  flex: 1;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.right-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.right-header-label {
  font-size: 14px;
  font-weight: 700;
  color: #333333;
}

.all-select-checkbox {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 400;
  color: #666666;
  cursor: pointer;
}

.all-select-checkbox input[type="checkbox"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
  accent-color: #008AD2;
}

.table-container {
  flex: 1;
  overflow-y: auto;
  max-height: 560px;
}

.calendar-table {
  border-collapse: collapse;
  width: 100%;
}

.calendar-table th {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #f7f7f7;
  font-size: 13px;
  font-weight: 500;
  color: #666666;
  padding: 6px 16px;
  border: 1px solid #eeeeee;
}

.calendar-table td {
  font-size: 14px;
  color: #333333;
  padding: 6px 16px;
  border: 1px solid #eeeeee;
}

.calendar-table tbody tr:hover {
  background-color: #fafafa;
}

.header-checkbox-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-checkbox-cell input[type="checkbox"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
  accent-color: #008AD2;
}

.date-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.date-line {
  font-size: 14px;
  color: #333333;
}

.date-bottom-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.date-bottom-row input[type="checkbox"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
  accent-color: #008AD2;
}

.weekday {
  font-size: 11px;
  color: #999999;
}

.city-tag {
  padding: 2px 8px;
  font-size: 13px;
  color: #333333;
}

.allowance-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.allowance-cell input[type="checkbox"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
  accent-color: #008AD2;
}

.amount-inputs {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.std-amount {
  font-size: 10px;
  color: #d97706;
  line-height: 1;
}

.amount-inputs input {
  padding: 4px 8px;
  font-size: 14px;
  text-align: right;
  height: 28px;
  width: 100%;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  outline: none;
  color: #333333;
  font-variant-numeric: tabular-nums;
  transition: border-color 0.15s;
}

.amount-inputs input:focus {
  border-color: #1677ff;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.amount-inputs input:disabled {
  background-color: #f5f5f5;
  color: #999999;
  cursor: not-allowed;
}

.font-mono {
  font-family: "SF Mono", "Cascadia Code", "Fira Code", monospace;
  font-variant-numeric: tabular-nums;
}

.text-center {
  text-align: center;
}

/* ========== Footer ========== */
.card-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 20px 28px;
  border-top: 1px solid #eeeeee;
  margin-top: auto;
}

.btn-cancel {
  font-size: 14px;
  font-weight: 400;
  color: #008AD2;
  background: transparent;
  border: 1px solid #008AD2;
  padding: 8px 20px;
  cursor: pointer;
  border-radius: 6px;
  transition: color 0.2s, background-color 0.2s;
}

.btn-cancel:hover {
  color: #ffffff;
  background-color: #008AD2;
}

.btn-confirm {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  background-color: #008AD2;
  border: none;
  border-radius: 6px;
  padding: 8px 24px;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
}

.btn-confirm:hover {
  background-color: #0070aa;
}

.btn-confirm:active {
  transform: scale(0.97);
}

/* ========== Transition ========== */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.25s ease;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}
</style>
