<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';

interface Option {
  id: string;
  name: string;
}

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  options: {
    type: Array as () => Option[],
    required: true,
  },
  placeholder: {
    type: String,
    default: '请选择',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue', 'change']);

const isOpen = ref(false);
const containerRef = ref<HTMLElement | null>(null);

const selectedLabel = computed(() => {
  if (!props.modelValue) return '';
  const item = props.options.find((o) => o.id === props.modelValue);
  return item ? item.name : '';
});

const toggleDropdown = () => {
  if (props.disabled) return;
  isOpen.value = !isOpen.value;
};

const closeDropdown = () => {
  isOpen.value = false;
};

const handleClickOutside = (event: MouseEvent) => {
  if (containerRef.value && !containerRef.value.contains(event.target as Node)) {
    closeDropdown();
  }
};

const handleSelect = (option: Option) => {
  emit('update:modelValue', option.id);
  emit('change', option.id);
  closeDropdown();
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <div class="flat-select-container" ref="containerRef">
    <div
      class="select-trigger"
      :class="{ open: isOpen, disabled: disabled }"
      @click="toggleDropdown"
    >
      <span v-if="selectedLabel" class="selected-text">{{ selectedLabel }}</span>
      <span v-else class="placeholder-text">{{ placeholder }}</span>
      <span class="chevron-icon"></span>
    </div>

    <transition name="slide-fade">
      <div v-if="isOpen" class="dropdown-menu">
        <ul class="option-list">
          <li
            v-for="option in options"
            :key="option.id"
            class="option-item"
            :class="{ selected: modelValue === option.id }"
            @click="handleSelect(option)"
          >
            <span class="option-content">{{ option.name }}</span>
          </li>
        </ul>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.flat-select-container {
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
  max-height: 240px;
  overflow-y: auto;
  z-index: 500;
  padding: 4px 0;
}

.option-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  font-size: 14px;
  color: var(--text-primary);
  cursor: pointer;
  user-select: none;
  transition: background-color 0.15s ease;
}

.option-item:hover {
  background-color: var(--primary-light);
  color: var(--primary-color);
}

.option-item.selected {
  background-color: var(--primary-color);
  color: #ffffff;
  font-weight: 500;
}

.option-content {
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
