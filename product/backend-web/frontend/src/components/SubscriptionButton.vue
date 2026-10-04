<template>
  <div class="subscription-btn-wrapper">
    <q-btn
      v-if="!subscribed"
      label="Подписаться"
      no-caps
      icon-right="notifications"
      color="primary"
      text-color="secondary"
      class="q-ml-sm q-mt-sm"
      @click="onSubscribeClick"
      :loading="loadingSubscription"
      :disable="loadingSubscription"
    />

    <q-btn-dropdown
      v-else
      outline
      @show="onDropdownShow"
      no-caps
      :color="$q.dark.isActive ? 'primary' : 'secondary'"
      class="q-ml-sm q-mt-sm"
      label="Управлять подпиской"
      dropdown-icon="settings"
      v-model="dropdown"
      :loading="loadingDropdown"
      :disable="loadingDropdown"
    >
      <q-list>
        <q-item clickable @click="toggleCheckbox('send_to_mail')">
          <q-item-section>
            <q-checkbox
              v-model="send_to_mail"
              class="custom-checkbox"
              label="На почту"
              @update:model-value="sendSubscriptionUpdate"
              @click.stop
            />
          </q-item-section>
          <q-item-section avatar>
            <q-icon name="email" :color="$q.dark.isActive ? 'primary' : 'secondary'" />
          </q-item-section>
        </q-item>

        <q-item clickable @click="toggleCheckbox('mobile_notifications')">
          <q-item-section>
            <q-checkbox
              v-model="mobile_notifications"
              keep-color
              label="Мобильные уведомления"
              @update:model-value="sendSubscriptionUpdate"
              @click.stop
            />
          </q-item-section>
          <q-item-section avatar>
            <q-icon name="phone_iphone" :color="$q.dark.isActive ? 'primary' : 'secondary'" />
          </q-item-section>
        </q-item>

        <q-separator />

        <q-item clickable @click="onUnsubscribeClick">
          <q-item-section> Отписаться </q-item-section>
        </q-item>
      </q-list>
    </q-btn-dropdown>

    <owner-transfer-dialog
      v-model="showOwnerTransferDialog"
      :subscriberEmails="subscriberEmails"
      :transferLoading="transferLoading"
      :transferError="transferError"
      @cancel="handleOwnerTransferCancel"
      @confirm="handleOwnerTransferConfirm"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { backendURL } from 'src/data/lookups'
import OwnerTransferDialog from './OwnerTransferDialog.vue'

const props = defineProps({
  digest: { type: Object, required: true },
  triggeredFrom: { type: String, required: true },
})
const emit = defineEmits(['remove-subscription'])

const router = useRouter()
const $q = useQuasar()

const dropdown = ref(false)
const loadingSubscription = ref(false)
const loadingDropdown = ref(false)
const subscribed = ref(props.digest.subscribe_options.subscribed)
const send_to_mail = ref(props.digest.subscribe_options.send_to_mail)
const mobile_notifications = ref(props.digest.subscribe_options.mobile_notifications)
const subscriberEmails = ref([])
const showOwnerTransferDialog = ref(false)
const transferLoading = ref(false)
const transferError = ref('')

function getRightUrlPart() {
  return props.triggeredFrom === 'subscription_page' || props.triggeredFrom === 'subscriptions'
    ? 'subscriptions'
    : 'digests'
}

function getUpdateUrl() {
  let url = `${backendURL}digests/${props.digest.id}/subscription/settings/update`
  if (props.triggeredFrom === 'subscriptions' || props.triggeredFrom === 'subscription_page') {
    url = `${backendURL}subscriptions/${props.digest.id}/settings/update`
  }
  return url
}

async function sendSubscriptionUpdate() {
  const payload = {
    subscribed: subscribed.value,
    send_to_mail: send_to_mail.value,
    mobile_notifications: mobile_notifications.value,
  }
  try {
    const response = await fetch(getUpdateUrl(), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(payload),
    })
    if (!response.ok) {
      if (response.status === 401) {
        router.replace('/login')
      } else {
        console.error('Ошибка при изменении настроек подписки')
      }
      return false
    }
    const data = await response.json()
    subscribed.value = data.subscribed
    send_to_mail.value = data.send_to_mail
    mobile_notifications.value = data.mobile_notifications
    return data
  } catch (err) {
    console.error('Ошибка запроса:', err)
    return false
  }
}

async function onSubscribeClick() {
  if (loadingSubscription.value) return
  loadingSubscription.value = true
  subscribed.value = true
  const success = await sendSubscriptionUpdate()
  if (!success) {
    $q.notify({
      message: 'Не удалось подписаться. Доступ к подписке закрыт или она была удалена.',
      type: 'negative',
      position: 'top',
      badgeColor: 'yellow',
      badgeTextColor: 'dark',
    })
    subscribed.value = false
  }
  loadingSubscription.value = false
}

async function toggleCheckbox(type) {
  const original = type === 'send_to_mail' ? send_to_mail.value : mobile_notifications.value
  if (type === 'send_to_mail') {
    send_to_mail.value = !send_to_mail.value
  } else {
    mobile_notifications.value = !mobile_notifications.value
  }
  const success = await sendSubscriptionUpdate()
  if (!success) {
    if (type === 'send_to_mail') {
      send_to_mail.value = original
    } else {
      mobile_notifications.value = original
    }
  }
}

function onDropdownShow() {}

async function executeUnsubscribe() {
  const origMail = send_to_mail.value
  const origMobile = mobile_notifications.value

  send_to_mail.value = null
  mobile_notifications.value = null
  subscribed.value = false

  const data = await sendSubscriptionUpdate()
  if (!data) {
    send_to_mail.value = origMail
    mobile_notifications.value = origMobile
    subscribed.value = true
  } else if (props.triggeredFrom === 'subscriptions') {
    if (!props.digest.public || data.deleted) {
      emit('remove-subscription', props.digest.id)
    }
  }

  loadingDropdown.value = false
}

async function onUnsubscribeClick() {
  if (loadingDropdown.value) return
  loadingDropdown.value = true

  if (props.digest.is_owner) {
    try {
      const resp = await fetch(`${backendURL}${getRightUrlPart()}/${props.digest.id}/followers`, {
        credentials: 'include',
      })
      const result = await resp.json()
      if (result && result.followers.length) {
        subscriberEmails.value = result.followers
        showOwnerTransferDialog.value = true
        loadingDropdown.value = false
        return
      }
      if (resp.status === 401) {
        router.replace('/login')
      }
    } catch (err) {
      console.error('Ошибка получения списка подписчиков:', err)
    }
  }

  await executeUnsubscribe()
}

async function handleOwnerTransferConfirm(newOwnerEmail) {
  transferLoading.value = true
  transferError.value = ''
  try {
    const resp = await fetch(`${backendURL}subscriptions/${props.digest.id}/change_owner`, {
      method: 'PATCH',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: props.digest.id, owner: newOwnerEmail }),
    })
    if (!resp.ok) {
      if (resp.status === 401) {
        router.replace('/login')
      } else {
        transferError.value = 'Что-то пошло не так при передаче прав'
      }
      return
    }
    await executeUnsubscribe()
    showOwnerTransferDialog.value = false
  } catch (err) {
    console.error('Ошибка запроса:', err)
    transferError.value = 'Что-то пошло не так при передаче прав'
  } finally {
    transferLoading.value = false
  }
}

function handleOwnerTransferCancel() {
  console.log('Выбор нового владельца отменён')
}
</script>

<style scoped>
.subscription-btn-wrapper {
  display: flex;
  align-items: center;
}
</style>
