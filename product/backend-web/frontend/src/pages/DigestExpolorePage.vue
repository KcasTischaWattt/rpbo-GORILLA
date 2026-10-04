<template>
  <ServerResponseSpinner v-if="isLoading" />
  <q-page v-else class="flex flex-center">
    <ConfirmEmailBanner v-if="isAuthed"/>
    <div class="full-width q-pa-md page-body">
      <q-carousel
        v-if="featuredDigests.length"
        v-model="activeFeaturedSlide"
        swipeable
        infinite
        :autoplay="autoplay"
        @mouseenter="autoplay = false"
        @mouseleave="autoplay = true"
        transition-prev="slide-right"
        transition-next="slide-left"
        arrows
        control-type="flat"
        animated
        navigation
        control-color="primary"
        style="width: 100%; max-height: 80vh; overflow: hidden"
        class="q-mb-md"
        :class="{ lightBgClass: !$q.dark.isActive }"
      >
        <q-carousel-slide
          v-for="(digest, index) in featuredDigests.slice(0, 3)"
          :key="digest.id"
          :name="String(index)"
          style="cursor: pointer"
          @click="goToDigest(digest.id)"
        >
          <div class="row" style="height: 100%">
            <div
              class="col-12 col-md-6 text-white"
              :style="getLeftStyle(digest)"
              style="display: flex; flex-direction: column; padding: 1rem"
            >
              <div class="row justify-between">
                <div><b>ID:</b> {{ digest.id }}</div>
                <div>
                  <b>{{ digest.date }}</b>
                </div>
              </div>

              <div
                class="row justify-center"
                style="flex: 1; flex-direction: column; align-items: center"
              >
                <div class="text-h5 text-center q-mb-sm">{{ digest.title }}</div>

                <div
                  v-if="digest?.description"
                  class="description text-body3 text-italic text-center text-white"
                >
                  "{{ digest.description }}"
                </div>
              </div>
              <div class="row">
                <div><b>Источники:</b> {{ getLimitedSources(digest.sources) }}</div>
              </div>
            </div>

            <div class="col-12 col-md-6">
              <q-img
                :src="getDigestPic(digest)"
                style="height: 100%; width: 100%; object-fit: cover"
                loading="lazy"
              />
            </div>
          </div>
        </q-carousel-slide>
      </q-carousel>

      <my-subscriptions v-if="isAuthed" />
      <locked-content
        v-else
        description="Войдите в аккаунт, чтобы создавать собственные дайджесты или подписываться на существующие"
      />

      <div
        class="caption text-h4 text-center q-mt-xl q-mb-md"
        :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
      >
        Все дайджесты
      </div>

      <div class="row items-center q-mb-sm">
        <q-input
  v-model="searchBody"
  filled
  label="Название дайджеста"
  class="col"
  :color="$q.dark.isActive ? 'primary' : 'accent'"
  @keyup.enter="onSearch"
>
  <template v-slot:append>
    <q-btn
      round
      flat
      dense
      :icon="isRecording ? 'stop_circle' : 'mic'"
      :color="isRecording ? 'negative' : 'secondary'"
      :loading="isUploading"
      :disable="isUploading"
      @click.stop="toggleRecording"
    />

    <q-icon
      name="search"
      class="cursor-pointer q-ml-xs"
      @click="onSearch"
    />
  </template>
</q-input>


        <q-btn
          label="Найти"
          color="primary"
          no-caps
          text-color="dark"
          padding="md"
          unelevated
          :loading="isDigestLoading"
          class="col-auto search-btn"
          @click="onSearch"
        />
      </div>
          <div v-if="isRecording" class="text-negative text-caption q-my-sm">
      Идёт запись... нажмите на микрофон ещё раз, чтобы остановить.
    </div>

      <div class="row q-col-gutter-md items-center">
        <div class="col-6">
          <q-input
            v-model="dateFrom"
            filled
            dense
            label="Дата (с)"
            mask="##/##/####"
            :color="$q.dark.isActive ? 'primary' : 'accent'"
          >
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-date
                    v-model="dateFrom"
                    title="Искать с"
                    subtitle="Выбор даты"
                    color="secondary"
                    mask="DD/MM/YYYY"
                  >
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup label="Закрыть" color="secondary" flat />
                    </div>
                  </q-date>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </div>

        <div class="col-6">
          <q-input
            v-model="dateTo"
            filled
            dense
            label="Дата (по)"
            mask="##/##/####"
            :color="$q.dark.isActive ? 'primary' : 'accent'"
          >
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-date
                    v-model="dateTo"
                    title="Искать по"
                    subtitle="Выбор даты"
                    color="secondary"
                    mask="DD/MM/YYYY"
                  >
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup label="Закрыть" color="secondary" flat />
                    </div>
                  </q-date>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </div>
      </div>

      <div class="row q-col-gutter-md items-center">
        <div class="col-6">
          <filter-selector
            v-model="selectedTags"
            :options="availableTags"
            label="Тэги"
            :multiple=true
            dense
            clearable
            class="q-my-sm"
          />
        </div>
        <div class="col-6">
          <filter-selector
            v-model="selectedSources"
            :options="availableSources"
            label="Источники"
            dense
            :multiple=true
            clearable
            class="q-my-sm"
            @clear="clearSelection('sources')"
          />
        </div>
      </div>

      <div class="row q-col-gutter-md items-center q-mb-md justify-between">
        <div class="col-auto">
          <q-checkbox
            v-if="isAuthed"
            v-model="subscribeOnly"
            label="Только подписки"
            color="primary"
          />
        </div>
        <div class="col-auto">
          <q-btn
            label="Сбросить фильтры"
            color="secondary"
            no-caps
            text-color="white"
            @click="resetFilters"
            :loading="isDigestLoading"
          />
        </div>
      </div>

      <div>
        <digest-card
          v-for="digest in digests"
          :key="digest.id"
          :digest="digest"
          :is_authed="isAuthed"
        />
      </div>
    </div>

    <div class="q-mt-md q-mb-md flex flex-center" v-if="nextPage !== -1">
      <q-btn label="Показать еще" color="primary" no-caps text-color="dark" @click="onLoadMore" />
    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import DigestCard from '../components/DigestCard.vue'
