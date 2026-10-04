<template>
  <div class="search-section q-pa-md no-shadow" bg-color="primary">
    <div class="row items-center">
      <q-select
        v-model="search_mode"
        :options="searchModes"
        option-value="value"
        option-label="label"
        emit-value
        map-options
        filled
        label="Режим"
        label-color="secondary"
        class="col-auto"
        bg-color="primary"
        :color="$q.dark.isActive ? 'primary' : 'accent'"
      />

      <q-input
        v-model="search_body"
        filled
        label="Строка поиска"
        class="col"
        :color="$q.dark.isActive ? 'primary' : 'accent'"
        @keyup.enter="emitSearch"
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
            @click="emitSearch"
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
        class="col-auto search-btn"
        @click="emitSearch"
      />
    </div>

    <div v-if="isRecording" class="text-negative text-caption q-mt-sm">
      Идёт запись... нажмите на микрофон ещё раз, чтобы остановить.
    </div>

    <div v-if="search_mode === 'semantic' || search_mode === 'exact'" class="q-my-sm">
      <q-checkbox
        v-model="search_in_text"
        label="Искать в тексте (иначе только по названию)"
      />
    </div>

    <div v-if="showSources || showTags" class="row q-col-gutter-md">
      <div v-if="showSources" class="col">
        <filter-selector
          v-model="selected_sources"
          :options="availableSources"
          label="Источники"
          dense
          clearable
          :multiple="true"
          class="q-my-sm"
          @clear="clearSelection('sources')"
        />
      </div>

      <div v-if="showTags" class="col">
        <filter-selector
          v-model="selected_tags"
          :options="availableTags"
          label="Тэги"
          dense
          :multiple="true"
          clearable
          class="q-my-sm"
          @clear="clearSelection('tags')"
        />
      </div>
    </div>

    <div v-if="showDateRange" class="row q-col-gutter-md items-center">
      <q-input
        v-model="date_from"
        filled
        dense
        label="Дата (с)"
        mask="##/##/####"
        class="col"
        :color="$q.dark.isActive ? 'primary' : 'accent'"
      >
        <template v-slot:append>
          <q-icon name="event" class="cursor-pointer">
            <q-popup-proxy cover transition-show="scale" transition-hide="scale">
              <q-date
                v-model="date_from"
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

      <q-input
        v-model="date_to"
        filled
        dense
        label="Дата (по)"
        mask="##/##/####"
        class="col"
        :color="$q.dark.isActive ? 'primary' : 'accent'"
      >
        <template v-slot:append>
          <q-icon name="event" class="cursor-pointer">
            <q-popup-proxy cover transition-show="scale" transition-hide="scale">
              <q-date
                v-model="date_to"
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

    <div v-if="showFolder" class="row q-col-gutter-md">
      <div class="col">
        <folder-selector v-model="selected_folder" label="Папка" />
      </div>
    </div>

    <div
      v-if="search_mode === 'semantic' || search_mode === 'exact'"
      class="q-mt-sm"
    >
      <q-btn
        label="Сбросить фильтры"
        color="secondary"
        no-caps
        @click="resetFilters"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { availableSources, availableTags } from 'src/data/lookups.js'
import FilterSelector from './FilterSelector.vue'
import FolderSelector from './FolderSelector.vue'
import { backendURL } from '../data/lookups.js'

const props = defineProps({
  is_authed: { type: Boolean, default: false }
})

const emit = defineEmits(['search'])

const searchModes = [
  { label: 'По смыслу', value: 'semantic' },
  { label: 'По номеру', value: 'id' },
  { label: 'Точный', value: 'exact' }
]

const search_mode = ref('semantic')
const search_body = ref('')
const search_in_text = ref(false)
const selected_sources = ref([])
const selected_tags = ref([])
const date_from = ref(null)
const date_to = ref(null)
const selected_folder = ref(null)

const isRecording = ref(false)
const isUploading = ref(false)

let mediaRecorder = null
let audioChunks = []
let mediaStream = null
let recordingTimeout = null

const showSources = computed(() =>
  search_mode.value === 'semantic' || search_mode.value === 'exact'
)

const showTags = computed(() =>
  search_mode.value === 'semantic' || search_mode.value === 'exact'
)

const showDateRange = computed(() =>
  search_mode.value === 'semantic' || search_mode.value === 'exact'
)

const showFolder = computed(() =>
  (search_mode.value === 'semantic' || search_mode.value === 'exact') &&
  props.is_authed
)

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

    const response = await fetch(`${backendURL}transcribe`, {
      method: 'POST',
      body: formData
    })

    if (!response.ok) {
      throw new Error(`Ошибка backend: ${response.status}`)
    }

    const data = await response.json()
    const text = data?.text

    if (text) {
      search_body.value = text
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

function clearSelection(field) {
  if (field === 'sources') {
    selected_sources.value = []
  } else if (field === 'tags') {
    selected_tags.value = []
  }
}

function resetFilters() {
  selected_sources.value = []
  selected_tags.value = []
  date_from.value = null
  date_to.value = null
  search_in_text.value = false
  search_body.value = ''
  selected_folder.value = null
}

function formatToISO(str) {
  if (!str) return null

  const [day, month, year] = str.split('/')

  if (!day || !month || !year) return null

  const utcMs = Date.UTC(+year, +month - 1, +day)

  return new Date(utcMs).toISOString().split('T')[0]
}

function emitSearch() {
  const payload = {
    search_mode: search_mode.value,
    search_body: search_body.value
  }

  if (showSources.value) {
    payload.sources = selected_sources.value.map(s => s.value)
    payload.tags = selected_tags.value.map(t => t.value)
    payload.date_from = formatToISO(date_from.value)
    payload.date_to = formatToISO(date_to.value)

    if (selected_folder.value) {
      payload.folders = selected_folder.value.map(f => f.value)
    }
  }

  if (search_mode.value === 'exact' || search_mode.value === 'semantic') {
    payload.search_in_text = search_in_text.value
  }

  emit('search', payload)
}

onBeforeUnmount(() => {
  clearTimeout(recordingTimeout)

  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }

  stopMicrophone()
})
</script>

<style scoped lang="scss"></style>