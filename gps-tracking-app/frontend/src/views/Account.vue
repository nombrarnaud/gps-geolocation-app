<template>
  <div class="min-h-screen bg-white p-4 max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-4 text-black">Mon compte</h1>
    <form @submit.prevent="handleUpdate" class="space-y-4">
      <div>
        <label class="block text-black mb-1">Nom complet</label>
        <input v-model="fullName" type="text" class="input" required />
      </div>
      <div>
        <label class="block text-black mb-1">Numéro de téléphone</label>
        <input v-model="phone" type="tel" class="input" required />
      </div>
      <div v-if="userType === 'BUSINESS'">
        <label class="block text-black mb-1">Nom de l'entreprise</label>
        <input v-model="companyName" type="text" class="input" required />
        <label class="block text-black mb-1">Nom du gérant</label>
        <input v-model="managerName" type="text" class="input" required />
      </div>
      <button type="submit" class="btn-primary w-full">Mettre à jour</button>
    </form>

    <hr class="my-6" />

    <h2 class="text-xl font-semibold mb-4 text-black">Changer le mot de passe</h2>
    <form @submit.prevent="handleChangePassword" class="space-y-4">
      <div>
        <label class="block text-black mb-1">Mot de passe actuel</label>
        <input v-model="currentPassword" type="password" class="input" required />
      </div>
      <div>
        <label class="block text-black mb-1">Nouveau mot de passe</label>
        <input v-model="newPassword" type="password" class="input" required />
      </div>
      <button type="submit" class="btn-primary w-full">Changer le mot de passe</button>
    </form>

    <div v-if="message" :class="{'text-green-600': success, 'text-red-600': !success}" class="mt-4 text-center">
      {{ message }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

const fullName = ref('');
const phone = ref('');
const companyName = ref('');
const managerName = ref('');
const userType = ref('');
const currentPassword = ref('');
const newPassword = ref('');
const message = ref('');
const success = ref(false);

const fetchUserProfile = async () => {
  try {
    const response = await axios.get('/api/users/profile');
    if (response.data.success) {
      const data = response.data.data;
      fullName.value = data.fullName;
      phone.value = data.phone;
      userType.value = data.userType;
      if (userType.value === 'BUSINESS') {
        companyName.value = data.companyName;
        managerName.value = data.managerName;
      }
    }
  } catch (error) {
    console.error('Erreur lors de la récupération du profil utilisateur', error);
  }
};

const handleUpdate = async () => {
  message.value = '';
  try {
    const payload: any = {
      fullName: fullName.value,
      phone: phone.value,
    };
    if (userType.value === 'BUSINESS') {
      payload.companyName = companyName.value;
      payload.managerName = managerName.value;
    }
    const response = await axios.put('/api/users/profile', payload);
    if (response.data.success) {
      message.value = response.data.message;
      success.value = true;
    } else {
      message.value = response.data.message || 'Erreur lors de la mise à jour';
      success.value = false;
    }
  } catch (error) {
    message.value = 'Erreur lors de la mise à jour';
    success.value = false;
  }
};

const handleChangePassword = async () => {
  message.value = '';
  try {
    const response = await axios.put('/api/users/change-password', {
      currentPassword: currentPassword.value,
      newPassword: newPassword.value,
    });
    if (response.data.success) {
      message.value = response.data.message;
      success.value = true;
      currentPassword.value = '';
      newPassword.value = '';
    } else {
      message.value = response.data.message || 'Erreur lors du changement de mot de passe';
      success.value = false;
    }
  } catch (error) {
    message.value = 'Erreur lors du changement de mot de passe';
    success.value = false;
  }
};

onMounted(() => {
  fetchUserProfile();
});
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
.btn-primary {
  background-color: black;
  color: white;
  padding: 0.5rem 0;
  border-radius: 0.375rem;
  font-weight: 600;
  cursor: pointer;
}
.btn-primary:hover {
  background-color: #222;
}
</style>
