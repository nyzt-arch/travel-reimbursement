<!-- eslint-disable vue/multi-word-component-names -->
<script setup lang="ts">
import { ref, computed } from 'vue';
import FlatSelect from './FlatSelect.vue';

const props = defineProps({
  total: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    default: 10
  },
  currentPage: {
    type: Number,
    default: 1
  }
});

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'change']);

const pageSizeOptions = [
  { id: '10', name: '10 条/页' },
  { id: '20', name: '20 条/页' },
  { id: '50', name: '50 条/页' },
];


// 生成要显示的页码列表
const pages = computed(() => {
  const current = props.currentPage;
  const total = Math.ceil(props.total / props.pageSize) || 1;
  const list: (number | string)[] = [];

  if (total <= 7) {
    for (let i = 1; i <= total; i++) list.push(i);
  } else {
    // 首页始终显示
    list.push(1);

    if (current > 4) {
      list.push('...');
    }

    const start = Math.max(2, current - 2);
    const end = Math.min(total - 1, current + 2);

    for (let i = start; i <= end; i++) {
      list.push(i);
    }

    if (current < total - 3) {
      list.push('...');
    }

    // 末页始终显示
    list.push(total);
  }

  return list;
});

const jumpPageVal = ref('');

const handlePageClick = (page: number | string) => {
  if (typeof page === 'number' && page !== props.currentPage) {
    emit('update:currentPage', page);
    emit('change', { page, pageSize: props.pageSize });
  }
};

const handlePageSizeChange = (value: string) => {
  const newSize = Number(value);
  emit('update:pageSize', newSize);
  emit('update:currentPage', 1); // 切到第一页
  emit('change', { page: 1, pageSize: newSize });
};

const handlePrev = () => {
  if (props.currentPage > 1) {
    handlePageClick(props.currentPage - 1);
  }
};

const handleNext = () => {
  const total = Math.ceil(props.total / props.pageSize) || 1;
  if (props.currentPage < total) {
    handlePageClick(props.currentPage + 1);
  }
};

const handleJump = () => {
  const page = parseInt(jumpPageVal.value, 10);
  const total = Math.ceil(props.total / props.pageSize) || 1;
  if (!isNaN(page) && page >= 1 && page <= total) {
    handlePageClick(page);
    jumpPageVal.value = '';
  }
};
</script>

<template>
  <div class="pagination-container">
    <div class="page-controls">
      <div class="page-info">
        共{{ total }}条
      </div>
      <!-- Page Size Selector -->
      <FlatSelect
        :modelValue="String(pageSize)"
        :options="pageSizeOptions"
        class="size-select"
        @change="handlePageSizeChange"
      />

      <!-- Navigation Arrows and Numbers -->
      <div class="page-list">
        <button
          class="page-btn arrow-btn"
          :disabled="currentPage === 1"
          @click="handlePrev"
        >
          &lt;
        </button>

        <button
          v-for="(page, index) in pages"
          :key="index"
          class="page-btn"
          :class="{
            'active': page === currentPage,
            'ellipsis-btn': page === '...'
          }"
          :disabled="page === '...'"
          @click="handlePageClick(page)"
        >
          {{ page }}
        </button>

        <button
          class="page-btn arrow-btn"
          :disabled="currentPage === Math.ceil(total / pageSize)"
          @click="handleNext"
        >
          &gt;
        </button>
      </div>

      <!-- Quick Jump -->
      <div class="quick-jump">
        <span>前往</span>
        <input
          type="text"
          v-model="jumpPageVal"
          @keyup.enter="handleJump"
          class="jump-input"
        />
        <span>页</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 16px 0px;
  font-size: 14px;
  color: var(--text-secondary);
}

.page-info {
  margin-right:16px;
}

.highlight {
  color: var(--primary-color);
  font-weight: 600;
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.size-select {
  width: 110px;
}

.page-list {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-btn {
  background-color: var(--bg-card);
  border: 1px solid var(--gray-300);
  color: var(--text-secondary);
  min-width: 32px;
  height: 32px;
  padding: 0 6px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: monospace;
  font-size: 13px;
  transition: var(--transition-fast);
  user-select: none;
}

.page-btn:hover:not(:disabled):not(.active) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.page-btn.active {
  background-color: var(--bg-card);
  border-color: var(--primary-color);
  color: var(--primary-color);
  font-weight: 600;
}

.page-btn.ellipsis-btn {
  border: none;
  cursor: default;
  background: transparent;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background-color: var(--gray-50);
}

.quick-jump {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.jump-input {
  width: 44px;
  height: 32px;
  padding: 4px;
  text-align: center;
  border: 1px solid var(--gray-300);
  border-radius: var(--radius-sm);
}
</style>
