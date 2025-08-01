<template>
  <div id="map" class="w-full h-[600px] rounded-md shadow-md"></div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref } from 'vue';
import L from 'leaflet';

interface Vehicle {
  id: number;
  name: string;
  currentLatitude: number;
  currentLongitude: number;
  speed: number;
  altitude: number;
  weight: number;
}

const props = defineProps<{
  vehicles: Vehicle[];
}>();

const map = ref<L.Map | null>(null);
const markers = ref<L.Marker[]>([]);

onMounted(() => {
  map.value = L.map('map').setView([48.8566, 2.3522], 12); // Default to Paris

  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
  }).addTo(map.value);
});

watch(
  () => props.vehicles,
  (newVehicles) => {
    if (!map.value) return;

    // Remove existing markers
    markers.value.forEach((marker) => marker.remove());
    markers.value = [];

    newVehicles.forEach((vehicle) => {
      if (
        vehicle.currentLatitude !== null &&
        vehicle.currentLongitude !== null
      ) {
        const marker = L.marker([vehicle.currentLatitude, vehicle.currentLongitude]).addTo(map.value!);
        marker.bindPopup(
          `<div>
            <strong>${vehicle.name}</strong><br/>
            Vitesse: ${vehicle.speed?.toFixed(2) ?? 'N/A'} km/h<br/>
            Altitude: ${vehicle.altitude?.toFixed(2) ?? 'N/A'} m<br/>
            Poids: ${vehicle.weight?.toFixed(2) ?? 'N/A'} kg
          </div>`
        );
        markers.value.push(marker);
      }
    });
  },
  { immediate: true }
);
</script>

<style scoped>
#map {
  width: 100%;
  height: 600px;
}
</style>