import MySubscriptions from '../components/MySubscriptions.vue'
import FilterSelector from 'src/components/FilterSelector.vue'
import LockedContent from 'src/components/LockedContent.vue'
import ServerResponseSpinner from 'src/components/ServerResponseSpinner.vue'
import { availableTags, availableSources, backendURL } from '../data/lookups.js'
import { getImageAssetPath } from 'src/data/imageLoader'
import defaultImage from "/src/assets/default.jpg"
import ConfirmEmailBanner from 'src/components/ConfirmEmailBanner.vue'

const $q = useQuasar()
const router = useRouter()

const searchBody = ref('')
const isRecording = ref(false)
const isUploading = ref(false)

let mediaRecorder = null
let audioChunks = []
let mediaStream = null
let recordingTimeout = null
const dateFrom = ref('')
const dateTo = ref('')
const selectedTags = ref([])
const selectedSources = ref([])
const subscribeOnly = ref(false)
const digests = ref([])
const featuredDigests = ref([])
const isDigestLoading = ref(false)
const isLoading = ref(true)
const activeFeaturedSlide = ref('0')
const isAuthed = ref(false)
const autoplay = ref(true)
const nextPage = ref(0)
const matchesPerPage = 20

const digestColors = reactive({})
const colorPalette = [
  '#072449',
  '#092449',
  '#121858',
  '#151240',
  '#161E4C',
  '#1E151C',
  '#121C18',
  '#1C1E0D',
  '#201717',
  '#351510',
]

const getLimitedSources = (sources) => {
  return sources.length > 3 ? sources.slice(0, 3).join(', ') + ' ...' : sources.join(', ')
}

const goToDigest = (id) => {
  router.push(`/subscriptions/${id}`)
}

const formatToISO = (str) => {
  if (!str) return null
  const [day, month, year] = str.split('/')
  if (!day || !month || !year) return null
  const utcDateMs = Date.UTC(+year, +month - 1, +day)
  return new Date(utcDateMs).toISOString().split('T')[0]
}
async function toggleRecording() {
  if (isUploading.value) return

  if (isRecording.value) {
    stopRecording()
  } else {
    await startRecording()
  }
}

async function startRecording() {
  try {
    if (!navigator.mediaDevices?.getUserMedia) {
      console.error('Браузер не поддерживает запись аудио')
      return
    }

    mediaStream = await navigator.mediaDevices.getUserMedia({
      audio: true
    })

    audioChunks = []

    const mimeType = getSupportedMimeType()

    mediaRecorder = new MediaRecorder(
      mediaStream,
      mimeType ? { mimeType } : undefined
    )

    mediaRecorder.ondataavailable = event => {
      if (event.data && event.data.size > 0) {
        audioChunks.push(event.data)
      }
    }

    mediaRecorder.onstop = async () => {
      clearTimeout(recordingTimeout)
      isRecording.value = false

      await uploadAudio()
      stopMicrophone()
    }

    mediaRecorder.start()
    isRecording.value = true

    recordingTimeout = setTimeout(() => {
      stopRecording()
    }, 15000)
  } catch (error) {
    console.error('Ошибка доступа к микрофону:', error)
    isRecording.value = false
    stopMicrophone()
  }
}

function stopRecording() {
  if (!mediaRecorder) return

  if (mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }
}

function stopMicrophone() {
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop())
    mediaStream = null
  }
}

