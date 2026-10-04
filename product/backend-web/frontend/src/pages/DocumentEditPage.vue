<template>
  <div class="page-body q-mt-md">
    <q-page>
      <BackButton :to="`/documents/${article.id}`" />
      <div
        class="text-h5 caption"
        :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
      >
        Редактирование статьи
      </div>

      <q-form @submit.prevent="onSaveClick" class="q-gutter-md q-mt-md">
        <q-input
          v-model="article.title"
          label="Название"
          type="textarea"
          autogrow
          outlined
          filled
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />

        <q-input
          v-model="article.text"
          label="Текст статьи"
          type="textarea"
          outlined
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />

        <q-input
          v-model="article.summary"
          label="Краткое содержание"
          type="textarea"
          autogrow
          outlined
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />

        <div class="col-12">
          <div class="row q-col-gutter-md">
            <div class="col-4">
              <q-select
                v-model="article.source"
                :options="sources"
                option-label="label"
                option-value="value"
                label="Источник"
                outlined
                dense
                filled
                :color="$q.dark.isActive ? 'primary' : 'accent'"
              />
            </div>

            <div class="col-4">
              <q-input
                v-model="article.date"
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
                        v-model="article.date"
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

            <div class="col-4">
              <q-select
                v-model="article.language"
                :options="languages"
                option-label="label"
                option-value="value"
                label="Язык"
                outlined
                dense
                filled
                :color="$q.dark.isActive ? 'primary' : 'accent'"
              />
            </div>
          </div>
        </div>

        <q-select
          v-model="article.tags"
          :options="tagsList"
          multiple
          option-label="label"
          option-value="value"
          label="Теги"
          outlined
          dense
          filled
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />

        <q-input
          v-model="article.URL"
          label="URL"
          type="textarea"
          autogrow
          outlined
          dense
          filled
          :color="$q.dark.isActive ? 'primary' : 'accent'"
        />

        <q-toggle
          v-model="article.drop_rating_summary"
          label="Сбросить рейтинг краткого содержания"
          color="primary"
        />

        <q-toggle
          v-model="article.drop_rating_classification"
          label="Сбросить рейтинг классификации"
          color="primary"
        />

        <div class="col-12 q-mt-md">
          <div class="row items-start wrap justify-between q-gutter-sm">
            <q-btn
              label="Сохранить"
              color="primary"
              text-color="secondary"
              no-caps
              :loading="isSaving"
              @click="onSaveClick"
            />
            <q-btn label="Отменить" color="secondary" no-caps @click="cancelEdit" />
          </div>
        </div>
      </q-form>

      <ConfirmDialog
        v-model="showConfirmDialog"
        title="Внимание!"
        message="Вы изменили текст статьи. Сохранение этих изменений удалит аннотации на этой статье для всех пользователей корпуса. Уверены, что хотите сохранить изменения?"
        checkboxLabel="Я понимаю, что делаю"
        confirmLabel="Сохранить"
        @confirm="performSave"
        @cancel="onDialogCancel"
      />
    </q-page>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter, useRoute } from 'vue-router'
import {
  availableSources,
  availableTags,
  availableLanguages,
  backendURL,
} from 'src/data/lookups.js'

import ConfirmDialog from 'src/components/ConfirmDialog.vue'
import BackButton from 'src/components/BackButton.vue'

const $q = useQuasar()
const router = useRouter()
const route = useRoute()

const sources = availableSources
const tagsList = availableTags
const languages = availableLanguages

const article = reactive({
  id: '',
  title: '',
  text: '',
  summary: '',
  source: '',
  date: '',
  language: '',
  tags: [],
  URL: '',
  drop_rating_summary: false,
  drop_rating_classification: false,
})

const originalText = ref('')
const isSaving = ref(false)
const showConfirmDialog = ref(false)

const formatToISO = (str) => {
  if (!str) return null
  const [day, month, year] = str.split('/')
  if (!day || !month || !year) return null
  const utcDateMs = Date.UTC(+year, +month - 1, +day)
  return new Date(utcDateMs).toISOString().split('T')[0]
}

const fetchArticle = async () => {
  try {
    const id = route.params.id
    const response = await fetch(`${backendURL}documents/${id}`, {
      method: 'GET',
      credentials: 'include',
    })
    if (!response.ok) throw new Error('Ошибка при получении данных статьи')
    const data = await response.json()

    article.id = data.id
    article.title = data.title
    article.text = data.text
    article.summary = data.summary
    article.date = data.date
    article.language = data.language
    ;(article.tags = tagsList.filter((opt) => data.tags.includes(opt.value))),
      (article.source = sources.find((opt) => opt.value === data.source)),
      (article.URL = data.URL)
    originalText.value = data.text
  } catch (err) {
    $q.notify({ message: err.message, type: 'negative', position: 'top' })
  }
}

const onSaveClick = () => {
  if (article.text !== originalText.value) {
    showConfirmDialog.value = true
  } else {
    performSave()
  }
}

const performSave = async () => {
  isSaving.value = true
  try {
    const now = new Date().toISOString()
    const body = {
      id: article.id,
      title: article.title,
      text: article.text,
      summary: article.summary,
      source: article.source.value,
      date: formatToISO(article.date),
      language: article.language,
      tags: article.tags.map((tag) => tag.value),
      URL: article.URL,
      drop_rating_summary: article.drop_rating_summary,
      drop_rating_classification: article.drop_rating_classification,
      last_edit: now,
    }

    const response = await fetch(`${backendURL}documents/${article.id}/edit`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(body),
    })
    if (!response.ok) {
      throw new Error('Ошибка при сохранении статьи')
    } else {
      router.push(`/documents/${article.id}`)
      $q.notify({
        message: 'Изменения успешно сохранены!',
        type: 'positive',
        position: 'top',
        badgeColor: 'yellow',
        badgeTextColor: 'dark',
      })
    }
  } catch (err) {
    $q.notify({ message: err.message, type: 'negative', position: 'top' })
  } finally {
    isSaving.value = false
    showConfirmDialog.value = false
  }
}

const cancelEdit = () => {
  router.push(`/documents/${article.id}`)
}

const onDialogCancel = () => {
  showConfirmDialog.value = false
}

onMounted(fetchArticle)
</script>
<style scoped lang="scss"></style>
