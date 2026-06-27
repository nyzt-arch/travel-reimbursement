<script setup lang="ts">


defineProps({
  show: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: '提示'
  },
  message: {
    type: String,
    required: true
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  showCancel: {
    type: Boolean,
    default: true
  },
  type: {
    type: String,
    default: 'info' // info, warning, success, danger
  }
});

const emit = defineEmits(['confirm', 'cancel']);
</script>

<template>
  <transition name="dialog-fade">
    <div v-if="show" class="dialog-backdrop" @click="showCancel && emit('cancel')">
      <div class="dialog-box" @click.stop>
        <!-- Header -->
        <div class="dialog-header" :class="`header-${type}`">
          <span class="dialog-title">{{ title }}</span>
          <button v-if="showCancel" class="close-btn" @click="emit('cancel')">×</button>
        </div>
        
        <!-- Content Body -->
        <div class="dialog-body">
          <div class="dialog-icon-wrapper" :class="`icon-${type}`">
            <span v-if="type === 'warning'" class="dialog-icon">⚠️</span>
            <span v-else-if="type === 'success'" class="dialog-icon">✅</span>
            <span v-else-if="type === 'danger'" class="dialog-icon">🚨</span>
            <span v-else class="dialog-icon">ℹ️</span>
          </div>
          <p class="dialog-message">{{ message }}</p>
        </div>

        <!-- Footer Actions -->
        <div class="dialog-footer">
          <button 
            v-if="showCancel" 
            class="btn btn-secondary" 
            @click="emit('cancel')"
          >
            {{ cancelText }}
          </button>
          <button 
            class="btn" 
            :class="{
              'btn-primary': type === 'info' || type === 'success',
              'btn-danger': type === 'danger',
              'btn-warning': type === 'warning'
            }"
            @click="emit('confirm')"
          >
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
.dialog-box {
  width: 400px;
  max-width: 90%;
}

.dialog-header {
  padding: 14px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--gray-200);
}

.header-warning {
  border-bottom-color: var(--warning-border);
}

.header-danger {
  border-bottom-color: var(--danger-border);
}

.dialog-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  color: var(--text-muted);
  cursor: pointer;
  line-height: 1;
}

.close-btn:hover {
  color: var(--text-secondary);
}

.dialog-body {
  padding: 24px 20px;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.dialog-icon-wrapper {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-round);
}

.icon-info {
  background-color: var(--primary-light);
}

.icon-success {
  background-color: var(--success-light);
}

.icon-warning {
  background-color: var(--warning-light);
}

.icon-danger {
  background-color: var(--danger-light);
}

.dialog-message {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
  flex: 1;
  padding-top: 8px;
}

.dialog-footer {
  padding: 12px 20px;
  background-color: var(--gray-50);
  border-top: 1px solid var(--gray-200);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-warning {
  background-color: var(--warning-color);
  color: #ffffff;
}

.btn-warning:hover {
  background-color: #d97706;
}

/* Transitions */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.2s ease;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}
</style>
