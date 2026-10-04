<template>
  <q-page class="page-body q-pa-md">
    <q-btn label="к другим настройкам" @click="toggleDrawer" class="q-mb-xl" outline />
    <div class="text-h4 caption">Настройки аккаунта</div>

    <q-card class="q-mb-xl q-mt-md" :class="{ 'no-shadow': $q.dark.isActive }">
      <q-card-section>
        <div class="text-h6 q-mb-md">Параметры аккаунта</div>
        <div><strong>Имя:</strong> {{ first_name }}</div>
        <div><strong>Фамилия:</strong> {{ last_name }}</div>
        <div>
          <strong>username:</strong> {{ username }}
          <q-icon name="info">
            <q-tooltip
              anchor="top middle"
              self="bottom middle"
              class="bg-primary text-secondary"
              transition-show="scale"
              transition-hide="scale"
            >
              Ваш username автоматически создается на основе преддоменной части вашей электронной
              почты. <br />
              Его можно использовать вместо почты для авторизации и добавления пользователей в
              дайджесты.
            </q-tooltip></q-icon
          >
        </div>
        <div><strong>email:</strong> {{ static_email }}</div>
        <div class="row items-center q-gutter-sm">
          <strong>Telegram:</strong>

          <template v-if="telegram">
            <span>Привязан</span>
            <q-icon name="check_circle" color="positive" size="20px" />
          </template>

          <q-btn
            v-else
            label="Привязать"
            color="primary"
            no-caps
            text-color="secondary"
            size="sm"
            unelevated
            :loading="telegramLoading"
            @click="bindTelegram"
          />
        </div>
      </q-card-section>
    </q-card>

    <div>
      <h6 class="q-mb-none UIText">Сменить почту</h6>
      <div class="row items-center q-mt-sm">
        <q-input
          filled
          v-model="email"
          label="Email"
          class="col"
          type="email"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />
        <q-btn
          label="Обновить"
          color="primary"
          no-caps
          text-color="secondary"
          @click="updateEmail"
          padding="md"
          unelevated
        />
      </div>
    </div>

    <div>
      <h6 class="q-mb-lg UIText">Изменить пароль</h6>
      <div class="q-gutter-md">
        <q-input
          filled
          type="password"
          v-model="oldPassword"
          label="старый пароль"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />
        <q-input
          filled
          type="password"
          v-model="newPassword"
          label="новый пароль"
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />
        <q-btn
          label="Обновить пароль"
          color="primary"
          @click="updatePassword"
          :disable="!oldPassword || !newPassword"
          no-caps
          text-color="secondary"
        />
      </div>
      <div class="q-mt-sm row q-gutter-sm">
        <q-btn label="Выйти из аккаунта" outline @click="logout" no-caps text-color="negative" />
        <q-btn
          label="Выйти из аккаунта на всех устройствах"
          outline
          @click="globalLogout"
          no-caps
          text-color="negative"
        />
      </div>
    </div>

    <div>
      <h4 class="q-mb-none caption">Настройки уведомлений</h4>
      <div class="q-gutter-md q-mt-sm">
        <q-toggle
          v-model="personal_send_to_mail"
          @update:model-value="updateSubscriptions"
          label="Разрешить уведомления на почту"
        />
        <q-toggle
          v-model="personal_mobile_notifications"
          @update:model-value="updateSubscriptions"
          label="Разрешить мобильные уведомления"
        />
        <q-checkbox
          v-model="allowForeignSubs"
          label="Разрешить другим пользователями добавлять вас в рассылку дайджестов"
          @update:model-value="updateSubscriptions"
        />

        <div v-if="allowForeignSubs" class="q-ml-lg q-gutter-md">
          <q-toggle
            v-model="send_to_mail"
            label="Разрешить посторонние уведомления на почту"
            @update:model-value="updateSubscriptions"
          />
          <q-toggle
            v-model="mobile_notifications"
            label="Разрешить посторонние мобильные уведомления"
            @update:model-value="updateSubscriptions"
          />
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import useDrawer from 'src/composables/useDrawer'
import { useRouter } from 'vue-router'
import { backendURL } from 'src/data/lookups'
import { useQuasar } from 'quasar'
import { emitter } from 'src/boot/emitter'

const $q = useQuasar()
const router = useRouter()
const { toggleDrawer } = useDrawer()

const first_name = ref('')
const last_name = ref('')
const username = ref('')
const email = ref('')
const static_email = ref('')
const oldPassword = ref('')
const newPassword = ref('')
const personal_mobile_notifications = ref(false)
const personal_send_to_mail = ref(false)
const allowForeignSubs = ref(false)
const send_to_mail = ref(false)
const mobile_notifications = ref(false)
const telegram = ref(false)
const telegramLoading = ref(false)

