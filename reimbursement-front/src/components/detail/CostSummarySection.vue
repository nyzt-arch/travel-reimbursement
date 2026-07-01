<script setup lang="ts">

import { formatAmount } from '../../utils/formatter';
import SectionHeader from '../common/SectionHeader.vue';

defineProps({
  model: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    required: true
  }
});
</script>

<template>
  <div class="detail-section card">
    <SectionHeader 
      title="费用合计" 
      :collapsed="collapsed" 
      @toggle="$emit('toggle')"
    />
    
    <transition name="collapse">
      <div v-show="!collapsed" class="section-content">
        <div class="summary-cards-grid">
          <!-- Total Card -->
          <div class="summary-card highlight-card">
            <div class="card-info-area">
              <span class="card-label">补助总金额</span>
              <span class="card-value font-mono">{{ formatAmount(model.subsidyTotal) }}</span>
            </div>
          </div>

          <!-- Meal Card -->
          <div class="summary-card">
            <div class="card-info-area">
              <span class="card-label">餐费补助</span>
              <span class="card-value font-mono">{{ formatAmount(model.mealSubsidyTotal) }}</span>
            </div>
          </div>

          <!-- Transit Card -->
          <div class="summary-card">
            <div class="card-info-area">
              <span class="card-label">交通补助</span>
              <span class="card-value font-mono">{{ formatAmount(model.transportSubsidyTotal) }}</span>
            </div>
          </div>

          <!-- Comm Card -->
          <div class="summary-card">
            <div class="card-info-area">
              <span class="card-label">通讯补助</span>
              <span class="card-value font-mono">{{ formatAmount(model.commSubsidyTotal) }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition>
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

.summary-cards-grid {
  display: flex;
  gap: 16px;
  width: 100%;
}

.summary-card {
  background-color: var(--bg-primary);
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-sm);
  padding: 8px 16px;
  display: flex;
  flex: 1;
  justify-content: center;
  align-items: center;
}

.summary-card.highlight-card {
  background-color: #fffbeb;
  border-color: #fde68a;
  box-shadow: 0 2px 4px -1px rgba(245, 158, 11, 0.05);
}

.card-info-area {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.card-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.summary-card.highlight-card .card-label {
  color: #b45309;
  font-weight: 500;
}

.card-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
}

.summary-card.highlight-card .card-value {
  color: #d97706;
  font-size: 14px;
}

.font-mono {
  font-family: monospace;
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
