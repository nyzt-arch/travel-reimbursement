<script setup lang="ts">
/* eslint-disable vue/no-mutating-props */

import { useBaseDataStore } from '../../stores/baseData'
import SectionHeader from '../common/SectionHeader.vue'
import FormField from '../common/FormField.vue'
import TreeSelect from '../TreeSelect.vue'

defineProps({
  model: {
    type: Object,
    required: true,
  },
  collapsed: {
    type: Boolean,
    required: true,
  },
  errors: {
    type: Object,
    default: () => ({}),
  },
})

const baseDataStore = useBaseDataStore()
</script>

<template>
  <div class="detail-section card">
    <SectionHeader title="基础信息" :collapsed="collapsed" @toggle="$emit('toggle')" />

    <transition name="collapse">
      <div v-show="!collapsed" class="section-content">
        <div class="form-grid">
          <!-- Row 1: Title (span 3) -->
          <div class="form-row-full">
            <FormField label="报销标题" required :error="errors.title">
              <input
                type="text"
                v-model="model.title"
                placeholder="请输入报销标题"
                maxlength="500"
              />
            </FormField>
          </div>

          <!-- Row 2: Person, Dept, Company -->
          <div class="form-col">
            <FormField label="报销人" required :error="errors.reimburserId">
              <select v-model="model.reimburserId">
                <option value="">请选择报销人</option>
                <option v-for="e in baseDataStore.employees" :key="e.id" :value="e.id">
                  {{ e.name }} [{{ e.no }}]
                </option>
              </select>
            </FormField>
          </div>

          <div class="form-col">
            <FormField label="报销部门" required :error="errors.reimDepartmentId">
              <select v-model="model.reimDepartmentId">
                <option value="">请选择报销部门</option>
                <option v-for="d in baseDataStore.departments" :key="d.id" :value="d.id">
                  [{{ d.no }}] {{ d.name }}
                </option>
              </select>
            </FormField>
          </div>

          <div class="form-col">
            <FormField label="费用归属公司" required :error="errors.reimCompanyId">
              <select v-model="model.reimCompanyId">
                <option value="">请选择费用归属公司</option>
                <option v-for="c in baseDataStore.companies" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </FormField>
          </div>

          <!-- Row 3: Business Type -->
          <div class="form-row-third">
            <FormField label="业务类型" required :error="errors.businessTypeId">
              <TreeSelect
                v-model="model.businessTypeId"
                :options="baseDataStore.businessTypes"
                placeholder="请选择业务类型"
              />
            </FormField>
          </div>

          <!-- Row 4: Reason (span 3) -->
          <div class="form-row-full">
            <FormField label="出差事由" required :error="errors.reason">
              <textarea
                v-model="model.reason"
                placeholder="请说明本次出差的具体业务背景与工作内容"
                rows="3"
                maxlength="500"
              ></textarea>
            </FormField>
          </div>
        </div>
      </div>
    </transition>
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.form-row-full {
  grid-column: span 3;
}

.form-row-third {
  grid-column: span 1;
}

.form-col {
  grid-column: span 1;
}

/* Section collapse animation */
.collapse-enter-active,
.collapse-leave-active {
  transition:
    max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    padding 0.3s ease;
  max-height: 500px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
