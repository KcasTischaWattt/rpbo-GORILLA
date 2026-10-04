<template>
  <q-card :class="['q-my-sm', { 'no-shadow': $q.dark.isActive }]">
    <q-card-section>
      <div class="row items-center justify-between">
        <div><b>ID:</b> {{ props.digest.id }}</div>
        <div class="row items-center">
          <div>
            <b class="q-mr-sm">
              <q-icon name="star" color="primary" />
              {{ displayRating }}
            </b>
          </div>

          <b v-if="props.is_authed && hasSubscribeOptions">
            {{ subscribed ? 'Подписан' : 'Не подписан' }}
          </b>
          <span v-if="hasSubscribeOptions && subscribed">
            <q-icon v-if="sendToMail && props.is_authed" name="mail" class="q-ml-xs" />
            <q-icon v-if="mobileNotifications && props.is_authed" name="phone_iphone" class="q-ml-xs" />
          </span>

          <span class="q-ml-sm"><b>Дата:</b> {{ props.digest.date }}</span>
        </div>
      </div>

      <div v-if="props.digest.tags" class="row items-center q-mt-sm">
        <div class="text-h4 ellipsis">{{ truncatedTitle }}</div>
      </div>

      <div class="q-mt-sm">{{ truncatedText }}</div>

      <div v-if="props.digest.tags && props.digest.tags.length" class="q-mt-sm row wrap">
        <DocumentTags :tags="props.digest.tags" />
      </div>

      <div class="row items-center justify-between q-mt-sm">
        <q-btn
          label="Посмотреть"
          color="primary"
          no-caps
          text-color="secondary"
          @click="viewDigest"
        />
        <q-btn
          label="PDF версия"
          color="secondary"
          icon-right="download"
          no-caps
          @click="downloadPdf"
        />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { backendURL } from 'src/data/lookups'
import DocumentTags from '../components/DocumentTags.vue'

const props = defineProps({
  digest: { type: Object, required: true },
  is_authed: { type: Boolean, default: false }
})

const $q = useQuasar()
const router = useRouter()

const subscribed = ref(props.digest.subscribe_options?.subscribed ?? false)
const sendToMail = ref(props.digest.subscribe_options?.send_to_mail ?? false)
const mobileNotifications = ref(props.digest.subscribe_options?.mobile_notifications ?? false)

const hasSubscribeOptions = computed(() => !!props.digest.subscribe_options)
const displayRating = computed(() => {
  const r = props.digest.average_rating
  return (r != null && r !== -1) ? r : 'Нет оценок'
})
const truncatedTitle = computed(() => {
  const t = props.digest.title || ''
  return t.length > 22 ? t.slice(0, 22) + '...' : t
})
const truncatedText = computed(() => {
  const txt = props.digest.text || ''
  return txt.length > 665 ? txt.slice(0, 665) + '...' : txt
})

function viewDigest() {
  const path = `/digests/${props.digest.id}`
  router.push(path)
}

async function downloadPdf() {
  try {
    const response = await fetch(
      `${backendURL}digests/${props.digest.id}/pdf`,
      { credentials: 'include' }
    )
    if (!response.ok) {
      throw new Error(`Сервер вернул ${response.status}`)
    }
    const blob = await response.blob()
    const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${props.digest.title}_${props.digest.id}.pdf`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('Ошибка при скачивании PDF:', err)
    $q.notify({ message: 'Ошибка скачивания PDF', type: 'negative', position: 'top' })
  }
}
</script>

<style scoped>
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
