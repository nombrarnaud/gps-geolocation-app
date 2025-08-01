<template>
  <div class="min-h-screen bg-white p-4">
    <h1 class="text-2xl font-bold mb-4 text-black">Détail de l'engin</h1>
    <div v-if="vehicle">
      <div class="mb-4">
        <h2 class="text-xl font-semibold">{{ vehicle.name }}</h2>
        <p>Vitesse: {{ vehicle.speed?.toFixed(2) ?? 'N/A' }} km/h</p>
        <p>Altitude: {{ vehicle.altitude?.toFixed(2) ?? 'N/A' }} m</p>
        <p>Poids: {{ vehicle.weight?.toFixed(2) ?? 'N/A' }} kg</p>
      </div>
      <HistoryTable :vehicleId="vehicle.id" />
    </div>
    <div v-else>
      <p>Chargement des données...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import HistoryTable from '../components/HistoryTable.vue';

interface Vehicle {
  id: number;
  name: string;
  speed: number;
  altitude: number;
  weight: number;
}

const route = useRoute();
const vehicle = ref<Vehicle | null>(null);

const fetchVehicle = async () => {
  try {
    const response = await axios.get(`/api/vehicles/${route.params.id}`);
    if (response.data.success) {
      vehicle.value = response.data.data;
    }
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'engin', error);
  }
};

onMounted(() => {
  fetchVehicle();
});
</script>

<style scoped>
/* Add any scoped styles here */
</style>
