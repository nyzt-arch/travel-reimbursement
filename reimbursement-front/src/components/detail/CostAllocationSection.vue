<script setup lang="ts">
import { ref } from 'vue';
import type { CostAllocation } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import { formatAmount } from '../../utils/formatter';
import SectionHeader from '../common/SectionHeader.vue';
import ConfirmDialog from '../ConfirmDialog.vue';
import FlatSelect from '../FlatSelect.vue';

const props = defineProps({
  model: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    required: true
  },
  isReadOnly: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['toggle', 'update-allocations']);

const baseDataStore = useBaseDataStore();

// 删除确认
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

// （1）自动配平函数
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

  // 汇总第 2 行及之后的比例和金额
  let row2PlusRatioSum = 0;
  let row2PlusAmountSum = 0;

  for (let i = 1; i < list.length; i++) {
    const item = list[i];
    if (item) {
      row2PlusRatioSum += item.allocRatio;
      row2PlusAmountSum += item.allocAmount;
    }
  }

  // 第一行取余数
  const first = list[0];
  if (first) {
    first.allocRatio = Math.max(0, Number((1.0 - row2PlusRatioSum).toFixed(4)));
    first.allocAmount = Math.max(0, Number((totalAmount - row2PlusAmountSum).toFixed(2)));
  }

  emit('update-allocations', list);
};

// 方便用户输入（比如用户输入 1234 变成 12.34）
const handleInputNumberLimit = (event: Event) => {
  const target = event.target as HTMLInputElement;

  let digitsStr = target.value.replace(/[^0-9]/g, '');

  if (!digitsStr) {
    digitsStr = '0';
  }

  const intVal = parseInt(digitsStr, 10);

  target.value = (intVal / 100).toFixed(2);
};

// （2）比例输入校验函数
const handleRatioInput = (index: number, event: Event) => {
  const target = event.target as HTMLInputElement;
  const list = [...(props.model.allocations || [])];
  const item = list[index];
  if (!item) return;

  const rawValue = Number(target.value);

  if (isNaN(rawValue) || rawValue < 0) {
    item.allocRatio = 0;
    item.allocAmount = 0;
    target.value = '0.00';
    rebalanceAllocations(list);
    return;
  }

  const roundedValue = Number(rawValue.toFixed(2));
  const inputVal = Number((roundedValue / 100).toFixed(4));
  target.value = roundedValue.toFixed(2);

  let otherSum = 0;
  for (let i = 1; i < list.length; i++) {
    const otherItem = list[i];
    if (i !== index && otherItem) {
      otherSum += otherItem.allocRatio;
    }
  }

  if (Number((otherSum + inputVal).toFixed(4)) > 1.0) {
    item.allocRatio = 0;
    item.allocAmount = 0;
    target.value = '0.00';
    rebalanceAllocations(list);
    return;
  }

  item.allocRatio = inputVal;
  item.allocAmount = Number((props.model.subsidyTotal * inputVal).toFixed(2));

  rebalanceAllocations(list);
};

// （3）金额输入校验函数
const handleAmountInput = (index: number, event: Event) => {
  const target = event.target as HTMLInputElement;
  const list = [...(props.model.allocations || [])];
  const item = list[index];
  if (!item) return;

  const rawValue = Number(target.value);
  const totalAmount = props.model.subsidyTotal || 0;

  if (isNaN(rawValue) || rawValue < 0 || totalAmount === 0) {
    item.allocAmount = 0;
    item.allocRatio = 0;
    target.value = '0.00';
    rebalanceAllocations(list);
    return;
  }

  const inputVal = Number(rawValue.toFixed(2));
  target.value = inputVal.toFixed(2);

  let otherSum = 0;
  for (let i = 1; i < list.length; i++) {
    const otherItem = list[i];
    if (i !== index && otherItem) {
      otherSum += otherItem.allocAmount;
    }
  }

  if (otherSum + inputVal > totalAmount) {
    item.allocAmount = 0;
    item.allocRatio = 0;
    target.value = '0.00';
    rebalanceAllocations(list);
    return;
  }

  item.allocAmount = inputVal;
  item.allocRatio = Number((inputVal / totalAmount).toFixed(4));

  rebalanceAllocations(list);
};

