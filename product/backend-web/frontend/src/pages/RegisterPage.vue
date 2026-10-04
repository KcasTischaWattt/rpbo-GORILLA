<template>
  <q-page class="flex flex-center q-pa-sm">
    <div class="login-container">
      <div class="logo-container">
        <q-img :src="logoSrc" alt="Логотип" class="logo-img" />
      </div>

      <div class="title text-center q-mt-sm">
        <h4 class="caption">Создайте аккаунт</h4>
      </div>

      <q-form @submit.prevent="register" class="q-mt-md">
        <q-input
          filled
          v-model="name"
          label="Имя"
          class="q-mb-md"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />
        <q-input
          filled
          v-model="surname"
          label="Фамилия"
          class="q-mb-md"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />
        <q-input
          filled
          v-model="email"
          label="Email"
          type="email"
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
          <p>Уже есть зарегистрированный аккаунт? <a href="/login">Войти</a></p>
        </div>

        <q-btn
          type="submit"
          label="Зарегистрироваться"
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
import { onMounted } from 'vue'
import { sendAnalyticsEvent } from 'src/utils/analytics.js'

const $q = useQuasar()
const router = useRouter()

const name = ref('')
const surname = ref('')
const email = ref('')
const password = ref('')

const logoSrc = computed(() => $q.dark.isActive ? darkLogo : lightLogo)



onMounted(() => {
  sendAnalyticsEvent('registration_page_view', {
    source: document.referrer || null
  })
})

async function register() {
  const payload = {
    username: email.value,
    password: password.value,
    name: name.value,
    surname: surname.value,
    email: email.value
  }

  try {
    const response = await fetch(
      `${backendURL}users/register`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      }
    )
    if (!response.ok) {
      throw new Error('Ошибка при регистрации')
    }
    await response.json()
    emitter.emit('auth-changed')
    router.replace('/')
  } catch (error) {
    console.error('Ошибка при регистрации:', error)
    $q.notify({ type: 'negative', message: 'Ошибка регистрации. Проверьте данные.', position: 'top' })
  }
}
</script>

<style scoped>
.login-container {
  /* Поднимаем контейнер чуть выше центра */
  margin-top: -50px;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}
</style>
