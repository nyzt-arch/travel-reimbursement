<script setup lang="ts">
import { ref, computed, reactive } from 'vue';
import { useReimbursementStore } from '../stores/reimbursement';
import SearchBar from '../components/SearchBar.vue';
import DataTable from '../components/DataTable.vue';
import Pagination from '../components/Pagination.vue';
import ConfirmDialog from '../components/ConfirmDialog.vue';

const reimbursementStore = useReimbursementStore();

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

// Filtered list
const filteredItems = computed(() => {
  return reimbursementStore.getReimbursementList(searchFilters);
});

// Paged list
const pagedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredItems.value.slice(start, end);
});

const handleSearch = (newFilters: Partial<typeof searchFilters>) => {
  Object.assign(searchFilters, newFilters);
  currentPage.value = 1; // Reset to page 1
};

const handleReset = (clearedFilters: Partial<typeof searchFilters>) => {
  Object.assign(searchFilters, clearedFilters);
  currentPage.value = 1;
};

// Dialog state
const showDeleteDialog = ref(false);
const itemToDeleteId = ref<string | null>(null);

const showPushDialog = ref(false);
const pushDialogMsg = ref('');

const handleCopy = (id: string) => {
  reimbursementStore.copyReimbursement(id);
};

const handleDeleteClick = (id: string) => {
  itemToDeleteId.value = id;
  showDeleteDialog.value = true;
};

const confirmDelete = () => {
  if (itemToDeleteId.value) {
    reimbursementStore.deleteReimbursement(itemToDeleteId.value);
    itemToDeleteId.value = null;
  }
  showDeleteDialog.value = false;
};

const cancelDelete = () => {
  itemToDeleteId.value = null;
  showDeleteDialog.value = false;
};

const handleVoid = (id: string) => {
  reimbursementStore.voidReimbursement(id);
};

const handlePush = (id: string) => {
  const item = reimbursementStore.reimbursements.find(r => r.id === id);
  if (item) {
    pushDialogMsg.value = `报销单号为 [${item.reimNo}] 的报销单已成功手工推送至财务核算系统！`;
    showPushDialog.value = true;
  }
};
</script>

<template>
  <div class="list-page-container container">
    <!-- Top Filter Area -->
    <SearchBar @search="handleSearch" @reset="handleReset" />

    <!-- Table List Grid -->
    <DataTable 
      :items="pagedItems" 
      @copy="handleCopy"
      @delete="handleDeleteClick"
      @void="handleVoid"
      @push="handlePush"
    />

    <!-- Bottom Pagination -->
    <Pagination 
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      :total="filteredItems.length"
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

    <!-- Push success notification -->
    <ConfirmDialog
      :show="showPushDialog"
      title="手工推送结果"
      :message="pushDialogMsg"
      type="success"
      :showCancel="false"
      confirmText="好"
      @confirm="showPushDialog = false"
    />
  </div>
</template>

<style scoped>
.list-page-container {
  display: flex;
  flex-direction: column;
}
</style>
