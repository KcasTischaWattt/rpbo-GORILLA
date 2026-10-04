<template>
  <q-page class="page-body q-mt-md">
    <BackButton to="/digests" />
    <div
      class="text-h5 caption"
      :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
    >
      Редактирование заказа на дайджест
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
          v-for="(chip, index) in addedEmails"
          :key="chip.email"
          :label="chip.email"
          :removable="chip.email !== ownerEmail"
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
          <q-icon
            v-if="chip.email !== ownerEmail"
            name="arrow_upward"
            color="secondary"
            class="q-ml-xs cursor-pointer"
            @click="openConfirmationPopup(chip.email)"
          >
            <q-tooltip
              class="bg-primary text-secondary"
              transition-show="scale"
              transition-hide="scale"
            >
              Назначить владельцем
            </q-tooltip>
          </q-icon>
          <q-icon
            v-if="chip.email === ownerEmail"
            :name="fasCrown"
            color="secondary"
            class="q-ml-xs"
          >
            <q-tooltip
              class="bg-primary text-secondary"
              transition-show="scale"
              transition-hide="scale"
            >
              Это текущий владелец
            </q-tooltip>
          </q-icon>
        </q-chip>
      </div>

      <q-btn
        label="Сохранить изменения"
        type="submit"
        icon-right="save"
        color="secondary"
        text-color="white"
        no-caps
        class="q-mt-lg"
      />
    </q-form>

    <ConfirmationPopup
      v-model="isPopupOpen"
      title="Подтверждение передачи прав"
      :message="popupMessage"
      checkboxLabel="Я понимаю, что делаю"
      confirmLabel="Подтвердить"
      @confirm="setAsOwnerConfirmed"
      @cancel="closeConfirmationPopup"
    />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter, useRoute } from 'vue-router'
import { availableSources, availableTags, backendURL } from '../data/lookups.js'
import { fasCrown } from '@quasar/extras/fontawesome-v6'
import FilterSelector from '../components/FilterSelector.vue'
import ConfirmationPopup from '../components/ConfirmDialog.vue'
import BackButton from 'src/components/BackButton.vue'

const $q = useQuasar()
const router = useRouter()
const route = useRoute()

const digestId = route.params.id

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
const isPopupOpen = ref(false)
const popupMessage = ref('')
const emailToBeOwner = ref('')

onMounted(async () => {
  try {
    const res = await fetch(`${backendURL}subscriptions/${digestId}`, { credentials: 'include' })
    const data = await res.json()
    title.value = data.title
    description.value = data.description
    selectedSources.value = availableSources.filter(opt => data.sources.includes(opt.value))
    selectedTags.value = availableTags.filter(opt => data.tags.includes(opt.value))
    notificationEmail.value = data.subscribe_options.send_to_mail
    notificationMobile.value = data.subscribe_options.mobile_notifications
    isPublic.value = data.public
    ownerEmail.value = data.owner
    addedEmails.value = data.followers.map(f => ({
      email: f.email,
      send_to_mail: f.send_to_mail,
      mobile_notifications: f.mobile_notifications
    }))
  } catch (err) {
    console.error('Ошибка при получении данных дайджеста:', err)
    $q.notify({ type: 'negative', message: 'Не удалось загрузить данные', position: 'top' })
  }
})

async function addEmail() {
  if (addedEmails.value.some(chip => chip.email === email.value) || email.value === ownerEmail.value) {
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
    if (res.status === 401) { router.replace('/login'); return }
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

function openConfirmationPopup(newOwner) {
  emailToBeOwner.value = newOwner
  popupMessage.value = `Вы уверены, что хотите передать права владельца пользователю с email: ${newOwner}?`
  isPopupOpen.value = true
}

function closeConfirmationPopup() {
  isPopupOpen.value = false
}

function setAsOwnerConfirmed() {
  ownerEmail.value = emailToBeOwner.value
  isPopupOpen.value = false
}

async function submitForm() {
  const payload = {
    title: title.value,
    description: description.value,
    sources: selectedSources.value.map(s => s.value),
    tags: selectedTags.value.map(t => t.value),
    subscribe_options: {
      send_to_mail: notificationEmail.value,
      mobile_notifications: notificationMobile.value
    },
    public: isPublic.value,
    followers: addedEmails.value.map(ch => ({ username: ch.email })),
    owner: ownerEmail.value
  }

  try {
    const res = await fetch(`${backendURL}subscriptions/${digestId}/update`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(payload)
    })
    await res.json()
    $q.notify({ type: 'positive', message: 'Изменения успешно сохранены', position: 'top' })
    router.replace('/digests')
  } catch (err) {
    console.error('Ошибка при сохранении изменений:', err)
    $q.notify({ type: 'negative', message: 'Ошибка при сохранении', position: 'top' })
  }
}
</script>

<style scoped>
.q-mb-xs { margin-bottom: 5px; }
</style>
