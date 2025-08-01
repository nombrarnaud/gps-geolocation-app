<template>
  <div class="min-h-screen flex items-center justify-center bg-white">
    <div class="max-w-md w-full space-y-8 p-10 border border-gray-200 rounded-lg shadow-lg">
      <h2 class="text-center text-3xl font-bold tracking-tight text-black">Se connecter</h2>
      <form @submit.prevent="handleLogin" class="mt-8 space-y-6">
        <div class="rounded-md shadow-sm -space-y-px">
          <div>
            <label for="email" class="sr-only">Adresse email</label>
            <input id="email" name="email" type="email" autocomplete="email" required
              v-model="email"
              class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-black rounded-t-md focus:outline-none focus:ring-black focus:border-black focus:z-10 sm:text-sm"
              placeholder="Adresse email" />
          </div>
          <div>
            <label for="password" class="sr-only">Mot de passe</label>
            <input id="password" name="password" type="password" autocomplete="current-password" required
              v-model="password"
              class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-black rounded-b-md focus:outline-none focus:ring-black focus:border-black focus:z-10 sm:text-sm"
              placeholder="Mot de passe" />
          </div>
        </div>
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input id="rememberMe" name="rememberMe" type="checkbox" v-model="rememberMe"
              class="h-4 w-4 text-black focus:ring-black border-gray-300 rounded" />
            <label for="rememberMe" class="ml-2 block text-sm text-black">Se souvenir de moi</label>
          </div>
          <div class="text-sm">
            <a href="#" class="font-medium text-black hover:underline">Mot de passe oublié ?</a>
          </div>
        </div>
        <div>
          <button type="submit"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-black hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-black">
            Se connecter
          </button>
        </div>
        <div v-if="errorMessage" class="text-red-600 text-sm text-center">{{ errorMessage }}</div>
      </form>
      <p class="mt-2 text-center text-sm text-black">
        Pas encore de compte ?
        <router-link to="/register" class="font-medium text-black hover:underline">S'inscrire</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const email = ref('');
const password = ref('');
const rememberMe = ref(false);
const errorMessage = ref('');

const router = useRouter();

const handleLogin = async () => {
  errorMessage.value = '';
  try {
    const response = await axios.post('/api/auth/login', {
      email: email.value,
      password: password.value,
      rememberMe: rememberMe.value,
    });
    if (response.data.success) {
      localStorage.setItem('token', response.data.data.token);
      router.push('/');
    } else {
      errorMessage.value = response.data.message || 'Erreur lors de la connexion';
    }
  } catch (error) {
    errorMessage.value = 'Erreur lors de la connexion';
  }
};
</script>

<style scoped>
/* Add any scoped styles here */
</style>
