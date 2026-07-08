<script setup lang="ts">
import { reactive, watch, computed } from 'vue';
import type { Trip } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  tripData: {
    type: Object as () => Trip | null,
    default: null
  }
});

const emit = defineEmits(['save', 'cancel']);

const baseDataStore = useBaseDataStore();

const employeeOptions = computed(() =>
  baseDataStore.employees.map((e) => ({ id: e.id, name: `${e.name} [${e.no}]` })),
)
const cityOptions = computed(() =>
  baseDataStore.cities.map((c) => ({ id: c.cityNo, name: c.cityName })),
)

// 表单数据
const form = reactive({
  id: '',
  travelerId: '',
  departCityNo: '',
  arriveCityNo: '',
  departDate: '',
  arriveDate: '',
  tripDesc: ''
});

// 表单校验错误信息
const errors = reactive({
  travelerId: '',
  departCityNo: '',
  arriveCityNo: '',
  departDate: '',
  arriveDate: '',
  tripDesc: ''
});

// 获取默认日期时间字符串（dayOffset: 0=今天, -1=昨天）
const getDateDefault = (dayOffset: number = 0): string => {
  const now = new Date();
  now.setDate(now.getDate() + dayOffset);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T00:00:00`;
};

// 确保日期字符串带有 "T00:00:00" 后缀，兼容 datetime-local 输入框
const normalizeDateForInput = (val: string): string => {
  if (!val) return ''
  // 已经有时分秒就直接返回
  if (val.includes('T')) return val
  // 纯日期如 "2026-07-05" → 补上默认时分秒
  if (/^\d{4}-\d{2}-\d{2}$/.test(val)) return val + 'T00:00:00'
  return val
};

// 弹窗打开时初始化表单数据
watch(() => props.show, (newVal) => {
  if (newVal) {
    Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');

    if (props.tripData) {
      Object.assign(form, JSON.parse(JSON.stringify(props.tripData)));
      // 确保日期格式兼容 datetime-local 输入框
      form.departDate = normalizeDateForInput(form.departDate);
      form.arriveDate = normalizeDateForInput(form.arriveDate);
    } else {
      form.id = '';
      form.travelerId = '';
      form.departCityNo = '';
      form.arriveCityNo = '';
      form.departDate = getDateDefault(-1);
      form.arriveDate = getDateDefault(0);
      form.tripDesc = '';
    }
  }
});

const handleSave = () => {
  let isValid = true;
  Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');

  if (!form.travelerId) {
    errors.travelerId = '请选择出行人';
    isValid = false;
  }
  if (!form.departCityNo) {
    errors.departCityNo = '请选择出发城市';
    isValid = false;
  }
  if (!form.arriveCityNo) {
    errors.arriveCityNo = '请选择到达城市';
    isValid = false;
  }
  if (!form.departDate) {
    errors.departDate = '请选择出发日期';
    isValid = false;
  }
  if (!form.arriveDate) {
    errors.arriveDate = '请选择到达日期';
    isValid = false;
  }
  if (!form.tripDesc) {
    errors.tripDesc = '请输入行程说明';
    isValid = false;
  }

  if (form.departDate && form.arriveDate) {
    const start = new Date(form.departDate);
    const end = new Date(form.arriveDate);

    if (end < start) {
      errors.arriveDate = '到达日期不能早于出发日期';
      isValid = false;
    }

    const today = new Date();
    today.setHours(23, 59, 59, 999);
    if (start > today) {
      errors.departDate = '出发日期不能晚于当前日期';
      isValid = false;
    }
    if (end > today) {
      errors.arriveDate = '到达日期不能晚于当前日期';
      isValid = false;
    }
  }

  if (!isValid) return;

  emit('save', { ...form });
};
</script>

<template>
  <transition name="dialog-fade">
    <div v-if="show" class="dialog-backdrop" @click="emit('cancel')">
      <div class="trip-modal" @click.stop>
        <!-- Header -->
        <div class="modal-header">
          <span class="modal-title">补录行程</span>
          <button class="modal-close" @click="emit('cancel')">&times;</button>
        </div>

        <!-- Warning Bar -->
        <div class="warning-bar">
          <span class="warning-icon">!</span>
          <div class="warning-text">
            <p class="warning-title">仅可补录未从申请单带入或未产生费用的行程信息</p>
            <p class="warning-desc">跨天跨城行程填写说明：出发城市 - 到达城市：武汉 - 北京；出发日期 - 到达日期：1 号 - 5 号；1 号～5 号补助按北京匹配；</p>
          </div>
        </div>

        <!-- Form Body -->
        <div class="modal-body">
          <!-- 1. Traveler -->
          <div class="form-row" :class="{ 'has-error': errors.travelerId }">
            <label class="form-label">出行人 <span class="required">*</span></label>
            <div class="form-control-wrap">
              <div class="select-wrapper">
                <select v-model="form.travelerId" class="form-select">
                  <option value="" disabled>请选择出行人</option>
                  <option v-for="emp in employeeOptions" :key="emp.id" :value="emp.id">{{ emp.name }}</option>
                </select>
                <span class="select-chevron"></span>
              </div>
              <p v-if="errors.travelerId" class="field-error">{{ errors.travelerId }}</p>
            </div>
          </div>

          <!-- 2. Depart City -->
          <div class="form-row" :class="{ 'has-error': errors.departCityNo }">
            <label class="form-label">出发城市 <span class="required">*</span></label>
            <div class="form-control-wrap">
              <div class="select-wrapper">
                <select v-model="form.departCityNo" class="form-select">
                  <option value="" disabled>请选择出发城市</option>
                  <option v-for="city in cityOptions" :key="city.id" :value="city.id">{{ city.name }}</option>
                </select>
                <span class="select-clear"></span>
              </div>
              <p v-if="errors.departCityNo" class="field-error">{{ errors.departCityNo }}</p>
            </div>
          </div>

          <!-- 3. Arrive City -->
          <div class="form-row" :class="{ 'has-error': errors.arriveCityNo }">
            <label class="form-label">到达城市 <span class="required">*</span></label>
            <div class="form-control-wrap">
              <div class="select-wrapper">
                <select v-model="form.arriveCityNo" class="form-select">
                  <option value="" disabled>请选择到达城市</option>
                  <option v-for="city in cityOptions" :key="city.id" :value="city.id">{{ city.name }}</option>
                </select>
                <span class="select-clear"></span>
              </div>
              <p v-if="errors.arriveCityNo" class="field-error">{{ errors.arriveCityNo }}</p>
            </div>
          </div>

          <!-- 4. Date Range -->
          <div class="form-row" :class="{ 'has-error': errors.departDate || errors.arriveDate }">
            <label class="form-label">出发到达日期 <span class="required">*</span></label>
            <div class="form-control-wrap">
              <div class="date-range-box">
                <input type="datetime-local" v-model="form.departDate" class="date-input" step="1" />
                <span class="date-separator">—</span>
                <input type="datetime-local" v-model="form.arriveDate" class="date-input" step="1" />
              </div>
              <p v-if="errors.departDate" class="field-error">{{ errors.departDate }}</p>
              <p v-if="errors.arriveDate" class="field-error">{{ errors.arriveDate }}</p>
            </div>
          </div>

          <!-- 5. Trip Description -->
          <div class="form-row" :class="{ 'has-error': errors.tripDesc }">
            <label class="form-label">行程说明 <span class="required">*</span></label>
            <div class="form-control-wrap">
              <textarea
                v-model="form.tripDesc"
                class="form-textarea"
                placeholder="行程说明"
                rows="3"
                maxlength="500"
              ></textarea>
              <p v-if="errors.tripDesc" class="field-error">{{ errors.tripDesc }}</p>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
          <button class="btn-cancel" @click="emit('cancel')">取消</button>
          <button class="btn-save" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
/* ========== Font ========== */
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

/* ========== Modal ========== */
.trip-modal {
  width: 660px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ========== Header ========== */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 16px 24px;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #333333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 22px;
  color: #8c8c8c;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  transition: color 0.15s;
}

.modal-close:hover {
  color: #333333;
}

/* ========== Warning Bar ========== */
.warning-bar {
  background-color: #fffbe6;
  margin: 0 24px;
  padding: 12px 16px;
  border-radius: 4px;
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.warning-icon {
  width: 20px;
  height: 20px;
  min-width: 20px;
  border-radius: 50%;
  background-color: #fa8c16;
  color: #ffffff;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.warning-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.warning-title {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: #333333;
  line-height: 1.5;
}

.warning-desc {
  margin: 0;
  font-size: 13px;
  color: #8c8c8c;
  line-height: 1.6;
}

/* ========== Form Body ========== */
.modal-body {
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.form-label {
  width: 100px;
  flex-shrink: 0;
  font-size: 14px;
  color: #333333;
  padding-top: 8px;
  text-align: right;
}

.required {
  color: #f5222d;
}

.form-control-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: relative;
}

/* ========== Select ========== */
.select-wrapper {
  position: relative;
  width: 280px;
}

.form-select {
  width: 100%;
  height: 36px;
  padding: 0 32px 0 12px;
  font-size: 14px;
  color: #333333;
  background: #ffffff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
  appearance: none;
  cursor: pointer;
  transition: border-color 0.15s;
}

.form-select:focus {
  border-color: #008AD2;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.select-arrow {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  border: 1.5px solid #d9d9d9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: 1;
}

.select-arrow::after {
  content: '';
  display: block;
  width: 5px;
  height: 5px;
  border-right: 1.5px solid #8c8c8c;
  border-bottom: 1.5px solid #8c8c8c;
  transform: rotate(45deg);
  margin-top: -2px;
}

/* Chevron-only (no circle) for traveler select */
.select-chevron {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: 1;
}

.select-chevron::after {
  content: '';
  display: block;
  width: 5px;
  height: 5px;
  border-right: 1.5px solid #8c8c8c;
  border-bottom: 1.5px solid #8c8c8c;
  transform: rotate(45deg);
  margin-top: -2px;
}

/* Clear (×) icon for city selects */
.select-clear {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  border: 1.5px solid #d9d9d9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: 1;
}

.select-clear::before,
.select-clear::after {
  content: '';
  position: absolute;
  width: 1.5px;
  height: 7px;
  background: #8c8c8c;
  border-radius: 1px;
  left: 50%;
  top: 50%;
  transform-origin: center;
}

.select-clear::before {
  transform: translate(-50%, -50%) rotate(45deg);
}

.select-clear::after {
  transform: translate(-50%, -50%) rotate(-45deg);
}

/* ========== Date Range ========== */
.date-range-box {
  display: flex;
  align-items: center;
  gap: 0;
  width: 420px;
  height: 42px;
  padding: 0 14px;
  background: #fafbfc;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.date-range-box:hover {
  border-color: #b0c4de;
  background: #f5f8fb;
}

.date-range-box:focus-within {
  border-color: #008AD2;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(0, 138, 210, 0.08);
}

.date-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333333;
  background: transparent;
  font-variant-numeric: tabular-nums;
  padding: 0;
  height: 100%;
  letter-spacing: 0.02em;
  min-width: 0;
}

.date-input::-webkit-datetime-edit-fields-wrapper {
  padding: 0;
}

.date-input::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.15s;
  padding: 2px;
  margin-left: 2px;
}

.date-input::-webkit-calendar-picker-indicator:hover {
  opacity: 0.8;
}

.date-separator {
  font-size: 15px;
  color: #8c8c8c;
  flex-shrink: 0;
  margin: 0 8px;
  user-select: none;
}

/* ========== Textarea ========== */
.form-textarea {
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  color: #333333;
  background: #ffffff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
  resize: vertical;
  min-height: 64px;
  transition: border-color 0.15s;
}

.form-textarea:focus {
  border-color: #008AD2;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.form-textarea::placeholder {
  color: #8c8c8c;
}

/* ========== Error ========== */
.field-error {
  margin: 0;
  font-size: 12px;
  color: #f5222d;
}

.has-error .form-select,
.has-error .form-textarea,
.has-error .date-range-box {
  border-color: #f5222d;
}

/* ========== Footer ========== */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #eeeeee;
}

.btn-cancel {
  font-size: 14px;
  color: #008AD2;
  background: #ffffff;
  border: 1px solid #008AD2;
  border-radius: 4px;
  padding: 8px 24px;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-cancel:hover {
  color: #0070aa;
  border-color: #0070aa;
}

.btn-save {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  background-color: #008AD2;
  border: none;
  border-radius: 4px;
  padding: 8px 24px;
  cursor: pointer;
  transition: background-color 0.15s;
}

.btn-save:hover {
  background-color: #0070aa;
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
