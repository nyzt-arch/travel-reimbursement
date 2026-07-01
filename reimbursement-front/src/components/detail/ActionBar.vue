<script setup lang="ts">


defineProps({
  saving: {
    type: Boolean,
    default: false
  },
  submitting: {
    type: Boolean,
    default: false
  },
  isReadOnly: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'save', 'submit']);
</script>

<template>
  <footer class="action-bar-footer">
    <div class="action-container">
      <div class="left-actions">
        <button class="btn btn-secondary" @click="emit('close')">
          <span>❌</span> 关闭
        </button>
      </div>
      
      <div v-if="!isReadOnly" class="right-actions">
        <button 
          class="btn btn-secondary btn-save" 
          :disabled="saving || submitting"
          @click="emit('save')"
        >
          <span>💾</span> 保存草稿
        </button>
        
        <button 
          class="btn btn-primary btn-submit" 
          :disabled="saving || submitting"
          @click="emit('submit')"
        >
          <span>🚀</span> 确认提交
        </button>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.action-bar-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: var(--footer-height);
  background-color: var(--bg-card);
  border-top: 1px solid var(--gray-200);
  box-shadow: 0 -4px 10px -2px rgba(0, 0, 0, 0.05);
  z-index: 100;
  display: flex;
  align-items: center;
}

.action-container {
  width: 100%;
  max-width: var(--content-width);
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.right-actions {
  display: flex;
  gap: 12px;
}

.btn-save {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.btn-save:hover {
  background-color: var(--primary-light);
}

.btn-submit {
  background-color: #2563eb;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
