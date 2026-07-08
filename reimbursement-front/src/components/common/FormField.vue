<script setup lang="ts">


defineProps({
  label: {
    type: String,
    required: true
  },
  required: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  inline: {
    type: Boolean,
    default: false
  }
});
</script>

<template>
  <div class="form-field" :class="{ 'inline': inline, 'has-error': error }">
    <label class="field-label">
      {{ label }}
    </label>
    <div class="field-content">
      <slot></slot>
      <transition name="fade">
        <span v-if="error" class="error-msg">{{ error }}</span>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
}

.form-field.inline {
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

.form-field.inline .field-label {
  width: 96px;
  text-align: left;
  margin-bottom: 0;
  flex-shrink: 0;
  white-space: nowrap;
}

.form-field.inline .field-content {
  flex: 1;
}

.field-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.required-star {
  color: var(--danger-color);
  margin-right: 4px;
  font-weight: bold;
}

.field-content {
  position: relative;
  width: 100%;
}

.error-msg {
  color: var(--danger-color);
  font-size: 12px;
  margin-top: 4px;
  display: block;
}

.has-error input,
.has-error select,
.has-error textarea {
  border-color: var(--danger-color) !important;
}

.has-error input:focus,
.has-error select:focus,
.has-error textarea:focus {
  border-color: var(--danger-hover) !important;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