onMounted(async () => {
  try {
    const response = await fetch(`${backendURL}users/personal_info`, { credentials: 'include' })
    const data = await response.json()
    first_name.value = data.first_name
    last_name.value = data.second_name
    username.value = data.username
    email.value = data.email
    static_email.value = data.email
    allowForeignSubs.value = data.subscribable
    personal_mobile_notifications.value = data.personal_mobile_notifications
    personal_send_to_mail.value = data.personal_send_to_mail
    mobile_notifications.value = data.mobile_notifications
    send_to_mail.value = data.send_to_mail
    telegram.value = data.telegram
  } catch (err) {
    console.error('Error fetching user details:', err)
    $q.notify({
      type: 'negative',
      message: 'Не удалось загрузить данные пользователя',
      position: 'top',
    })
  }
})

async function updateSubscriptions() {
  try {
    const payload = {
      subscribable: allowForeignSubs.value,
      send_to_mail: send_to_mail.value,
      mobile_notifications: mobile_notifications.value,
      personal_send_to_mail: personal_send_to_mail.value,
      personal_mobile_notifications: personal_mobile_notifications.value,
    }
    const response = await fetch(`${backendURL}users/subscription_settings/set`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(payload),
    })
    if (!response.ok) throw new Error(`Status ${response.status}`)
    $q.notify({
      type: 'positive',
      message: 'Настройки уведомлений сохранены',
      position: 'top',
      badgeColor: 'yellow',
      badgeTextColor: 'dark',
    })
  } catch (err) {
    console.error('Ошибка при сохранении уведомлений', err)
    $q.notify({
      type: 'negative',
      message: 'Не удалось сохранить настройки уведомлений',
      position: 'top',
    })
  }
}

async function updateEmail() {
  try {
    const response = await fetch(`${backendURL}users/change/email`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ new_email: email.value }),
    })
    if (response.ok) {
      $q.notify({ type: 'positive', message: 'Email успешно обновлён', position: 'top' })
      const data = await response.json()
      email.value = data.email
      static_email.value = data.email
      username.value = data.username
    } else if (response.status === 401) {
      router.replace('/login')
    } else if (response.status === 400) {
      $q.notify({
        type: 'negative',
        message: 'Новый email не может быть таким же, как старый или пустым',
        position: 'top',
      })
    } else {
      $q.notify({ type: 'negative', message: 'Ошибка при обновлении email', position: 'top' })
    }
  } catch (err) {
    console.error('Error updating email:', err)
    $q.notify({ type: 'negative', message: 'Ошибка при обновлении email', position: 'top' })
  }
}

async function bindTelegram() {
  telegramLoading.value = true

  try {
    const response = await fetch(`${backendURL}users/telegram/generate-token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include'
    })

    if (response.ok) {
      const data = await response.json()

      if (!data.link) {
        throw new Error('Telegram link is missing')
      }

      window.location.href = data.link
    } else if (response.status === 401) {
      router.replace('/login')
    } else {
      $q.notify({
        type: 'negative',
        message: 'Не удалось создать ссылку для привязки Telegram',
        position: 'top'
      })
    }
  } catch (err) {
    console.error('Error binding Telegram:', err)

    $q.notify({
      type: 'negative',
      message: 'Ошибка при привязке Telegram',
      position: 'top'
    })
  } finally {
    telegramLoading.value = false
  }
}

async function updatePassword() {
  try {
    const response = await fetch(`${backendURL}users/change/password`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ old_password: oldPassword.value, new_password: newPassword.value }),
    })
    oldPassword.value = ''
    newPassword.value = ''
    if (response.ok) {
      $q.notify({ type: 'positive', message: 'Пароль успешно обновлён', position: 'top' })
    } else if (response.status === 401) {
      router.replace('/login')
    } else if (response.status === 403) {
      $q.notify({ type: 'negative', message: 'Неверный старый пароль', position: 'top' })
    } else if (response.status === 400) {
      $q.notify({
        type: 'negative',
        message: 'Новый пароль не может быть таким же, как старый или пустым',
        position: 'top',
      })
    } else {
      $q.notify({ type: 'negative', message: 'Ошибка при обновлении пароля', position: 'top' })
    }
  } catch (err) {
    console.error('Error updating password:', err)
    $q.notify({ type: 'negative', message: 'Ошибка при обновлении пароля', position: 'top' })
  }
}

async function logout() {
  const response = await fetch(`${backendURL}users/logout`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
  })
  if (!response.ok) {
    if (response.status === 401) router.replace('/login')
    else $q.notify({ type: 'negative', message: 'Ошибка при выходе из аккаунта', position: 'top' })
    return
  }
  emitter.emit('auth-changed')
  router.replace('/login')
  $q.notify({ type: 'positive', message: 'Вы вышли из аккаунта', position: 'top' })
}

async function globalLogout() {
  const response = await fetch(`${backendURL}users/logout/full`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
  })
  if (!response.ok) {
    if (response.status === 401) router.replace('/login')
    else $q.notify({ type: 'negative', message: 'Ошибка при выходе из аккаунта', position: 'top' })
    return
  }
  emitter.emit('auth-changed')
  router.replace('/login')
  $q.notify({ type: 'positive', message: 'Вы вышли из аккаунта', position: 'top' })
}
</script>

<style scoped>
.row {
  display: flex;
  align-items: center;
}
</style>
