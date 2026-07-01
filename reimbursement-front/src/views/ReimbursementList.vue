//将原先在前端内存里利用计算属性（filteredItems、pagedItems）
//对静态 Mock 列表进行过滤和分页的纯本地逻辑，重构为基于后端交互的机制。
//在页面挂载时首先并发加载字典数据和报销单列表，
//并在每次页码、每页记录数或搜索条件发生改变时
//自动触发 Axios 请求从数据库拉取最新的单据分页数据。

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'; // 引入 onMounted 和 watch
import { useReimbursementStore } from '../stores/reimbursement';
import { useBaseDataStore } from '../stores/baseData'; // 引入 baseData 以拉取下拉项
import SearchBar from '../components/SearchBar.vue';
import DataTable from '../components/DataTable.vue';
import Pagination from '../components/Pagination.vue';
import ConfirmDialog from '../components/ConfirmDialog.vue';

const reimbursementStore = useReimbursementStore();
const baseDataStore = useBaseDataStore();

// Search state
const searchFilters = reactive({
  reimNo: '',
  title: '',
  reason: '',
  companyId: '',
  deptId: '',
  reimburserId: '',
  businessTypeId: ''
});

// Paging state
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

// Dialog state
const showDeleteDialog = ref(false);
const itemToDeleteId = ref<string | null>(null);



const isActionPending = ref(false);

const handleCopy = async (id: string) => {
  if (isActionPending.value) return;
  isActionPending.value = true;
  try {
    // 复制功能：先加载详情，重命名并生成新ID然后保存到后端
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
      await fetchList(); // 刷新列表
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
      type="danger"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />


  </div>
</template>

<style scoped>
.list-page-container.container {
  max-width: 96%; /* 将当前页面容器的最大宽度改大（可设为百分比或如 1600px） */
}
</style>
