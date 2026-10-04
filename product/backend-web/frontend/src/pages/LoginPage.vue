<template>
  <q-page class="flex flex-center q-pa-sm">
    <div class="login-container">
      <!-- Логотип, располагается чуть выше центра -->
      <div class="logo-container">
        <q-img :src="logoSrc" alt="Логотип" class="logo-img" />
      </div>

      <div class="title text-center q-mt-sm">
        <h4 class="caption">Добро пожаловать!</h4>
      </div>

      <q-form @submit.prevent="login" class="q-mt-md">
        <q-input
          filled
          v-model="email"
          label="Email"

          class="q-mb-md"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />
        <q-input
          filled
          v-model="password"
          label="Пароль"
          type="password"
          class="q-mb-md"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />

        <div class="text-center q-mb-md">
          <p>Нет аккаунта? <a href="/register">Зарегистрируйтесь - это быстро!</a></p>
        </div>

        <q-btn
          type="submit"
          label="Войти"
          no-caps
          text-color="secondary"
          color="primary"
          class="full-width"
        />
      </q-form>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import lightLogo from '../assets/crosswords_mono.png'
import darkLogo from '../assets/crosswords_mono_white.png'
import { backendURL } from 'src/data/lookups'
import { emitter } from 'src/boot/emitter'

const $q = useQuasar()
const router = useRouter()

const email = ref('')
const password = ref('')

const logoSrc = computed(() => $q.dark.isActive ? darkLogo : lightLogo)

async function login() {
  const payload = { username: email.value, password: password.value }
  try {
    const response = await fetch(
      `${backendURL}users/login`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      }
    )
    if (!response.ok) {
      $q.notify({
        type: 'negative',
        message: 'Ошибка входа. Проверьте свои данные.',
        position: 'top'
      })
      throw new Error('Ошибка при входе')
    }
    await response.json()
    emitter.emit('auth-changed')
    router.replace('/')
  } catch (error) {
    console.error('Ошибка входа:', error)
  }
}
</script>

<style scoped>
.login-container {
  margin-top: -50px;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}
</style>
