<script setup lang="ts">
import { reactive, watch } from 'vue';
import type { Trip } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import FormField from '../common/FormField.vue';

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

// Form state
const form = reactive({
  id: '',
  travelerId: '',
  departCityNo: '',
  arriveCityNo: '',
  departDate: '',
  arriveDate: '',
  tripDesc: ''
});

// Errors state
const errors = reactive({
  travelerId: '',
  departCityNo: '',
  arriveCityNo: '',
  departDate: '',
  arriveDate: '',
  tripDesc: ''
});

// Initialize form when tripData is passed or updated
watch(() => props.show, (newVal) => {
  if (newVal) {
    // Reset errors
    Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
    
    if (props.tripData) {
      Object.assign(form, JSON.parse(JSON.stringify(props.tripData)));
    } else {
      form.id = '';
      form.travelerId = '';
      form.departCityNo = '';
      form.arriveCityNo = '';
      form.departDate = '';
      form.arriveDate = '';
      form.tripDesc = '';
    }
  }
});

const handleSave = () => {
  // Clear previous errors
  let isValid = true;
  Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');

  // Validation
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
    today.setHours(23, 59, 59, 999); // Allow selecting today
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
      <div class="dialog-box trip-dialog" @click.stop>
        <!-- Header -->
        <div class="dialog-header">
          <span class="dialog-title">{{ tripData?.id ? '编辑行程' : '补录行程' }}</span>
          <button class="close-btn" @click="emit('cancel')">×</button>
        </div>
        
        <!-- Top warning bar (orange) -->
        <div class="notice-bar">
          <p>⚠️ 仅可补录未从申请单带入或未产生费用的行程信息</p>
          <p>💡 跨天跨城行程填写说明：出发城市-到达城市：武汉-北京；出发日期-到达日期：1号-5号；1号~5号补助按北京(到达城市)标准进行核算</p>
        </div>

        <!-- Body -->
        <div class="dialog-body">
          <div class="form-grid">
            <div class="form-col">
              <FormField label="出行人" required :error="errors.travelerId">
                <select v-model="form.travelerId">
                  <option value="">请选择出行人</option>
                  <option v-for="e in baseDataStore.employees" :key="e.id" :value="e.id">
                    {{ e.name }} [{{ e.no }}]
                  </option>
                </select>
              </FormField>
            </div>
            
            <div class="form-col">
              <FormField label="出发城市" required :error="errors.departCityNo">
                <select v-model="form.departCityNo">
                  <option value="">请选择出发城市</option>
                  <option v-for="c in baseDataStore.cities" :key="c.cityNo" :value="c.cityNo">
                    {{ c.cityName }}
                  </option>
                </select>
              </FormField>
            </div>

            <div class="form-col">
              <FormField label="到达城市" required :error="errors.arriveCityNo">
                <select v-model="form.arriveCityNo">
                  <option value="">请选择到达城市</option>
                  <option v-for="c in baseDataStore.cities" :key="c.cityNo" :value="c.cityNo">
                    {{ c.cityName }}
                  </option>
                </select>
              </FormField>
            </div>

            <div class="form-col">
              <FormField label="出发日期" required :error="errors.departDate">
                <input type="date" v-model="form.departDate" />
              </FormField>
            </div>

            <div class="form-col">
              <FormField label="到达日期" required :error="errors.arriveDate">
                <input type="date" v-model="form.arriveDate" />
              </FormField>
            </div>

            <div class="form-row-full mt-10">
              <FormField label="行程说明" required :error="errors.tripDesc">
                <textarea 
                  v-model="form.tripDesc" 
                  placeholder="请输入本次具体行程业务内容或备注说明" 
                  rows="3"
                  maxlength="500"
                ></textarea>
              </FormField>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="dialog-footer">
          <button class="btn btn-secondary" @click="emit('cancel')">取消</button>
          <button class="btn btn-primary" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
.trip-dialog {
  width: 680px;
}

.notice-bar {
  background-color: var(--warning-light);
  border-left: 4px solid var(--warning-color);
  padding: 12px 20px;
  font-size: 13px;
  color: #b45309;
  line-height: 1.6;
}

.notice-bar p {
  margin-bottom: 4px;
}

.notice-bar p:last-child {
  margin-bottom: 0;
}

.dialog-body {
  padding: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-col {
  grid-column: span 1;
}

.form-row-full {
  grid-column: span 2;
}

.mt-10 {
  margin-top: 4px;
}

.dialog-footer {
  padding: 12px 20px;
  background-color: var(--gray-50);
  border-top: 1px solid var(--gray-200);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