// 均摊：把所有行平均分配比例和金额
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
    const splitRatio = Number((1.0 / count).toFixed(4));
    const splitAmount = Number((totalAmount / count).toFixed(2));

    for (let i = 1; i < count; i++) {
      const item = list[i];
      if (item) {
        item.allocRatio = splitRatio;
        item.allocAmount = splitAmount;
      }
    }

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
      first.allocRatio = Number((1.0 - sumRatios).toFixed(4));
      first.allocAmount = Number((totalAmount - sumAmounts).toFixed(2));
    }
  }

  emit('update-allocations', list);
};

// 把小数比例转成百分比字符串显示
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
          （分摊金额: {{ formatAmount(model.subsidyTotal) }}）
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
                    <span>分摊比例</span>
                    <button v-if="!isReadOnly" class="split-btn" @click.stop="handleEqualSplit">均摊</button>
                    <span class="required-star">*</span>
                  </div>
                </th>
                <th style="width: 180px;" class="text-right">分摊金额 *</th>
                <th v-if="!isReadOnly" style="width: 80px;" class="text-center">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!model.allocations || model.allocations.length === 0">
                <td :colspan="isReadOnly ? 5 : 6" class="empty-cell">
                  暂无分摊比例数据{{ isReadOnly ? '' : '，请点击下方进行“添加一行”' }}
                </td>
              </tr>
              <tr v-for="(alloc, idx) in model.allocations" :key="alloc.id">
                <td class="text-center font-mono">{{ Number(idx) + 1 }}</td>

                <td>
                  <FlatSelect
                    v-model="alloc.companyId"
                    :options="baseDataStore.companies"
                    placeholder="请选择费用归属公司"
                    :disabled="isReadOnly"
                  />
                </td>

                <td>
                  <FlatSelect
                    v-model="alloc.projectId"
                    :options="baseDataStore.projects"
                    placeholder="请选择归属项目（选填）"
                    :disabled="isReadOnly"
                  />
                </td>

                <td>
                  <div class="percent-input-wrapper">
                    <input
                      type="text"
                      inputmode="decimal"
                      :value="getRatioDisplay(alloc.allocRatio)"
                      :disabled="Number(idx) === 0 || isReadOnly"
                      class="text-right font-mono"
                      @change="handleRatioInput(Number(idx), $event)"
                    />
                    <span class="percent-symbol">%</span>
                  </div>
                </td>

                <td>
                  <input
                    type="text"
                    inputmode="decimal"
                    :value="alloc.allocAmount.toFixed(2)"
                    :disabled="Number(idx) === 0 || isReadOnly"
                    class="text-right font-mono"
                    @input="handleInputNumberLimit"
                    @change="handleAmountInput(Number(idx), $event)"
                  />
                </td>

                <td v-if="!isReadOnly" class="text-center">
                  <button
                    class="btn-text-action text-danger"
                    title="删除该行"
                    @click="handleDeleteClick(alloc.id)"
                  >
                    删除
                  </button>
                </td>
              </tr>

              <tr v-if="model.allocations && model.allocations.length > 0" class="totals-row">
                <td colspan="3" class="text-right font-bold">合计</td>
                <td class="text-right font-mono font-bold orange-text">
                  {{ (model.allocations.reduce((sum: number, a: CostAllocation) => sum + a.allocRatio, 0) * 100).toFixed(2) }}%
                </td>
                <td class="text-right font-mono font-bold orange-text">
                  {{ formatAmount(model.allocations.reduce((sum: number, a: CostAllocation) => sum + a.allocAmount, 0)) }}
                </td>
                <td v-if="!isReadOnly"></td>
              </tr>
            </tbody>
          </table>
        </div>

        <button v-if="!isReadOnly" class="add-row-btn" @click="handleAddRow">
          ⊕ 添加一行
        </button>
      </div>
    </transition>

    <ConfirmDialog
      :show="showConfirmDelete"
      title="提示"
      message="确定删除？"
      type="warning"
      @confirm="confirmDelete"
      @cancel="showConfirmDelete = false"
    />

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


.required-star {
  color: var(--text-secondary);
  font-weight: bold;
}

/* 覆盖全局,避免裁剪 FlatSelect 下拉菜单 */
.table-container {
  overflow: visible;
}
</style>
