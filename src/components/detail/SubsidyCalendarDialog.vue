<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { SubsidyInfo, SubsidyCalendar, Trip } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import { formatAmount } from '../../utils/formatter';

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
  }
});

const emit = defineEmits(['save', 'cancel']);

const baseDataStore = useBaseDataStore();

// Local copy of calendars to allow editing and revert on Cancel
const calendars = ref<SubsidyCalendar[]>([]);

watch(() => props.show, (newVal) => {
  if (newVal && props.subsidy) {
    calendars.value = JSON.parse(JSON.stringify(props.subsidy.calendars || []));
  }
});

// City label of arrival city
const cityLabel = computed(() => {
  if (!props.trip) return '';
  return baseDataStore.getCityNameByNo(props.trip.arriveCityNo);
});

// Calculations for standard and actual totals
const standardTotal = computed(() => {
  return calendars.value.reduce((sum, day) => {
    return sum + day.mealStandard + day.transportStandard + day.commStandard;
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

// Checkbox selection utilities
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
      
      // Auto-prefill standard amount if checked, else 0
      day.mealAmount = val ? day.mealStandard : 0;
      day.transportAmount = val ? day.transportStandard : 0;
      day.commAmount = val ? day.commStandard : 0;
    });
  }
});

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
  // Validate all active inputs
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
      <div class="dialog-box calendar-dialog" @click.stop>
        <!-- Header -->
        <div class="dialog-header">
          <span class="dialog-title">补助日历编辑</span>
          <button class="close-btn" @click="emit('cancel')">×</button>
        </div>

        <div class="dialog-split-body">
          <!-- Left side status details card -->
          <div class="left-pane">
            <div class="pane-group">
              <span class="pane-label">出差类型</span>
              <span class="pane-value type-badge">{{ businessTypeName || '未确定' }}</span>
            </div>
            
            <hr class="pane-divider" />
            
            <div class="timeline-container">
              <div class="timeline-node start-node">
                <span class="node-dot start-dot"></span>
                <div class="node-info">
                  <span class="node-date font-mono">{{ trip?.departDate }}</span>
                  <span class="node-city">{{ baseDataStore.getCityNameByNo(trip?.departCityNo || '') }}</span>
                </div>
              </div>
              
              <div class="timeline-span-badge">
                <span class="span-line"></span>
                <span class="days-badge">
                  {{ baseDataStore.getCityNameByNo(trip?.arriveCityNo || '') }} {{ subsidy?.subsidyDays }}天
                </span>
                <span class="span-line"></span>
              </div>
              
              <div class="timeline-node end-node">
                <span class="node-dot end-dot"></span>
                <div class="node-info">
                  <span class="node-date font-mono">{{ trip?.arriveDate }}</span>
                  <span class="node-city">{{ baseDataStore.getCityNameByNo(trip?.arriveCityNo || '') }}</span>
                </div>
              </div>
            </div>
            
            <hr class="pane-divider" />

            <div class="totals-panel">
              <div class="total-row">
                <span class="total-label">标准总金额</span>
                <span class="total-val font-mono">CNY {{ formatAmount(standardTotal) }}</span>
              </div>
              <div class="total-row highlight-row">
                <span class="total-label">补助总金额</span>
                <span class="total-val font-mono">CNY {{ formatAmount(actualTotal) }}</span>
              </div>
            </div>
          </div>

          <!-- Right side matrix checklist table -->
          <div class="right-pane">
            <div class="table-actions">
              <label class="all-select-checkbox">
                <input type="checkbox" v-model="isAllChecked" />
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
                        <input type="checkbox" v-model="isMealColChecked" />
                        <span>餐费补助</span>
                      </div>
                    </th>
                    <th style="width: 140px;">
                      <div class="header-checkbox-cell">
                        <input type="checkbox" v-model="isTransportColChecked" />
                        <span>交通补助</span>
                      </div>
                    </th>
                    <th style="width: 140px;">
                      <div class="header-checkbox-cell">
                        <input type="checkbox" v-model="isCommColChecked" />
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
                        <input 
                          type="checkbox" 
                          :checked="isRowChecked(day)"
                          @change="handleRowToggle(day, $event)"
                        />
                        <div class="date-text">
                          <span class="font-mono">{{ day.subsidyDate }}</span>
                          <span class="weekday">{{ day.weekDay }}</span>
                        </div>
                      </div>
                    </td>
                    
                    <!-- City -->
                    <td class="text-center">
                      <span class="city-tag">📍 {{ cityLabel }}</span>
                    </td>
                    
                    <!-- Meal allowance cell -->
                    <td>
                      <div class="allowance-cell">
                        <input 
                          type="checkbox" 
                          :checked="day.mealChecked === 1"
                          @change="handleCellToggle('meal', day)"
                        />
                        <div class="amount-inputs">
                          <span class="std-amount" title="标准金额">标准:{{ day.mealStandard }}</span>
                          <input 
                            type="number" 
                            v-model.number="day.mealAmount"
                            :disabled="day.mealChecked === 0"
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
                          @change="handleCellToggle('transport', day)"
                        />
                        <div class="amount-inputs">
                          <span class="std-amount" title="标准金额">标准:{{ day.transportStandard }}</span>
                          <input 
                            type="number" 
                            v-model.number="day.transportAmount"
                            :disabled="day.transportChecked === 0"
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
                          @change="handleCellToggle('comm', day)"
                        />
                        <div class="amount-inputs">
                          <span class="std-amount" title="标准金额">标准:{{ day.commStandard }}</span>
                          <input 
                            type="number" 
                            v-model.number="day.commAmount"
                            :disabled="day.commChecked === 0"
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

        <!-- Footer -->
        <div class="dialog-footer">
          <button class="btn btn-secondary" @click="emit('cancel')">取消</button>
          <button class="btn btn-primary" @click="handleConfirm">确认</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
