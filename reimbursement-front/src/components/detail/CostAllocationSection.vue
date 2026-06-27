<script setup lang="ts">
import { ref } from 'vue';
import type { CostAllocation } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import { formatAmount } from '../../utils/formatter';
import SectionHeader from '../common/SectionHeader.vue';
import ConfirmDialog from '../ConfirmDialog.vue';

const props = defineProps({
  model: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['toggle', 'update-allocations']);

const baseDataStore = useBaseDataStore();

// Delete confirm triggers
const showConfirmDelete = ref(false);
const allocToDeleteId = ref<string | null>(null);
const showMinRowAlert = ref(false);

const handleAddRow = () => {
  const newRow: CostAllocation = {
    id: `A_NEW_${Date.now()}`,
    reimId: props.model.id,
    companyId: '',
    projectId: '',
    allocRatio: 0.00,
    allocAmount: 0.00,
    sortOrder: (props.model.allocations?.length || 0) + 1
  };
  
  const currentList = [...(props.model.allocations || [])];
  currentList.push(newRow);
  
  // Recalculate based on new row count
  rebalanceAllocations(currentList);
};

const handleDeleteClick = (id: string) => {
  const list = props.model.allocations || [];
  if (list.length <= 1) {
    showMinRowAlert.value = true;
    return;
  }
  allocToDeleteId.value = id;
  showConfirmDelete.value = true;
};

const confirmDelete = () => {
  if (allocToDeleteId.value) {
    const list = (props.model.allocations || []).filter((a: CostAllocation) => a.id !== allocToDeleteId.value);
    rebalanceAllocations(list);
    allocToDeleteId.value = null;
  }
  showConfirmDelete.value = false;
};

// Rebalance calculations (row 1 takes remainder)
const rebalanceAllocations = (list: CostAllocation[]) => {
  const totalAmount = props.model.subsidyTotal || 0;
  
  if (list.length === 0) {
    emit('update-allocations', []);
    return;
  }
  
  if (list.length === 1) {
    const first = list[0];
    if (first) {
      first.allocRatio = 1.0;
      first.allocAmount = totalAmount;
    }
    emit('update-allocations', list);
    return;
  }
  
  // Sum row 2+ values
  let row2PlusRatioSum = 0;
  let row2PlusAmountSum = 0;
  
  for (let i = 1; i < list.length; i++) {
    const item = list[i];
    if (item) {
      row2PlusRatioSum += item.allocRatio;
      row2PlusAmountSum += item.allocAmount;
    }
  }
  
  // Row 1 remainder
  const first = list[0];
  if (first) {
    first.allocRatio = Math.max(0, 1.0 - row2PlusRatioSum);
    first.allocAmount = Math.max(0, totalAmount - row2PlusAmountSum);
  }
  
  emit('update-allocations', list);
};

// Handle Ratio edit on Row 2+
const handleRatioInput = (index: number, event: Event) => {
  const list = [...(props.model.allocations || [])];
  const item = list[index];
  if (!item) return;

  const inputVal = Number((event.target as HTMLInputElement).value) / 100;
  
  if (isNaN(inputVal) || inputVal < 0) {
    item.allocRatio = 0;
    item.allocAmount = 0;
    (event.target as HTMLInputElement).value = '0.00';
    rebalanceAllocations(list);
    return;
  }
  
  // Calculate sum of other row 2+ ratios
  let otherSum = 0;
  for (let i = 1; i < list.length; i++) {
    const otherItem = list[i];
    if (i !== index && otherItem) {
      otherSum += otherItem.allocRatio;
    }
  }
  
  // Check if exceeds 100%
  if (otherSum + inputVal > 1.0) {
    // ∑（第2+行）> 100%, reset input
    item.allocRatio = 0;
    item.allocAmount = 0;
    (event.target as HTMLInputElement).value = '';
    rebalanceAllocations(list);
    return;
  }
  
  // Set value and link amount
  item.allocRatio = inputVal;
  item.allocAmount = Number((props.model.subsidyTotal * inputVal).toFixed(2));
  
  rebalanceAllocations(list);
};

// Handle Amount edit on Row 2+
const handleAmountInput = (index: number, event: Event) => {
  const list = [...(props.model.allocations || [])];
  const item = list[index];
  if (!item) return;

  const inputVal = Number((event.target as HTMLInputElement).value);
  const totalAmount = props.model.subsidyTotal || 0;
  
  if (isNaN(inputVal) || inputVal < 0 || totalAmount === 0) {
    item.allocAmount = 0;
    item.allocRatio = 0;
    (event.target as HTMLInputElement).value = '0.00';
    rebalanceAllocations(list);
    return;
  }
  
  // Calculate sum of other row 2+ amounts
  let otherSum = 0;
  for (let i = 1; i < list.length; i++) {
    const otherItem = list[i];
    if (i !== index && otherItem) {
      otherSum += otherItem.allocAmount;
    }
  }
  
  // Check if exceeds total
  if (otherSum + inputVal > totalAmount) {
    item.allocAmount = 0;
    item.allocRatio = 0;
    (event.target as HTMLInputElement).value = '';
    rebalanceAllocations(list);
    return;
  }
  
  // Set value and link ratio
  item.allocAmount = inputVal;
  item.allocRatio = Number((inputVal / totalAmount).toFixed(6));
  
  rebalanceAllocations(list);
};

// Equal Split function (均摊)
const handleEqualSplit = () => {
  const list = [...(props.model.allocations || [])];
  const count = list.length;
  if (count === 0) return;
  
  const totalAmount = props.model.subsidyTotal || 0;
  
  if (count === 1) {
    const first = list[0];
    if (first) {
      first.allocRatio = 1.0;
      first.allocAmount = totalAmount;
    }
  } else {
    // Equal ratio
    const splitRatio = Number((1.0 / count).toFixed(6));
    const splitAmount = Number((totalAmount / count).toFixed(2));
    
    // Set for Row 2+
    for (let i = 1; i < count; i++) {
      const item = list[i];
      if (item) {
        item.allocRatio = splitRatio;
        item.allocAmount = splitAmount;
      }
    }
    
    // Row 1 remainder
    let sumRatios = 0;
    let sumAmounts = 0;
    for (let i = 1; i < count; i++) {
      const item = list[i];
      if (item) {
        sumRatios += item.allocRatio;
        sumAmounts += item.allocAmount;
      }
    }
    
    const first = list[0];
    if (first) {
      first.allocRatio = Number((1.0 - sumRatios).toFixed(6));
      first.allocAmount = Number((totalAmount - sumAmounts).toFixed(2));
    }
  }
  
  emit('update-allocations', list);
};

// Ratio display getter
const getRatioDisplay = (val: number) => {
  return (val * 100).toFixed(2);
};
</script>

<template>
  <div class="detail-section card">
    <SectionHeader 
      title="费用归属及分摊" 
      :collapsed="collapsed" 
      @toggle="emit('toggle')"
    >
      <template #summary>
        <span class="header-summary-val font-mono">
          （分摊金额: CNY {{ formatAmount(model.subsidyTotal) }}）
        </span>
      </template>
    </SectionHeader>
    
    <transition name="collapse">
      <div v-show="!collapsed" class="section-content">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th style="width: 60px;" class="text-center">序号</th>
                <th style="width: 260px;">费用归属 *</th>
                <th style="width: 260px;">项目</th>
                <th style="width: 180px;" class="text-right">
                  <div class="header-split-btn">
                    <span>分摊比例 % *</span>
                    <button class="split-btn" @click.stop="handleEqualSplit">⟳ 均摊</button>
                  </div>
                </th>
                <th style="width: 180px;" class="text-right">分摊金额 *</th>
                <th style="width: 80px;" class="text-center">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!model.allocations || model.allocations.length === 0">
                <td colspan="6" class="empty-cell">
                  暂无分摊比例数据，请点击下方进行“添加一行”
                </td>
              </tr>
              <tr v-for="(alloc, idx) in model.allocations" :key="alloc.id">
                <td class="text-center font-mono">{{ Number(idx) + 1 }}</td>
                
                <!-- Company select -->
                <td>
                  <select v-model="alloc.companyId">
                    <option value="">请选择费用归属公司</option>
                    <option v-for="c in baseDataStore.companies" :key="c.id" :value="c.id">
                      {{ c.name }}
                    </option>
                  </select>
                </td>
                
                <!-- Project select -->
                <td>
                  <select v-model="alloc.projectId">
                    <option value="">请选择归属项目（选填）</option>
                    <option v-for="p in baseDataStore.projects" :key="p.id" :value="p.id">
                      {{ p.name }}
                    </option>
                  </select>
                </td>
                
                <!-- Ratio Input (Row 1 is disabled) -->
                <td>
                  <div class="percent-input-wrapper">
                    <input 
                      type="number" 
                      :value="getRatioDisplay(alloc.allocRatio)"
                      :disabled="Number(idx) === 0"
                      class="text-right font-mono"
                      step="0.01"
                      min="0"
                      max="100"
                      @input="handleRatioInput(Number(idx), $event)"
                    />
                    <span class="percent-symbol">%</span>
                  </div>
                </td>
                
                <!-- Amount Input (Row 1 is disabled) -->
                <td>
                  <input 
                    type="number" 
                    :value="alloc.allocAmount.toFixed(2)"
                    :disabled="Number(idx) === 0"
                    class="text-right font-mono"
                    step="0.01"
                    min="0"
                    @input="handleAmountInput(Number(idx), $event)"
                  />
                </td>
                
                <!-- Delete row action -->
                <td class="text-center">
                  <button 
                    class="delete-icon-btn" 
                    title="删除该行" 
                    @click="handleDeleteClick(alloc.id)"
                  >
                    🗑️
                  </button>
                </td>
              </tr>
              
              <!-- Total row -->
              <tr v-if="model.allocations && model.allocations.length > 0" class="totals-row">
                <td colspan="3" class="text-right font-bold">合计</td>
                <td class="text-right font-mono font-bold orange-text">
                  {{ (model.allocations.reduce((sum: number, a: CostAllocation) => sum + a.allocRatio, 0) * 100).toFixed(2) }}%
                </td>
                <td class="text-right font-mono font-bold orange-text">
                  CNY {{ formatAmount(model.allocations.reduce((sum: number, a: CostAllocation) => sum + a.allocAmount, 0)) }}
                </td>
                <td></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Add Row Link -->
        <button class="add-row-btn" @click="handleAddRow">
          ⊕ 添加一行
        </button>
      </div>
    </transition>

    <!-- Delete confirmation -->
    <ConfirmDialog 
      :show="showConfirmDelete"
      title="提示"
      message="确定删除？"
      type="danger"
      @confirm="confirmDelete"
      @cancel="showConfirmDelete = false"
    />

    <!-- Minimum row alert -->
    <ConfirmDialog 
      :show="showMinRowAlert"
      title="提示"
      message="至少保留一条分摊信息"
      type="warning"
      :showCancel="false"
      confirmText="确定"
      @confirm="showMinRowAlert = false"
    />
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

.header-summary-val {
  font-size: 13px;
  color: var(--text-secondary);
}

.header-split-btn {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

.split-btn {
  background-color: var(--primary-light);
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
}

.split-btn:hover {
  background-color: var(--primary-color);
  color: #ffffff;
}

.percent-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.percent-symbol {
  position: absolute;
  right: 12px;
  font-size: 13px;
  color: var(--text-muted);
  pointer-events: none;
}

.percent-input-wrapper input {
  padding-right: 28px;
}

.delete-icon-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 4px;
  border-radius: var(--radius-sm);
}

.delete-icon-btn:hover {
  background-color: var(--danger-light);
}

.totals-row td {
  background-color: #fffbeb !important;
}

.orange-text {
  color: #d97706;
}

.add-row-btn {
  display: inline-flex;
  align-items: center;
  background: transparent;
  border: none;
  color: var(--primary-color);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  margin-top: 12px;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
}

.add-row-btn:hover {
  background-color: var(--primary-light);
}

.empty-cell {
  text-align: center;
  color: var(--text-muted);
  padding: 30px 0 !important;
}

.font-mono {
  font-family: monospace;
}

.font-bold {
  font-weight: 600;
}

/* Section collapse animation */
.collapse-enter-active,
.collapse-leave-active {
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1), padding 0.3s ease;
  max-height: 800px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
