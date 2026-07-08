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
    default: 'info'  // info=信息, warning=警告, success=成功, danger=危险
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
            <!-- 警告图标：黄色圆形 + 白色感叹号 -->
            <svg v-if="type === 'warning'" class="dialog-icon-svg" viewBox="0 0 40 40">
              <circle cx="20" cy="20" r="19" fill="#f59e0b" stroke="#f59e0b" stroke-width="2"/>
              <text x="20" y="27" text-anchor="middle" fill="white" font-size="24" font-weight="700" font-family="sans-serif">!</text>
            </svg>
            <!-- 成功图标：绿色圆形 + 白色对勾 -->
            <svg v-else-if="type === 'success'" class="dialog-icon-svg" viewBox="0 0 40 40">
              <circle cx="20" cy="20" r="19" fill="#10b981" stroke="#10b981" stroke-width="2"/>
              <polyline points="10,20 17,27 30,13" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <!-- 危险图标：红色圆形 + 白色叉号 -->
            <svg v-else-if="type === 'danger'" class="dialog-icon-svg" viewBox="0 0 40 40">
              <circle cx="20" cy="20" r="19" fill="#ef4444" stroke="#ef4444" stroke-width="2"/>
              <line x1="13" y1="13" x2="27" y2="27" stroke="white" stroke-width="3" stroke-linecap="round"/>
              <line x1="27" y1="13" x2="13" y2="27" stroke="white" stroke-width="3" stroke-linecap="round"/>
            </svg>
            <!-- 信息图标：蓝色圆形 + 白色字母 i -->
            <svg v-else class="dialog-icon-svg" viewBox="0 0 40 40">
              <circle cx="20" cy="20" r="19" fill="#3b82f6" stroke="#3b82f6" stroke-width="2"/>
              <text x="20" y="27" text-anchor="middle" fill="white" font-size="24" font-weight="700" font-family="sans-serif">i</text>
            </svg>
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
              'btn-primary': type === 'info' || type === 'success' || type === 'warning',
              'btn-danger': type === 'danger'
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

.header-warning {
  border-bottom-color: var(--warning-border);
}

.header-danger {
  border-bottom-color: var(--danger-border);
}

.dialog-body {
  padding: 24px 20px;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.dialog-icon-wrapper {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-round);
  flex-shrink: 0;
}

.dialog-icon-svg {
  width: 40px;
  height: 40px;
  display: block;
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