.calendar-dialog {
  width: 960px;
}

.dialog-split-body {
  display: flex;
  height: 480px;
  overflow: hidden;
}

/* Left Pane Details */
.left-pane {
  width: 260px;
  background-color: var(--gray-50);
  border-right: 1px solid var(--gray-200);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.pane-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pane-label {
  font-size: 13px;
  color: var(--text-muted);
}

.pane-value {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.type-badge {
  background-color: var(--warning-light);
  color: var(--warning-color);
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  align-self: flex-start;
  font-size: 13px;
  border: 1px solid var(--warning-border);
}

.pane-divider {
  border: none;
  border-top: 1px solid var(--gray-200);
  margin: 16px 0;
}

/* Timeline vertical cards */
.timeline-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 10px 0;
  gap: 6px;
  flex: 1;
}

.timeline-node {
  display: flex;
  align-items: center;
  gap: 12px;
}

.node-dot {
  width: 10px;
  height: 10px;
  border-radius: var(--radius-round);
}

.start-dot {
  background-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.end-dot {
  background-color: var(--success-color);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
}

.node-info {
  display: flex;
  flex-direction: column;
}

.node-date {
  font-size: 13px;
  font-weight: 500;
}

.node-city {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.timeline-span-badge {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 6px 0;
}

.span-line {
  flex: 1;
  height: 1px;
  background-color: var(--gray-300);
}

.days-badge {
  background-color: var(--primary-light);
  color: var(--primary-color);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: var(--radius-round);
  margin: 0 6px;
  white-space: nowrap;
}

.totals-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.highlight-row {
  background-color: #fef3c7;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid #fde68a;
}

.highlight-row .total-label {
  color: #b45309;
  font-weight: 600;
}

.highlight-row .total-val {
  color: #d97706;
  font-size: 16px;
  font-weight: 700;
}

/* Right Pane Checkbox Matrix */
.right-pane {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-actions {
  margin-bottom: 12px;
}

.all-select-checkbox {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.right-pane .table-container {
  flex: 1;
  overflow-y: auto;
  border: 1px solid var(--gray-200);
}

.calendar-table {
  border-collapse: collapse;
}

.calendar-table th {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: var(--gray-50);
}

.header-checkbox-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.date-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-text {
  display: flex;
  flex-direction: column;
}

.weekday {
  font-size: 11px;
  color: var(--text-muted);
}

.city-tag {
  background-color: var(--gray-100);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  font-size: 12px;
  color: var(--text-secondary);
}

.allowance-cell {
  display: flex;
  align-items: center;
  gap: 6px;
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
  padding: 2px 6px;
  font-size: 12px;
  text-align: right;
  height: 24px;
}

.dialog-footer {
  padding: 12px 20px;
  background-color: var(--gray-50);
  border-top: 1px solid var(--gray-200);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.font-mono {
  font-family: monospace;
}
</style>
