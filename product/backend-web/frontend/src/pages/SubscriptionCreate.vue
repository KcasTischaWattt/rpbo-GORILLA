<template>
  <q-page class="page-body q-mt-md">
    <BackButton to="/digests" />
    <div
      class="text-h5 caption"
      :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
    >
      Заказ дайджеста
    </div>

    <q-form @submit.prevent="submitForm" class="q-gutter-md">
      <q-input
        v-model="title"
        label="Название"
        filled
        required
        class="q-mt-lg"
        :color="$q.dark.isActive ? 'primary' : 'accent'"
      />

      <div class="q-mt-sm row items-center q-gutter-sm">
        <FilterSelector
          v-model="selectedSources"
          label="Источники"
          :options="availableSources"
          multiple
          required
        />
        <FilterSelector
          v-model="selectedTags"
          label="Теги"
          :options="availableTags"
          multiple
          required
        />
      </div>

      <q-input
        v-model="description"
        label="Описание"
        type="textarea"
        outlined
        :color="$q.dark.isActive ? 'primary' : 'accent'"
      />

      <div class="q-mt-md">
        <span>Настройки уведомлений:</span>
        <q-checkbox v-model="notificationEmail" label="На почту" />
        <q-checkbox v-model="notificationMobile" label="В мобильное приложение" />
      </div>
      <div class="q-mt-md">
        <span>Доступ:</span>
        <q-checkbox v-model="isPublic" label="Сделать публичным" />
      </div>

      <div class="row items-center q-mt-md">
        <q-input
          v-model="email"
          class="col"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
          label="Добавить получателя"
          filled
          @keyup.enter="addEmail"
        />
        <q-btn
          label="Добавить"
          @click="addEmail"
          color="primary"
          no-caps
          text-color="dark"
          padding="md"
          unelevated
          class="col-auto search-btn"
        />
      </div>
      <div v-if="emailError" class="text-negative">Данный пользователь не найден в системе</div>
      <div v-if="emailExists" class="text-negative">Этот человек уже был добавлен в рассылку</div>

      <div class="q-mt-sm">
        <q-chip
          :label="ownerEmail"
          color="primary"
          text-color="secondary"
          class="q-mb-xs"
        >
          <q-icon :name="fasCrown" color="secondary" class="q-ml-xs">
            <q-tooltip
              anchor="top middle"
              self="bottom middle"
              class="bg-primary text-secondary"
              transition-show="scale"
              transition-hide="scale"
            >
              Вы владелец этого дайджеста
            </q-tooltip>
          </q-icon>
        </q-chip>
        <q-chip
          v-for="(chip, index) in addedEmails"
          :key="index"
          :label="chip.email"
          removable
          color="primary"
          text-color="secondary"
          @remove="removeChip(index)"
          class="q-mb-xs"
        >
          <q-icon
            v-if="chip.send_to_mail"
            name="mail"
            color="secondary"
            class="q-ml-xs"
          >
            <q-tooltip
              class="bg-primary text-secondary"
              transition-show="scale"
              transition-hide="scale"
            >
              Этот пользователь получает уведомления на почту
            </q-tooltip>
          </q-icon>
          <q-icon
            v-if="chip.mobile_notifications"
            name="phone_iphone"
            color="secondary"
            class="q-ml-xs"
          >
            <q-tooltip
              class="bg-primary text-secondary"
              transition-show="scale"
              transition-hide="scale"
            >
              Этот пользователь получает мобильные уведомления
            </q-tooltip>
          </q-icon>
        </q-chip>
      </div>

      <q-btn
        label="Создать дайджест"
        type="submit"
        icon-right="add"
        color="secondary"
        text-color="white"
        no-caps
        class="q-mt-lg"
      />
    </q-form>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import { availableSources, availableTags, backendURL } from '../data/lookups.js'
import { fasCrown } from '@quasar/extras/fontawesome-v6'
import FilterSelector from '../components/FilterSelector.vue'
import BackButton from 'src/components/BackButton.vue'

const $q = useQuasar()
const router = useRouter()
const title = ref('')
const description = ref('')
const notificationEmail = ref(false)
const notificationMobile = ref(false)
const isPublic = ref(false)
const email = ref('')
const addedEmails = ref([])
const emailError = ref(false)
const emailExists = ref(false)
const selectedSources = ref([])
const selectedTags = ref([])
const ownerEmail = ref('')

onMounted(fetchOwnerEmail)

async function fetchOwnerEmail() {
  try {
    const res = await fetch(`${backendURL}users/get_email`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include'
    })
    if (!res.ok) throw new Error('Не удалось получить email владельца')
    const data = await res.json()
    ownerEmail.value = data.email
  } catch (err) {
    console.error('Ошибка при получении email владельца:', err)
  }
}

async function addEmail() {
  if (
    addedEmails.value.some(chip => chip.email === email.value) ||
    email.value === ownerEmail.value
  ) {
    emailExists.value = true
    return
  }
  emailExists.value = false

  if (!email.value) return

  try {
    const res = await fetch(`${backendURL}users/subscription_settings/check`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ username: email.value })
    })
    if (res.status === 401) {
      router.replace('/login')
      return
    }
    const data = await res.json()
    const { send_to_mail, mobile_notifications } = data
    if (send_to_mail !== undefined || mobile_notifications !== undefined) {
      emailError.value = false
      addedEmails.value.push({ email: email.value, send_to_mail, mobile_notifications })
      email.value = ''
    }
  } catch (err) {
    console.error('Ошибка при добавлении email:', err)
    emailError.value = true
  }
}

function removeChip(index) {
  addedEmails.value.splice(index, 1)
}

async function submitForm() {
  const requestData = {
    title: title.value,
    description: description.value,
    sources: selectedSources.value.map(src => src.value),
    tags: selectedTags.value.map(tag => tag.value),
    subscribe_options: {
      send_to_mail: notificationEmail.value,
      mobile_notifications: notificationMobile.value
    },
    public: isPublic.value,
    followers: addedEmails.value.map(chip => chip.email)
  }

  try {
    const res = await fetch(`${backendURL}subscriptions/create`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(requestData)
    })
    await res.json()
    router.replace('/digests')
  } catch (err) {
    console.error('Ошибка при создании дайджеста:', err)
  }
}
</script>

<style scoped>
.q-mb-xs {
  margin-bottom: 5px;
}
</style>
