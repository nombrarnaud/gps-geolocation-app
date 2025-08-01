<template>
  <div class="flex justify-center mt-4 space-x-2">
    <button
      :disabled="currentPage === 0"
      @click="$emit('page-changed', currentPage - 1)"
      class="px-3 py-1 rounded border border-gray-300 bg-white text-black disabled:opacity-50"
    >
      Précédent
    </button>
    <button
      v-for="page in totalPages"
      :key="page"
      :class="['px-3 py-1 rounded border', page - 1 === currentPage ? 'bg-black text-white' : 'bg-white text-black']"
      @click="$emit('page-changed', page - 1)"
    >
      {{ page }}
    </button>
    <button
      :disabled="currentPage === totalPages - 1"
      @click="$emit('page-changed', currentPage + 1)"
      class="px-3 py-1 rounded border border-gray-300 bg-white text-black disabled:opacity-50"
    >
      Suivant
    </button>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  currentPage: number;
  totalPages: number;
}>();
</script>

<style scoped>
button {
  cursor: pointer;
}
button:disabled {
  cursor: not-allowed;
}
</style>