async function uploadAudio() {
  if (!audioChunks.length) return

  try {
    isUploading.value = true

    const mimeType = mediaRecorder?.mimeType || 'audio/webm'
    const audioBlob = new Blob(audioChunks, { type: mimeType })

    const formData = new FormData()
    formData.append('file', audioBlob, getAudioFileName(mimeType))

    const res = await fetch(`${backendURL}transcribe`, {
      method: 'POST',
      body: formData,
      credentials: 'include'
    })

    if (!res.ok) {
      throw new Error(`Ошибка backend: ${res.status}`)
    }

    const data = await res.json()
    const text = data?.text

    if (text) {
      searchBody.value = text
    }
  } catch (error) {
    console.error('Ошибка отправки аудио:', error)
  } finally {
    isUploading.value = false
    audioChunks = []
  }
}

function getSupportedMimeType() {
  const types = [
    'audio/webm;codecs=opus',
    'audio/webm',
    'audio/mp4',
    'audio/mpeg'
  ]

  return types.find(type => MediaRecorder.isTypeSupported(type))
}

function getAudioFileName(mimeType) {
  if (mimeType.includes('mp4')) return 'voice.mp4'
  if (mimeType.includes('mpeg')) return 'voice.mp3'
  return 'voice.webm'
}
const getDigestPic = (digest) => {
  if (!digest.tags || !digest.tags.length) {
    return defaultImage // ← лежит в public/images/
  }

  for (const tagValue of digest.tags) {
    const found = availableTags.find((t) => t.value === tagValue)
    if (found?.digest_pic) {
      const assetPath = getImageAssetPath(found.digest_pic)
      if (assetPath) return assetPath
    }
  }

  return defaultImage
}

const getLeftStyle = (digest) => {
  if (!digestColors[digest.id]) {
    const idx = Math.floor(Math.random() * colorPalette.length)
    digestColors[digest.id] = colorPalette[idx]
  }
  return { backgroundColor: digestColors[digest.id] }
}

const clearSelection = (type) => {
  if (type === 'sources') selectedSources.value = []
  if (type === 'tags') selectedTags.value = []
}

const fetchDigests = async (reset) => {
  isDigestLoading.value = true
  try {
    const params = new URLSearchParams()
    if (searchBody.value) params.append('search_body', searchBody.value)
    if (dateFrom.value) params.append('date_from', formatToISO(dateFrom.value))
    if (dateTo.value) params.append('date_to', formatToISO(dateTo.value))
    selectedTags.value.forEach((tag) => params.append('tags', tag.label))
    selectedSources.value.forEach((src) => params.append('sources', src.label))
    params.append('subscribe_only', subscribeOnly.value ? 'true' : 'false')
    params.append('page_number', nextPage.value)
    params.append('matches_per_page', matchesPerPage)

    const res = await fetch(`${backendURL}digests/search?${params.toString()}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
    })
    if (!res.ok) throw new Error('Ошибка при получении дайджестов')
    const data = await res.json()
    if (reset) digests.value = data.digests || []
    else digests.value = [...digests.value, ...(data.digests || [])]
    nextPage.value = data.next_page
    isAuthed.value = data.is_authed
  } catch (e) {
    console.error(e)
  } finally {
    isDigestLoading.value = false
  }
}

const fetchFeaturedDigests = async () => {
  try {
    const params = new URLSearchParams()
    params.append('amount', 3)
    const res = await fetch(
      //'https://d859459784894b3fa1c10b75490e2c56.api.mockbin.io/'
      backendURL + `subscriptions/most_rated?${params.toString()}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
    })
    if (!res.ok) throw new Error('Ошибка при получении featured дайджестов')
    const data = await res.json()
    featuredDigests.value = data.subscriptions || []
  } catch (e) {
    console.error(e)
  }
}

const onSearch = () => {
  nextPage.value = 0
  fetchDigests(true)
}

const onLoadMore = () => {
  if (nextPage.value === -1) return
  fetchDigests(false)
}

const resetFilters = () => {
  searchBody.value = ''
  dateFrom.value = null
  dateTo.value = null
  selectedTags.value = []
  selectedSources.value = []
  subscribeOnly.value = false
}

onMounted(async () => {
  try {
    await Promise.all([fetchFeaturedDigests(), fetchDigests(false)])
  } catch (e) {
    console.error('Ошибка при загрузке дайджестов:', e)
  } finally {
    isLoading.value = false
  }
})
onBeforeUnmount(() => {
  clearTimeout(recordingTimeout)

  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }

  stopMicrophone()
})
</script>

<style scoped lang="scss">
.description {
  display: -webkit-box;
  -webkit-line-clamp: 10;
  line-clamp: 10;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 0.875rem;
}

@media (max-width: 600px) {
  .description {
    -webkit-line-clamp: 3;
    line-clamp: 3;
  }
}
.lightBgClass {
  background-color: $lightaccent;
}
</style>
