<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useBaseDataStore } from '../stores/baseData';
import TreeSelect from './TreeSelect.vue';
import FlatSelect from './FlatSelect.vue';

const router = useRouter();
const baseDataStore = useBaseDataStore();

const emit = defineEmits(['search', 'reset']);

const filters = reactive({
  reimNo: '',
  title: '',
  reason: '',
  companyId: '',
  deptId: '',
  reimburserId: '',
  businessTypeId: ''
});

const handleSearch = () => {
  emit('search', { ...filters });
};

const handleReset = () => {
  filters.reimNo = '';
  filters.title = '';
  filters.reason = '';
  filters.companyId = '';
  filters.deptId = '';
  filters.reimburserId = '';
  filters.businessTypeId = '';
  emit('reset', { ...filters });
};

const handleNew = () => {
  router.push('/detail/new');
};
</script>

<template>
  <div class="search-bar-card card">
    <div class="search-grid">
      <!-- Row 1 -->
      <div class="filter-item">
        <label>报销单号</label>
        <input
          type="text"
          v-model="filters.reimNo"
          placeholder="请输入"
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="filter-item">
        <label>标题</label>
        <input
          type="text"
          v-model="filters.title"
          placeholder="请输入"
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="filter-item">
        <label>事由</label>
        <input
          type="text"
          v-model="filters.reason"
          placeholder="请输入"
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="filter-item filter-item-last">
        <label>费用归属公司</label>
        <FlatSelect
          v-model="filters.companyId"
          :options="baseDataStore.companies"
          placeholder="请选择"
          @change="handleSearch"
        />
      </div>

      <!-- Row 2 -->
      <div class="filter-item">
        <label>报销部门</label>
        <FlatSelect
          v-model="filters.deptId"
          :options="baseDataStore.departments"
          placeholder="请选择"
          @change="handleSearch"
        />
      </div>
      <div class="filter-item">
        <label>报销人</label>
        <FlatSelect
          v-model="filters.reimburserId"
          :options="baseDataStore.employees"
          placeholder="请选择"
          @change="handleSearch"
        />
      </div>
      <div class="filter-item business-type-filter">
        <label>业务类型</label>
        <TreeSelect
          v-model="filters.businessTypeId"
          :options="baseDataStore.businessTypes"
          placeholder="请选择"
          @change="handleSearch"
        />
      </div>

      <!-- Actions -->
      <div class="filter-actions">
        <button class="btn btn-secondary " @click="handleNew">新增</button>
<button class="btn btn-secondary" @click="handleReset">清除</button>
<button class="btn btn-secondary btn-search" @click="handleSearch">搜索</button>

      </div>

    </div>
  </div>
</template>

<style scoped>
.search-bar-card {
  padding: 6px 20px;
  margin-bottom: 12px;
  border: none;
  box-shadow: none;
  background-color: var(--bg-card);
}

.search-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  align-items: center;
}

.filter-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-muted);
  flex-shrink: 0;
  white-space: nowrap;
  width: 84px;
  text-align: right;
}

.filter-item input,
.filter-item :deep(.flat-select-container),
.filter-item :deep(.tree-select-container) {
  width: 250px;
  flex: none;
}

.filter-item input,
.filter-item :deep(.select-trigger) {
  padding: 5px 10px;
}

.filter-actions .btn {
  padding: 5px 12px;
  font-size: 13px;
}

.filter-item-last > label {
  margin-left: auto;
}

.business-type-filter {
  grid-column: span 1;
}

.filter-actions {
  grid-column: span 1;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn-icon {
  font-size: 12px;
}

.filter-actions .btn-secondary {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.btn-search {
  background-color: var(--primary-color);
  color: #ffffff !important;
  border-color: var(--primary-color);
}

.btn-search:hover {
  background-color: var(--primary-hover);
}
</style>
