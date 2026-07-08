<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import type { BusinessType } from '../types';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  options: {
    type: Array as () => BusinessType[],
    required: true
  },
  placeholder: {
    type: String,
    default: '请选择业务类型'
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'change']);

const isOpen = ref(false);
const treeRef = ref<HTMLElement | null>(null);

// 在树结构中递归查找指定 id 的节点
const findNode = (nodes: BusinessType[], id: string): BusinessType | null => {
  for (const node of nodes) {
    if (node.id === id) return node;
    if (node.children) {
      const found = findNode(node.children, id);
      if (found) return found;
    }
  }
  return null;
};

// 显示当前选中节点的名称
const selectedLabel = computed(() => {
  if (!props.modelValue) return '';
  const node = findNode(props.options, props.modelValue);
  return node ? node.name : '';
});

const toggleDropdown = () => {
  if (props.disabled) return;
  isOpen.value = !isOpen.value;
};

const closeDropdown = () => {
  isOpen.value = false;
};

// 点击组件外部时关闭下拉
const handleClickOutside = (event: MouseEvent) => {
  if (treeRef.value && !treeRef.value.contains(event.target as Node)) {
    closeDropdown();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

// 选中一个节点（只有叶子节点可以选中）
const handleSelectNode = (node: BusinessType) => {
  // 有子节点的只做展示，不能选中
  if (node.thereSubordinateNode === '1') {
    return;
  }
  emit('update:modelValue', node.id);
  emit('change', node.id);
  closeDropdown();
};
</script>

<template>
  <div class="tree-select-container" ref="treeRef">
    <!-- Select Input Trigger -->
    <div
      class="select-trigger"
      :class="{ 'open': isOpen, 'disabled': disabled }"
      @click="toggleDropdown"
    >
      <span v-if="selectedLabel" class="selected-text">{{ selectedLabel }}</span>
      <span v-else class="placeholder-text">{{ placeholder }}</span>
      <span class="chevron-icon"></span>
    </div>

    <!-- Dropdown Menu Overlay -->
    <transition name="slide-fade">
      <div v-if="isOpen" class="dropdown-menu">
        <ul class="tree-list">
          <template v-for="node in options" :key="node.id">
            <!-- Level 1 Node -->
            <li class="tree-node parent-node">
              <span class="node-content font-bold">{{ node.name }}</span>
            </li>
            
            <template v-if="node.children">
              <template v-for="subNode in node.children" :key="subNode.id">
                <!-- Level 2 Node -->
                <li 
                  class="tree-node"
                  :class="{ 
                    'leaf-node': subNode.thereSubordinateNode === '0', 
                    'parent-node': subNode.thereSubordinateNode === '1',
                    'selected': modelValue === subNode.id
                  }"
                  @click="handleSelectNode(subNode)"
                >
                  <span class="indent-1"></span>
                  <span class="node-content">{{ subNode.name }}</span>
                </li>

                <template v-if="subNode.children">
                  <template v-for="leafNode in subNode.children" :key="leafNode.id">
                    <!-- Level 3 Node -->
                    <li 
                      class="tree-node leaf-node"
                      :class="{ 'selected': modelValue === leafNode.id }"
                      @click="handleSelectNode(leafNode)"
                    >
                      <span class="indent-2"></span>
                      <span class="node-content">{{ leafNode.name }}</span>
                    </li>
                  </template>
                </template>
              </template>
            </template>
          </template>
        </ul>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.tree-select-container {
  position: relative;
  width: 100%;
}

.select-trigger {
  width: 100%;
  padding: 8px 12px;
  background-color: var(--bg-card);
  border: 1px solid var(--gray-300);
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  transition: var(--transition-fast);
  user-select: none;
}

.select-trigger:hover:not(.disabled) {
  border-color: var(--gray-400);
}

.select-trigger.open {
  border-color: var(--gray-500);
}

.select-trigger.disabled {
  background-color: var(--gray-100);
  color: var(--text-muted);
  cursor: not-allowed;
  border-color: var(--gray-200);
}

.placeholder-text {
  color: var(--text-muted);
}

.selected-text {
  color: var(--text-primary);
}

.chevron-icon {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-right: 2px solid var(--text-muted);
  border-bottom: 2px solid var(--text-muted);
  transform: rotate(45deg);
  transition: transform var(--transition-fast);
}

.select-trigger.open .chevron-icon {
  transform: rotate(225deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  margin-top: 4px;
  background-color: var(--bg-card);
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-lg);
  max-height: 280px;
  overflow-y: auto;
  z-index: 500;
  padding: 4px 0;
}

.tree-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tree-node {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  font-size: 14px;
  color: var(--text-primary);
  user-select: none;
  cursor: default;
}

.tree-node.parent-node {
  color: var(--text-secondary);
  background-color: var(--gray-50);
}

.tree-node.leaf-node {
  cursor: pointer;
}

.tree-node.leaf-node:hover {
  background-color: var(--primary-light);
  color: var(--primary-color);
}

.tree-node.leaf-node.selected {
  background-color: var(--primary-color);
  color: #ffffff;
  font-weight: 500;
}

.font-bold {
  font-weight: 600;
}

.indent-1 {
  width: 16px;
  flex-shrink: 0;
}

.indent-2 {
  width: 32px;
  flex-shrink: 0;
}

.node-content {
  flex: 1;
}

/* Animations */
.slide-fade-enter-active {
  transition: all 0.2s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.15s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-8px);
  opacity: 0;
}
</style>
