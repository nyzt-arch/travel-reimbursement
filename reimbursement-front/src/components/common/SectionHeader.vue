<script setup lang="ts">


defineProps({
  title: {
    type: String,
    required: true
  },
  collapsible: {
    type: Boolean,
    default: true
  },
  collapsed: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['toggle']);
</script>

<template>
  <div class="section-header" :class="{ 'collapsible': collapsible }" @click="collapsible && emit('toggle')">
    <div class="header-left">
      <h3 class="header-title">{{ title }}</h3>
      <slot name="summary"></slot>
    </div>
    
    <div class="header-right" @click.stop>
      <slot name="actions"></slot>
      <span v-if="collapsible" class="collapse-icon" :class="{ 'collapsed': collapsed }" @click="emit('toggle')">
        ▼
      </span>
    </div>
  </div>
</template>

<style scoped>
.section-header {
  height: 48px;
  padding: 0 16px;
  background-color: var(--gray-50);
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: space-between;
  user-select: none;
  border-top-left-radius: var(--radius-md);
  border-top-right-radius: var(--radius-md);
}

.section-header.collapsible {
  cursor: pointer;
}

.section-header.collapsible:hover {
  background-color: var(--gray-100);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-icon {
  font-size: 10px;
  color: var(--text-secondary);
  transition: transform var(--transition-fast);
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-round);
}

.collapse-icon:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.collapse-icon.collapsed {
  transform: rotate(-90deg);
}
</style>
