<template>
  <div class="min-h-screen bg-white p-4">
    <h1 class="text-2xl font-bold mb-4 text-black">Tableau de bord des engins</h1>
    <MapComponent :vehicles="vehicles" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import MapComponent from '../components/MapComponent.vue';

interface Vehicle {
  id: number;
  name: string;
  currentLatitude: number;
  currentLongitude: number;
  speed: number;
  altitude: number;
  weight: number;
}

const vehicles = ref<Vehicle[]>([]);

const fetchVehicles = async () => {
  try {
    const response = await axios.get('/api/vehicles');
    if (response.data.success) {
      vehicles.value = response.data.data;
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des engins', error);
  }
};

onMounted(() => {
  fetchVehicles();
});
</script>

<style scoped>
/* Add any scoped styles here */
</style>
