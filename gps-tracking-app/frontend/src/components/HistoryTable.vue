<template>
  <div>
    <h3 class="text-xl font-semibold mb-2 text-black">Historique des déplacements</h3>
    <table class="min-w-full divide-y divide-gray-200 border border-gray-300 rounded-md">
      <thead class="bg-gray-50">
        <tr>
          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Latitude</th>
          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Longitude</th>
          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Vitesse (km/h)</th>
          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Altitude (m)</th>
          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Poids (kg)</th>
        </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="history in histories" :key="history.id">
          <td class="px-4 py-2 whitespace-nowrap text-sm text-black">{{ formatDate(history.timestamp) }}</td>
          <td class="px-4 py-2 whitespace-nowrap text-sm text-black">{{ history.latitude.toFixed(6) }}</td>
          <td class="px-4 py-2 whitespace-nowrap text-sm text-black">{{ history.longitude.toFixed(6) }}</td>
          <td class="px-4 py-2 whitespace-nowrap text-sm text-black">{{ history.speed?.toFixed(2) ?? 'N/A' }}</td>
          <td class="px-4 py-2 whitespace-nowrap text-sm text-black">{{ history.altitude?.toFixed(2) ?? 'N/A' }}</td>
          <td class="px-4 py-2 whitespace-nowrap text-sm text-black">{{ history.weight?.toFixed(2) ?? 'N/A' }}</td>
        </tr>
      </tbody>
    </table>
    <PaginationComponent
      :currentPage="currentPage"
      :totalPages="totalPages"
      @page-changed="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import axios from 'axios';
import PaginationComponent from './PaginationComponent.vue';

interface History {
  id: number;
  timestamp: string;
  latitude: number;
  longitude: number;
  speed: number | null;
  altitude: number | null;
  weight: number | null;
}

const props = defineProps<{
  vehicleId: number;
}>();

const histories = ref<History[]>([]);
const currentPage = ref(0);
const totalPages = ref(0);

const fetchHistory = async (page = 0) => {
  try {
    const response = await axios.get(`/api/history/vehicle/${props.vehicleId}`, {
      params: { page, size: 10 },
    });
    if (response.data.success) {
      histories.value = response.data.data;
      currentPage.value = response.data.currentPage;
      totalPages.value = response.data.totalPages;
    }
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'historique', error);
  }
};

const handlePageChange = (page: number) => {
  fetchHistory(page);
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleString();
};

onMounted(() => {
  fetchHistory();
});

watch(() => props.vehicleId, () => {
  fetchHistory();
});
</script>

<style scoped>
/* Add any scoped styles here */
</style>
