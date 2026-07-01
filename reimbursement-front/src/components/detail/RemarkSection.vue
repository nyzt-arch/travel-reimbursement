<script setup lang="ts">
/* eslint-disable vue/no-mutating-props */
import { ref } from 'vue';
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
  },
  isReadOnly: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['toggle', 'update-remark']);

const showConfirmDelete = ref(false);

const handleDeleteClick = () => {
  if (!props.model.remark) return;
  showConfirmDelete.value = true;
};

const confirmDelete = () => {
  emit('update-remark', '');
  showConfirmDelete.value = false;
};
</script>

<template>
  <div class="detail-section card">
    <SectionHeader 
      title="备注信息" 
      :collapsed="collapsed" 
      @toggle="emit('toggle')"
    >
      <template #actions>
        <button 
          v-show="!collapsed && !isReadOnly" 
          class="btn-text-action text-danger" 
          :disabled="!model.remark"
          @click.stop="handleDeleteClick"
        >
          删除备注
        </button>
      </template>
    </SectionHeader>
    
    <transition name="collapse">
      <div v-show="!collapsed" class="section-content">
        <div class="textarea-wrapper">
          <textarea 
            v-model="model.remark" 
            placeholder="请输入备注说明内容" 
            rows="4"
            :disabled="isReadOnly"
          ></textarea>
        </div>
      </div>
    </transition>

    <!-- Delete remark confirm dialog -->
    <ConfirmDialog 
      :show="showConfirmDelete"
      title="提示"
      message="确认删除？"
      type="danger"
      @confirm="confirmDelete"
      @cancel="showConfirmDelete = false"
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

.textarea-wrapper {
  position: relative;
  width: 100%;
}

/* Section collapse animation */
.collapse-enter-active,
.collapse-leave-active {
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1), padding 0.3s ease;
  max-height: 200px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
