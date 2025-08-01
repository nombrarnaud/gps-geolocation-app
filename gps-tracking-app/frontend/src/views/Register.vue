<template>
  <div class="min-h-screen flex items-center justify-center bg-white">
    <div class="max-w-md w-full space-y-8 p-10 border border-gray-200 rounded-lg shadow-lg">
      <h2 class="text-center text-3xl font-bold tracking-tight text-black">S'inscrire</h2>
      <form @submit.prevent="handleRegister" class="mt-8 space-y-6">
        <div class="flex justify-center mb-4">
          <button type="button" :class="['px-4 py-2 rounded-l-md', userType === 'BUSINESS' ? 'bg-black text-white' : 'bg-gray-200 text-black']" @click="userType = 'BUSINESS'">Entreprise</button>
          <button type="button" :class="['px-4 py-2 rounded-r-md', userType === 'SIMPLE' ? 'bg-black text-white' : 'bg-gray-200 text-black']" @click="userType = 'SIMPLE'">Simple</button>
        </div>
        <div v-if="userType === 'BUSINESS'">
          <input v-model="companyName" type="text" placeholder="Nom de l'entreprise" required class="input" />
          <input v-model="registrationNumber" type="text" placeholder="Numéro d'enregistrement" required class="input" />
          <input v-model="phone" type="tel" placeholder="Numéro de téléphone" required class="input" />
          <input v-model="managerName" type="text" placeholder="Nom complet du gérant" required class="input" />
          <input v-model="email" type="email" placeholder="Adresse mail" required class="input" />
          <input v-model="password" type="password" placeholder="Mot de passe" required class="input" />
        </div>
        <div v-else>
          <input v-model="phone" type="tel" placeholder="Numéro de téléphone" required class="input" />
          <input v-model="idCardNumber" type="text" placeholder="Numéro de carte d'identité" required class="input" />
          <input v-model="fullName" type="text" placeholder="Nom complet" required class="input" />
          <input v-model="email" type="email" placeholder="Adresse mail" required class="input" />
          <input v-model="password" type="password" placeholder="Mot de passe" required class="input" />
        </div>
        <div>
          <button type="submit" class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-black hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-black">
            S'inscrire
          </button>
        </div>
        <div v-if="errorMessage" class="text-red-600 text-sm text-center">{{ errorMessage }}</div>
      </form>
      <p class="mt-2 text-center text-sm text-black">
        Déjà un compte ?
        <router-link to="/login" class="font-medium text-black hover:underline">Se connecter</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const userType = ref<'BUSINESS' | 'SIMPLE'>('BUSINESS');
const companyName = ref('');
const registrationNumber = ref('');
const phone = ref('');
const managerName = ref('');
const idCardNumber = ref('');
const fullName = ref('');
const email = ref('');
const password = ref('');
const errorMessage = ref('');

const router = useRouter();

const handleRegister = async () => {
  errorMessage.value = '';
  try {
    const payload: any = {
      userType: userType.value,
      phone: phone.value,
      email: email.value,
      password: password.value,
    };
    if (userType.value === 'BUSINESS') {
      payload.companyName = companyName.value;
      payload.registrationNumber = registrationNumber.value;
      payload.managerName = managerName.value;
    } else {
      payload.idCardNumber = idCardNumber.value;
      payload.fullName = fullName.value;
    }
    const response = await axios.post('/api/auth/register', payload);
    if (response.data.success) {
      router.push('/login');
    } else {
      errorMessage.value = response.data.message || 'Erreur lors de l\'inscription';
    }
  } catch (error) {
    errorMessage.value = 'Erreur lors de l\'inscription';
  }
};
</script>

<style scoped>
.input {
  appearance: none;
  border: 1px solid #d1d5db;
  padding: 0.5rem 0.75rem;
  margin-bottom: 0.75rem;
  width: 100%;
  border-radius: 0.375rem;
  color: black;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s;
}
.input:focus {
  border-color: black;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.2);
}
</style>
