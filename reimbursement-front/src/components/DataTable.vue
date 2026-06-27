<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import type { Reimbursement } from '../types';
import { useBaseDataStore } from '../stores/baseData';
import { formatAmount, formatDate } from '../utils/formatter';

defineProps({
  items: {
    type: Array as () => Reimbursement[],
    required: true
  }
});

const emit = defineEmits(['copy', 'edit', 'delete', 'void', 'push']);

const router = useRouter();
const baseDataStore = useBaseDataStore();

// Track which row has its "more dropdown" open
const activeMenuId = ref<string | null>(null);

const toggleMenu = (id: string, event: Event) => {
  event.stopPropagation();
  if (activeMenuId.value === id) {
    activeMenuId.value = null;
  } else {
    activeMenuId.value = id;
  }
};

const closeMenu = () => {
  activeMenuId.value = null;
};

const handleAction = (action: string, item: Reimbursement) => {
  closeMenu();
  if (action === 'copy') {
    emit('copy', item.id);
  } else if (action === 'edit') {
    router.push(`/detail/${item.id}`);
  } else if (action === 'delete') {
    emit('delete', item.id);
  } else if (action === 'void') {
    emit('void', item.id);
  } else if (action === 'push') {
    emit('push', item.id);
  }
};

// Global click handler to close dropdown when clicking anywhere else
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
            <th style="width: 50px;" class="text-center">序号</th>
            <th style="width: 100px;" class="text-center">操作</th>
            <th style="width: 140px;">报销单号</th>
            <th style="width: 100px;" class="text-center">单据状态</th>
            <th style="width: 140px;">报销人</th>
            <th style="width: 160px;">报销部门</th>
            <th style="width: 140px;">费用归属公司</th>
            <th style="width: 120px;">业务类型</th>
            <th style="width: 200px;">报销标题</th>
            <th style="width: 200px;">报销事由</th>
            <th style="width: 110px;" class="text-right">补助金额</th>
            <th style="width: 110px;">创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="items.length === 0">
            <td colspan="12" class="empty-cell">
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
                <!-- Copy Icon -->
                <button 
                  class="action-btn" 
                  title="复制报销单" 
                  @click="handleAction('copy', item)"
                >
                  📋
                </button>
                <!-- Edit Icon -->
                <button 
                  class="action-btn" 
                  title="编辑报销单" 
                  @click="handleAction('edit', item)"
                >
                  ✏️
                </button>
                <!-- More Actions Trigger -->
                <div class="more-actions-wrapper">
                  <button 
                    class="action-btn more-btn" 
                    title="更多操作"
                    @click="toggleMenu(item.id, $event)"
                  >
                    ⊙
                  </button>
                  <!-- Dropdown Menu -->
                  <div 
                    v-if="activeMenuId === item.id" 
                    class="more-dropdown-menu"
                    @click.stop
                  >
                    <button class="dropdown-item" @click="handleAction('push', item)">
                      🚀 手工推送
                    </button>
                    <button class="dropdown-item" @click="handleAction('copy', item)">
                      📋 复制
                    </button>
                    <hr class="menu-divider" />
                    <!-- Void action is only allowed for completed sheets -->
                    <button 
                      class="dropdown-item text-danger" 
                      :disabled="item.status === 2"
                      @click="handleAction('void', item)"
                    >
                      🚫 作废
                    </button>
                    <button 
                      class="dropdown-item text-danger" 
                      @click="handleAction('delete', item)"
                    >
                      🗑️ 删除
                    </button>
                  </div>
                </div>
              </div>
            </td>
            
            <!-- No. link -->
            <td>
              <router-link :to="`/detail/${item.id}`" class="link-no">
                {{ item.reimNo }}
              </router-link>
            </td>
            
            <!-- Status Badge -->
            <td class="text-center">
              <span v-if="item.status === 0" class="badge badge-draft">草稿</span>
              <span v-else-if="item.status === 1" class="badge badge-completed">已完成</span>
              <span v-else-if="item.status === 2" class="badge badge-cancelled">已作废</span>
            </td>
            
            <!-- Person -->
            <td>{{ baseDataStore.getEmployeeNameById(item.reimburserId) }}</td>
            
            <!-- Dept -->
            <td>{{ baseDataStore.getDeptNameById(item.reimDepartmentId) }}</td>
            
            <!-- Company -->
            <td>{{ baseDataStore.getCompanyNameById(item.reimCompanyId) }}</td>
            
            <!-- Biz Type -->
            <td>{{ baseDataStore.getBusinessTypeNameById(item.businessTypeId) }}</td>
            
            <!-- Title -->
            <td>
              <router-link :to="`/detail/${item.id}`" class="link-title" :title="item.title">
                {{ item.title }}
              </router-link>
            </td>
            
            <!-- Reason -->
            <td class="cell-reason" :title="item.reason">
              {{ item.reason }}
            </td>
            
            <!-- Subsidy -->
            <td class="text-right font-mono font-bold amount-text">
              CNY {{ formatAmount(item.subsidyTotal) }}
            </td>
            
            <!-- Date -->
            <td class="font-mono text-muted">{{ formatDate(item.createTime) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.table-card {
  padding: 0;
  border-radius: var(--radius-md);
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

.action-btn {
  background: transparent;
  border: 1px solid var(--gray-200);
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  transition: var(--transition-fast);
}

.action-btn:hover {
  background-color: var(--gray-100);
  border-color: var(--gray-300);
}

.more-btn {
  font-size: 12px;
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
  font-weight: 600;
  color: var(--primary-color);
}

.link-title {
  color: var(--text-primary);
  font-weight: 500;
}

.link-title:hover {
  color: var(--primary-color);
}

.cell-reason {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
  color: var(--text-secondary);
}

.amount-text {
  color: var(--text-primary);
}

.font-mono {
  font-family: monospace;
}

.font-bold {
  font-weight: 600;
}
</style>
