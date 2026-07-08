<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import type { Reimbursement } from '../types';
import { useBaseDataStore } from '../stores/baseData';
import { formatAmount, formatDate } from '../utils/formatter';

const props = defineProps({
  items: {
    type: Array as () => Reimbursement[],
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['submit','edit','delete','void','copy']);

const router = useRouter();
const baseDataStore = useBaseDataStore();

// 记录当前打开"更多操作"菜单的行
const activeMenuId = ref<string | null>(null);
const menuStyle = ref({ top: '0px', left: '0px' });

const getActiveItem = (): Reimbursement | null => {
  if (activeMenuId.value === null) return null;
  return props.items.find(item => item.id === activeMenuId.value) || null;
};

const toggleMenu = (id: string, event: Event) => {
  event.stopPropagation();
  if (activeMenuId.value === id) {
    activeMenuId.value = null;
  } else {
    activeMenuId.value = id;
    const target = event.currentTarget as HTMLElement;
    const rect = target.getBoundingClientRect();
    menuStyle.value = {
      top: `${rect.bottom + window.scrollY + 6}px`,
      left: `${rect.left + window.scrollX}px`
    };
  }
};

const closeMenu = () => {
  activeMenuId.value = null;
};

const handleAction = (action: string, item: Reimbursement | null) => {
  if (!item) return;
  closeMenu();
  if (action === 'submit') {
    emit('submit', item.id);
  } else if (action === 'copy') {
    emit('copy', item.id);
  } else if (action === 'edit') {
    router.push(`/detail/${item.id}`);
  } else if (action === 'delete') {
    emit('delete', item.id);
  } else if (action === 'void') {
    emit('void', item.id);
  }
};

// 点击页面其他地方时关闭下拉菜单
onMounted(() => {
  document.addEventListener('click', closeMenu);
});

onUnmounted(() => {
  document.removeEventListener('click', closeMenu);
});
</script>

<template>
  <div class="table-card card">
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th style="width: 40px;" class="text-center">
              <svg class="icon-seq" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="5" cy="6" r="2"/>
                <circle cx="5" cy="18" r="2"/>
                <line x1="11" y1="5" x2="22" y2="5"/>
                <line x1="11" y1="8" x2="22" y2="8"/>
                <line x1="11" y1="16" x2="22" y2="16"/>
                <line x1="11" y1="19" x2="22" y2="19"/>
              </svg>
            </th>
            <th style="width: 80px;" class="text-center">操作</th>
            <th style="width: 115px;">报销单号</th>
            <th style="width: 75px;" class="text-center border-right-divider">单据状态</th>
            <th style="width: 85px;" class="text-center">单据类型</th>
            <th style="width: 105px;">报销人</th>
            <th style="width: 120px;">报销部门</th>
            <th style="width: 125px;">费用归属公司</th>
            <th style="width: 95px;">业务类型</th>
            <th style="width: 120px;">报销标题</th>
            <th style="width: 120px;">报销事由</th>
            <th style="width: 90px;" class="text-right">补助金额</th>
            <th style="width: 90px;">创建时间</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="items.length === 0">
            <td colspan="13" class="empty-cell">
              <div class="empty-state">
                <span class="empty-icon">🗂️</span>
                <span class="empty-text">暂无报销单数据</span>
              </div>
            </td>
          </tr>

          <tr v-for="(item, idx) in items" :key="item.id">
            <td class="text-center font-mono">{{ idx + 1 }}</td>

            <!-- Actions Column -->
            <td class="text-center">
              <div class="action-buttons">
                <!-- Submit (draft → completed) -->
                <button
                  class="btn-text-action btn-icon-only"
                  title="提交报销单"
                  :disabled="loading || item.status !== 0"
                  @click="handleAction('submit', item)"
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"/>
                    <polyline points="13 2 13 9 20 9"/>
                    <line x1="9" y1="13" x2="15" y2="13"/>
                    <line x1="9" y1="16" x2="12" y2="16"/>
                  </svg>
                </button>
                <!-- Edit Icon -->
                <button
                  class="btn-text-action btn-icon-only"
                  :title="item.status === 1 ? '审批中单据不可编辑' : (item.status === 2 ? '已作废单据不可编辑' : '编辑报销单')"
                  :disabled="loading || item.status !== 0"
                  @click="handleAction('edit', item)"
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="3" y1="21" x2="21" y2="21"/>
                    <polyline points="17 3 21 7 8 20 4 20 4 16 17 3"/>
                  </svg>
                </button>
                <!-- More Actions Trigger -->
                <div class="more-actions-wrapper">
                  <button
                    class="btn-text-action btn-icon-only"
                    title="更多操作"
                    :disabled="loading"
                    @click="toggleMenu(item.id, $event)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"/>
                      <circle cx="7" cy="12" r="1.5" fill="currentColor" stroke="none"/>
                      <circle cx="12" cy="12" r="1.5" fill="currentColor" stroke="none"/>
                      <circle cx="17" cy="12" r="1.5" fill="currentColor" stroke="none"/>
                    </svg>
                  </button>
                </div>
              </div>
            </td>

            <!-- No. link -->
            <td>
              <router-link :to="{ path: `/detail/${item.id}`, query: { mode: 'view' } }" class="link-no">
                {{ item.reimNo }}
              </router-link>
            </td>

            <!-- Status Badge -->
            <td class="text-center border-right-divider">
              <span v-if="item.status === 0" class="badge badge-draft">草稿</span>
              <span v-else-if="item.status === 1" class="badge badge-completed">审批中</span>
              <span v-else-if="item.status === 2" class="badge badge-cancelled">已作废</span>
            </td>

            <!-- Document Type -->
            <td class="text-center">日常报销单</td>

            <!-- Person -->
            <td>{{ item.reimburserName ? `${item.reimburserName}[${item.reimburserNo}]` : baseDataStore.getEmployeeNameById(item.reimburserId) }}</td>

            <!-- Dept -->
            <td>{{ item.reimDepartmentName ? `[${item.reimDepartmentNo}]${item.reimDepartmentName}` : baseDataStore.getDeptNameById(item.reimDepartmentId) }}</td>

            <!-- Company -->
            <td>{{ item.reimCompanyName || baseDataStore.getCompanyNameById(item.reimCompanyId) }}</td>

            <!-- Biz Type -->
            <td>{{ item.businessTypeName || baseDataStore.getBusinessTypeNameById(item.businessTypeId) }}</td>

            <!-- Title -->
            <td>
              <router-link :to="{ path: `/detail/${item.id}`, query: { mode: 'view' } }" class="link-title" :title="item.title">
                {{ item.title }}
              </router-link>
            </td>

            <!-- Reason -->
            <td class="cell-reason" :title="item.reason">
              {{ item.reason }}
            </td>

            <!-- Subsidy -->
            <td class="text-right font-mono font-bold amount-text">
              {{ formatAmount(item.subsidyTotal) }}
            </td>

            <!-- Date -->
            <td class="font-mono text-muted">{{ formatDate(item.createTime) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <teleport to="body">
    <div
      v-if="activeMenuId !== null"
      class="more-dropdown-menu body-teleported-menu"
      :style="menuStyle"
      @click.stop
    >
      <button
        class="dropdown-item"
        :disabled="getActiveItem()?.status !== 0"
        @click="handleAction('delete', getActiveItem())"
      >
        删除
      </button>
      <button
        class="dropdown-item"
        :disabled="getActiveItem()?.status === 2"
        @click="handleAction('void', getActiveItem())"
      >
        作废
      </button>
      <button
        class="dropdown-item" @click="handleAction('copy', getActiveItem())">
        复制
      </button>
    </div>
  </teleport>
</template>

<style scoped>
.table-card {
  padding: 0;
  border-radius: 0;
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
  border: none;
}

.empty-cell {
  padding: 60px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.empty-icon {
  font-size: 40px;
}

.empty-text {
  font-size: 14px;
  color: var(--text-muted);
}

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-icon-only {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 3px;
  line-height: 1;
  border-radius: 4px;
}

.btn-icon-only svg {
  display: block;
}

.more-actions-wrapper {
  position: relative;
}

.more-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 6px;
  background-color: var(--bg-card);
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-lg);
  z-index: 200;
  padding: 4px 0;
  width: 120px;
  text-align: left;
  animation: dropdownFade 0.15s ease-out;
}

@keyframes dropdownFade {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 8px 16px;
  font-size: 13px;
  color: var(--text-primary);
  background: transparent;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: var(--transition-fast);
}

.dropdown-item:hover:not(:disabled) {
  background-color: var(--gray-50);
  color: var(--primary-color);
}

.dropdown-item:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.dropdown-item.text-danger {
  color: var(--danger-color);
}

.dropdown-item.text-danger:hover:not(:disabled) {
  background-color: var(--danger-light);
  color: var(--danger-hover);
}

.menu-divider {
  border: none;
  border-top: 1px solid var(--gray-200);
  margin: 4px 0;
}

.link-no {
  font-family: monospace;
  font-weight: 400;
  color: var(--primary-color);
}

.link-title {
  color: var(--primary-color);
  font-weight: 400;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
  text-decoration: none;
}

.link-title:hover {
  text-decoration: underline;
}

.cell-reason {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
  color: var(--text-muted);
}

.amount-text {
  color: var(--text-secondary);
  padding-right: 16px;
}

.font-mono {
  font-family: monospace;
}

.font-bold {
  font-weight: 600;
}

.border-right-divider {
  border-right: 1px solid var(--gray-200);
}

:deep(.data-table thead th) {
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
}

.icon-seq {
  color: var(--primary-color);
  vertical-align: middle;
}

:deep(.data-table tbody td) {
  color: var(--text-secondary);
}

:deep(.btn-text-action) {
  color: var(--primary-color);
  font-weight: 400;
}

:deep(.btn-text-action:hover:not(:disabled)) {
  color: var(--primary-hover);
}

:deep(.badge) {
  background: none;
  padding: 0;
  font-size: 12px;
  font-weight: 400;
}

:deep(.badge-draft) {
  color: var(--primary-color);
}

:deep(.badge-completed) {
  color: var(--primary-color);
}

:deep(.badge-cancelled) {
  color: var(--primary-color);
}

:global(.body-teleported-menu) {
  position: absolute;
  background-color: var(--bg-card);
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-lg);
  z-index: 9999;
  padding: 4px 0;
  width: 120px;
  text-align: left;
  box-sizing: border-box;
}

:global(.body-teleported-menu .dropdown-item) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 8px 12px;
  background: transparent;
  border: none;
  font-size: 13px;
  color: var(--text-primary);
  cursor: pointer;
  text-align: left;
  transition: var(--transition-fast);
}

:global(.body-teleported-menu .dropdown-item:hover:not(:disabled)) {
  background-color: var(--gray-50);
}

:global(.body-teleported-menu .dropdown-item:disabled) {
  opacity: 0.4;
  cursor: not-allowed;
}

:global(.body-teleported-menu .menu-divider) {
  margin: 4px 0;
  border: none;
  border-top: 1px solid var(--gray-100);
}
</style>
