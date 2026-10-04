<template>
  <div class="page-body">
    <search-section :is_authed="isAuthed" @search="onSearch" />
    <div class="q-mt-md">
      <document-card
        v-for="doc in documents"
        :key="doc.id"
        :doc="doc"
        :is_authed="isAuthed"
      />
    </div>
    <div v-if="is_loading" class="flex flex-center column">
      Загружаем документы
    <q-spinner-dots size="80px" color="primary"/>
    </div>

    <div class="q-mt-md q-mb-md flex flex-center" v-if="nextPage !== -1">
      <q-btn
        label="Показать еще"
        color="primary"
        no-caps
        text-color="dark"
        @click="onLoadMore"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import SearchSection from 'src/components/SearchSection.vue'
import DocumentCard from 'src/components/DocumentCard.vue'
import { backendURL } from 'src/data/lookups'
import { useQuasar } from 'quasar'

const documents = ref([])
const nextPage = ref(0)
const matchesPerPage = 20
const isAuthed = ref(false)
const currentSearchPayload = ref({})
const is_loading = ref(true)
const $q = useQuasar()
const router = useRouter()

onMounted(() => {
  const initialPayload = {
    search_mode: 'exact',
    search_body: '',
    sources: [],
    tags: [],
    date_from: null,
    date_to: null,
    search_in_text: false
  }
  onSearch(initialPayload)
})

function onSearch(newPayload) {
  nextPage.value = 0
  documents.value = []
  currentSearchPayload.value = { ...newPayload }
  fetchDocuments(true)
}

function onLoadMore() {
  if (nextPage.value === -1) return
  fetchDocuments(false)
}

async function fetchDocuments(reset) {
  const payload = {
    ...currentSearchPayload.value,
    next_page: nextPage.value,
    matches_per_page: matchesPerPage
  }
  is_loading.value = true
  try {
    const response = await fetch(
      `${backendURL}documents/search`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      }
    )
    if (!response.ok) {
      if (response.status === 401) {
        router.replace('/login')
      }
      throw new Error('Ошибка при поиске')
    }
    const data = await response.json()
    const { documents: docs, next_page, is_authed } = data

    if (reset) {
      documents.value = docs
    } else {
      documents.value = [...documents.value, ...docs]
    }

    nextPage.value = next_page
    isAuthed.value = is_authed
  } catch (error) {
    console.error('Ошибка поиска:', error)
    $q.notify({ message: "Ошибка поиска", type: 'negative', position: 'top' })
  } finally {
    is_loading.value = false
  }
}
</script>

<style scoped lang="scss">
</style>
