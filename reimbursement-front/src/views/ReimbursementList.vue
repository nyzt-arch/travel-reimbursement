<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { useReimbursementStore } from '../stores/reimbursement';
import { useBaseDataStore } from '../stores/baseData';
import SearchBar from '../components/SearchBar.vue';
import DataTable from '../components/DataTable.vue';
import Pagination from '../components/Pagination.vue';
import ConfirmDialog from '../components/ConfirmDialog.vue';

const reimbursementStore = useReimbursementStore();
const baseDataStore = useBaseDataStore();

// 搜索条件
const searchFilters = reactive({
  reimNo: '',
  title: '',
  reason: '',
  companyId: '',
  deptId: '',
  reimburserId: '',
  businessTypeId: ''
});

// 分页状态
const currentPage = ref(1);
const pageSize = ref(10);

// 查询接口方法调用
const fetchList = () => {
  reimbursementStore.fetchList({
    ...searchFilters,
    pageNum: currentPage.value,
    pageSize: pageSize.value
  });
};

// 页面加载时拉取基础主数据及单据列表
onMounted(async () => {
  await baseDataStore.loadAllBaseData();
  fetchList();
});

// 监听页码和页大小变化，重新查询后端
watch([currentPage, pageSize], () => {
  fetchList();
});

const handleSearch = (newFilters: Partial<typeof searchFilters>) => {
  Object.assign(searchFilters, newFilters);
  currentPage.value = 1; // 重置到第一页
  fetchList();
};

const handleReset = (clearedFilters: Partial<typeof searchFilters>) => {
  Object.assign(searchFilters, clearedFilters);
  currentPage.value = 1;
  fetchList();
};

// 删除确认弹窗
const showDeleteDialog = ref(false);
const itemToDeleteId = ref<string | null>(null);



const isActionPending = ref(false);

const handleCopy = async (id: string) => {
  if (isActionPending.value) return;
  isActionPending.value = true;
  try {
    // 复制：加载原单详情，生成新 ID 后保存为草稿
    const detail = await reimbursementStore.loadDetail(id);
    if (detail) {
      const copyDetail = {
        ...detail,
        id: `COPY_${Date.now()}`,
        reimNo: '',
        title: (detail.title || '').trim().endsWith('(复制)') ? detail.title : `${detail.title || ''} (复制)`,
        status: 0,
        createTime: '',
        updateTime: ''
      };
      await reimbursementStore.saveDraft(copyDetail);
      await fetchList();
    }
  } finally {
    isActionPending.value = false;
  }
};

const handleDeleteClick = (id: string) => {
  itemToDeleteId.value = id;
  showDeleteDialog.value = true;
};

const confirmDelete = async () => {
  if (isActionPending.value) return;
  isActionPending.value = true;
  try {
    if (itemToDeleteId.value) {
      await reimbursementStore.deleteReimbursement(itemToDeleteId.value);
      itemToDeleteId.value = null;
      await fetchList(); // 刷新
    }
  } finally {
    isActionPending.value = false;
  }
  showDeleteDialog.value = false;
};

const cancelDelete = () => {
  itemToDeleteId.value = null;
  showDeleteDialog.value = false;
};

const handleVoid = async (id: string) => {
  if (isActionPending.value) return;
  isActionPending.value = true;
  try {
    await reimbursementStore.voidReimbursement(id);
    await fetchList(); // 刷新
  } finally {
    isActionPending.value = false;
  }
};

const handleSubmit = async (id: string) => {
  if (isActionPending.value) return;
  isActionPending.value = true;
  try {
    const detail = await reimbursementStore.loadDetail(id);
    if (detail) {
      await reimbursementStore.submitSheet(detail);
      await fetchList();
    }
  } catch {
    // 错误已在拦截器里弹窗提示了，这里只需要静默处理
  } finally {
    isActionPending.value = false;
  }
};


</script>

<template>
  <div class="list-page-container container">
    <!-- Top Filter Area -->
    <SearchBar @search="handleSearch" @reset="handleReset" />

    <!-- Table List Grid -->
    <DataTable
      :items="reimbursementStore.reimbursements"
      :loading="isActionPending"
      @copy="handleCopy"
      @delete="handleDeleteClick"
      @void="handleVoid"
      @submit="handleSubmit"
    />

    <!-- Bottom Pagination -->
    <Pagination
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      :total="reimbursementStore.total"
    />

    <!-- Confirm deletion dialog -->
    <ConfirmDialog
      :show="showDeleteDialog"
      title="提示"
      message="确认删除该报销单吗？删除后数据不可恢复。"
      type="warning"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />


  </div>
</template>

<style scoped>
.list-page-container.container {
  max-width: 100%;
  min-height: 100vh;
  padding: 0 2%;
  background-color: var(--bg-card);
}
</style>
