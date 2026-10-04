<template>
  <q-card class="auth-wrapper q-mt-lg" :class="{ 'no-shadow': $q.dark.isActive }">
    <q-icon name="lock" size="xl" color="primary" class="q-mb-md" />
    <div class="text-h6 caption q-mb-md">
      Функционал только для авторизироанных пользователей
    </div>
    <div v-if="description" class="q-mb-md text-center">
      {{ description }}
    </div>
    <q-btn label="Войти" no-caps text-color="secondary" color="primary" @click="goToLogin" />
  </q-card>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  description: {
    type: String,
    default: ''
  }
})

const router = useRouter()
function goToLogin() {
  router.push({ name: 'login' })
}
</script>

<style scoped>
.auth-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
}
</style>