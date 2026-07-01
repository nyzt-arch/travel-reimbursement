<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useBaseDataStore } from '../stores/baseData';
import TreeSelect from './TreeSelect.vue';

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
      <div class="filter-item">
        <label>费用归属公司</label>
        <select 
          v-model="filters.companyId" 
          @change="handleSearch"
          :class="{ 'placeholder-style': !filters.companyId }"
        >
          <option value="">请选择</option>
          <option v-for="c in baseDataStore.companies" :key="c.id" :value="c.id">
            {{ c.name }}
          </option>
        </select>
      </div>

      <!-- Row 2 -->
      <div class="filter-item">
        <label>报销部门</label>
        <select 
          v-model="filters.deptId" 
          @change="handleSearch"
          :class="{ 'placeholder-style': !filters.deptId }"
        >
          <option value="">请选择</option>
          <option v-for="d in baseDataStore.departments" :key="d.id" :value="d.id">
            {{ d.name }}
          </option>
        </select>
      </div>
      <div class="filter-item">
        <label>报销人</label>
        <select 
          v-model="filters.reimburserId" 
          @change="handleSearch"
          :class="{ 'placeholder-style': !filters.reimburserId }"
        >
          <option value="">请选择</option>
          <option v-for="e in baseDataStore.employees" :key="e.id" :value="e.id">
            {{ e.name }}
          </option>
        </select>
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
  padding: 20px;
  margin-bottom: 20px;
}

.search-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  align-items: center;
}

.filter-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  flex-shrink: 0;
  white-space: nowrap;
}

.filter-item input,
.filter-item select,
.filter-item > *:not(label) {
  flex: 1;
  min-width: 0;
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

.btn-search {
  background-color: #a6dcdf;
}

select.placeholder-style {
  color: var(--text-muted);
}

select.placeholder-style option {
  color: var(--text-primary);
}
</style>
