<template>
  <ServerResponseSpinner v-if="isLoading" />
  <q-page v-else class="q-pa-md flex flex-center column">
    <div
      class="caption text-h4 text-center q-mt-xl q-mb-md"
      :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
    >
      Введите код подтверждения
    </div>
    <div
      class="q-mb-md text-center"
      :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
    >
      Отправили вам код подтвреждения на
      <b :class="{ 'text-secondary': !$q.dark.isActive, 'text-primary': $q.dark.isActive }">{{
        userEmail
      }}</b>
    </div>
    <div class="text-center q-mb-md">
      <p>Ошибка в адресе? <a href="/profile">Смените его в настройках!</a></p>
    </div>
    <div class="digits-row flex q-gutter-sm q-mb-md">
      <q-input
        v-for="(digit, idx) in digits"
        :key="idx"
        v-model="digits[idx]"
        :ref="(el) => (digitRefs[idx] = el)"
        maxlength="1"
        class="digit-box text-center"
        input-class="text-h5 text-center"
        outlined
        @update:model-value="(val) => handleInput(val, idx)"
        @keydown.backspace="handleBackspace(idx, $event)"
        @paste="handlePaste(idx, $event)"
      />
    </div>

    <q-btn
      label="Отправить"
      color="primary"
      no-caps
      padding="8px 100px"
      text-color="secondary"
      :disable="code.length !== 6 || sending"
      @click="submitCode"
    />

    <div class="q-mt-lg text-center">Не пришел код? <br> Проверьте спам и прочие папки</div>
    <div>
      <q-btn flat no-caps :disable="timerActive" :color="$q.dark.isActive ? 'primary' : 'accent'" @click="resend">{{
        resendLabel
      }}</q-btn>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, reactive } from 'vue'
import { useRouter } from 'vue-router'
import {useQuasar} from 'quasar'

import ServerResponseSpinner from 'src/components/ServerResponseSpinner.vue'
import { backendURL } from 'src/data/lookups'

const router = useRouter()
const $q = useQuasar()
const digits = reactive(Array.from({ length: 6 }, () => ''))
const digitRefs = reactive([])
const userEmail = ref()
const isLoading = ref(true)

const code = computed(() => digits.join(''))

function focusBox(i) {
  nextTick(() => {
    digitRefs[i]?.focus?.()
  })
}

function handleInput(val, idx) {
  if (val && idx < 5) {
    focusBox(idx + 1)
  }
}

function handleBackspace(idx, e) {
  if (!digits[idx] && idx > 0) {
    focusBox(idx - 1)
    e.preventDefault()
  }
}

function handlePaste(idx, e) {
  const paste = e.clipboardData.getData('text')
  if (!paste) return
  const clean = paste.replace(/\D/g, '').slice(0, 6)
  clean.split('').forEach((ch, i) => {
    if (idx + i < 6) digits[idx + i] = ch
  })
  nextTick(() => {
    focusBox(Math.min(idx + clean.length, 5))
  })
  e.preventDefault()
}

const timer = ref(60)
let intervalId = null
const timerActive = computed(() => timer.value > 0)
const resendLabel = computed(() =>
  timerActive.value
    ? `Отправить ещё раз (${String(timer.value).padStart(2, '0')})`
    : 'Отправить ещё раз',
)

function startTimer() {
  timer.value = 60
  clearInterval(intervalId)
  intervalId = setInterval(() => {
    if (timer.value > 0) {
      timer.value--
    } else {
      clearInterval(intervalId)
    }
  }, 1000)
}

const sending = ref(false)

async function requestConfirmation() {
  try {
    const response = await fetch(backendURL + 'users/verification_code/send', {
      method: 'POST',
      credentials: 'include'
    })
    if (!response.ok) {
      if (response.status === 401) {
        router.replace('/login')
        return
      }
      throw new Error(`Сервис временно недоступен`)
    }
    const data = await response.json()
    userEmail.value = data.email
    startTimer()
  } catch (error) {
    $q.notify({ type: 'negative', message: `Ошибка сети при запросе подтверждения: ${error.message}`, position: 'top' })
  } finally {
    isLoading.value = false
  }
}

async function resend() {
  if (timerActive.value) return
  await requestConfirmation()
}

async function submitCode() {
  if (code.value.length !== 6) return
  sending.value = true
  try {
    const response = await fetch(backendURL + 'users/verification_code/check', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ code: code.value }),
    })
    if (response.ok) {
      router.replace('/')
    } else {
      digits.fill('')
      focusBox(0)
      $q.notify({ type: 'negative', message: `Неверный код, попробуйте ещё раз`, position: 'top' })
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: `Сетевая ошибка: ${error.message}`, position: 'top' })
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  requestConfirmation()
  focusBox(0)
})

onUnmounted(() => clearInterval(intervalId))
</script>

<style scoped>
.digit-box {
  width: 48px;
}
</style>
