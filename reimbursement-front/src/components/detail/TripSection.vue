<script setup lang="ts">
import { ref } from 'vue';
import type { Trip } from '../../types';
import { useBaseDataStore } from '../../stores/baseData';
import SectionHeader from '../common/SectionHeader.vue';
import TripDialog from './TripDialog.vue';
import ConfirmDialog from '../ConfirmDialog.vue';

const props = defineProps({
  model: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['toggle', 'update-trips']);

const baseDataStore = useBaseDataStore();

// Modal triggers
const showDialog = ref(false);
const editingTrip = ref<Trip | null>(null);
const showConfirmDelete = ref(false);
const tripToDeleteId = ref<string | null>(null);

const openAddDialog = () => {
  editingTrip.value = null;
  showDialog.value = true;
};

const openEditDialog = (trip: Trip) => {
  editingTrip.value = trip;
  showDialog.value = true;
};

const handleCopyTrip = (trip: Trip) => {
  const copied = {
    ...JSON.parse(JSON.stringify(trip)),
    id: `T_NEW_${Date.now()}`
  };
  
  // trigger addition
  const newTrips = [...(props.model.trips || []), copied];
  emit('update-trips', newTrips);
};

const handleDeleteClick = (id: string) => {
  tripToDeleteId.value = id;
  showConfirmDelete.value = true;
};

const confirmDelete = () => {
  if (tripToDeleteId.value) {
    const newTrips = (props.model.trips || []).filter((t: Trip) => t.id !== tripToDeleteId.value);
    emit('update-trips', newTrips, tripToDeleteId.value); // Pass deleted ID to trigger subsidy removal
    tripToDeleteId.value = null;
  }
  showConfirmDelete.value = false;
};

const cancelDelete = () => {
  tripToDeleteId.value = null;
  showConfirmDelete.value = false;
};

const handleSaveTrip = (tripForm: Trip) => {
  const newTrips = [...(props.model.trips || [])];
  
  if (tripForm.id) {
    // Edit existing
    const idx = newTrips.findIndex(t => t.id === tripForm.id);
    if (idx !== -1) {
      newTrips[idx] = tripForm;
    }
  } else {
    // New item
    tripForm.id = `T_NEW_${Date.now()}`;
    tripForm.reimId = props.model.id;
    newTrips.push(tripForm);
  }
  
  emit('update-trips', newTrips, null, tripForm);
  showDialog.value = false;
};
</script>

<template>
  <div class="detail-section card">
    <SectionHeader 
      title="补录行程" 
      :collapsed="collapsed" 
      @toggle="emit('toggle')"
    >
      <template #actions>
        <button 
          v-show="!collapsed" 
          class="btn-add-text" 
          @click.stop="openAddDialog"
        >
          ⊕ 补录行程
        </button>
      </template>
    </SectionHeader>
    
    <transition name="collapse">
      <div v-show="!collapsed" class="section-content">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th style="width: 60px;" class="text-center">序号</th>
                <th style="width: 160px;">出行人员</th>
                <th style="width: 220px;">出差日期</th>
                <th style="width: 180px;">行程</th>
                <th>行程说明</th>
                <th style="width: 110px;" class="text-center">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!model.trips || model.trips.length === 0">
                <td colspan="6" class="empty-cell">
                  暂无行程信息，请点击右上角进行“补录行程”
                </td>
              </tr>
              <tr v-for="(trip, idx) in model.trips" :key="trip.id">
                <td class="text-center font-mono">{{ Number(idx) + 1 }}</td>
                <td>{{ baseDataStore.getEmployeeNameById(trip.travelerId) }}</td>
                <td class="font-mono">{{ trip.departDate }} 至 {{ trip.arriveDate }}</td>
                <td>
                  {{ baseDataStore.getCityNameByNo(trip.departCityNo) }} - 
                  {{ baseDataStore.getCityNameByNo(trip.arriveCityNo) }}
                </td>
                <td class="trip-desc-cell" :title="trip.tripDesc">{{ trip.tripDesc }}</td>
                <td class="text-center">
                  <div class="row-actions">
                    <button class="icon-btn" title="编辑" @click="openEditDialog(trip)">✏️</button>
                    <button class="icon-btn" title="复制" @click="handleCopyTrip(trip)">📋</button>
                    <button class="icon-btn text-danger" title="删除" @click="handleDeleteClick(trip.id)">🗑️</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </transition>

    <!-- Trip Form dialog -->
    <TripDialog 
      :show="showDialog"
      :trip-data="editingTrip"
      @save="handleSaveTrip"
      @cancel="showDialog = false"
    />

    <!-- Delete Confirmation -->
    <ConfirmDialog 
      :show="showConfirmDelete"
      title="提示"
      message="确认删除该行程吗？关联的补助及日历信息也将被联动删除。"
      type="danger"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
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

.btn-add-text {
  background: transparent;
  border: none;
  color: var(--primary-color);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
}

.btn-add-text:hover {
  background-color: var(--primary-light);
  color: var(--primary-hover);
}

.empty-cell {
  text-align: center;
  color: var(--text-muted);
  padding: 30px 0 !important;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.icon-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 4px;
  border-radius: var(--radius-sm);
}

.icon-btn:hover {
  background-color: var(--gray-100);
}

.icon-btn.text-danger:hover {
  background-color: var(--danger-light);
}

.trip-desc-cell {
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.font-mono {
  font-family: monospace;
}

/* Section collapse animation */
.collapse-enter-active,
.collapse-leave-active {
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1), padding 0.3s ease;
  max-height: 800px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
