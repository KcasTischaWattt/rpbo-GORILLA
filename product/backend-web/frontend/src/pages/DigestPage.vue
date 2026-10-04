<template>
  <div class="page-body">
    <q-page v-if="!isLoading" class="q-pa-md">
      <BackButton to="/digests" />
      <q-card :class="['q-px-md q-py-sm', { 'no-shadow': $q.dark.isActive }]">
        <div class="row items-center justify-between">
          <div class="text-caption row items-center text-left">
            <q-icon name="calendar_today" class="q-mr-xs" />
            {{ digestData?.date }}
            <q-icon :name="fasCrown" class="q-ml-sm q-mr-xs" />
            {{ digestData?.owner }}
            <q-icon name="star" color="primary" class="q-ml-sm q-mr-xs" />
            {{ digestData?.average_rating != -1 ? digestData?.average_rating : 'Нет оценок' }}
          </div>
        </div>
        <div class="row items-center q-my-sm">
          <div class="text-h4 q-mr-xs">
            {{ digestData?.title }}
          </div>
          <subscription-button
            v-if="digestData?.is_authed"
            :digest="digestData"
            triggeredFrom="digests"
          />
        </div>

        <div>
          <p
            :class="[
              'text-italic',
              { 'text-yellow': $q.dark.isActive, 'text-accent': !$q.dark.isActive },
            ]"
          >
            "{{ digestData?.description }}"
          </p>
        </div>

        <div v-if="digestData?.is_authed" class="q-my-sm">
          <div class="q-mb-xs">
            Оцените качество этого дайджетса:
            <q-rating
              v-model="digestData.user_rating"
              max="5"
              color="primary"
              icon="star"
              size="sm"
              no-reset
              @update:model-value="onRatingChange"
            />
          </div>
        </div>

        <div class="q-my-md">
          {{ digestData?.text }}
        </div>
        <q-separator />
        <div class="q-mt-sm">
          <DocumentTags :tags="digestData?.tags" />
        </div>
        <q-expansion-item
          v-if="digestData?.based_on && digestData?.based_on.length"
          :label="`Показать источники (${digestData?.based_on.length})`"
          class="q-mt-xs q-pa-xs"
        >
          <div v-for="source in digestData?.based_on" :key="source.id" class="q-mb-xs">
            <router-link
              :to="`/documents/${source.id}`"
              class="no-underline cursor-pointer q-ml-md"
              :class="$q.dark.isActive ? 'text-white' : 'text-secondary'"
            >
              {{ source.title }}
            </router-link>
            :
            <a
              :href="source.url"
              target="_blank"
              :class="$q.dark.isActive ? 'text-primary' : 'text-accent'"
            >
              {{ source.url }}
            </a>
          </div>
        </q-expansion-item>
      </q-card>

      <div class="row items-start wrap justify-between q-gutter-sm q-mt-md">
        <q-btn
          label="PDF версия"
          color="secondary"
          size="md"
          icon-right="download"
          no-caps
          @click="downloadPdf"
        />

        <div class="row items-start wrap col-auto">
          <q-btn
            v-if="showAdmin"
            label="Редактировать"
            color="primary"
            size="md"
            class="q-mr-md"
            no-caps
            icon-right="edit"
            text-color="secondary"
            @click="showEditDialog = true"
          />
          <q-btn
            v-if="showAdmin"
            label="Удалить"
            color="secondary"
            size="md"
            class="q-mr-md"
            no-caps
            icon-right="delete"
            @click="showDeleteDialog = true"
          />
        </div>
      </div>

      <ConfirmDialog
        v-model="showEditDialog"
        title="Внимание!"
        message="Редактирование документа уберет аннотации всех пользователей! Редактирование текста документа крайне нежелательно и должно применяться только в экстренных случаях. Документы, подвергшиеся редактированию, будут помечены соответствующим образом."
        confirmLabel="Редактировать"
        checkboxLabel="Я понимаю, что я делаю"
        @confirm="goToEditPage"
        @cancel="onEditCancel"
      />

      <ConfirmDialog
        v-model="showDeleteDialog"
        title="Внимание!"
        message="Удаление документа необратимо."
        confirmLabel="Удалить"
        checkboxLabel="Я понимаю, что я делаю"
        @confirm="onDeleteConfirm"
        @cancel="onDeleteCancel"
      />
    </q-page>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DocumentTags from '../components/DocumentTags.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import SubscriptionButton from '../components/SubscriptionButton.vue'
import BackButton from 'src/components/BackButton.vue'
import { fasCrown } from '@quasar/extras/fontawesome-v6'
import { backendURL } from 'src/data/lookups'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const route = useRoute()
const router = useRouter()
const digestData = ref(null)
const isLoading = ref(true)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const showAdmin = ref(false)

onMounted(async () => {
  try {
    const id = route.params.id
    const response = await fetch(
      `${backendURL}digests/${id}`,
      { credentials: 'include' },
      // `https://93442d81ece6495b95e185b5215b36f8.api.mockbin.io/`
    )
    if (!response.ok) {
      if (response.status === 401) {
        router.replace('/login')
      } else if (response.status === 404) {
        router.replace('/404')
      } else {
        console.error('Ошибка HTTP:', response.status)
      }
      return
    }
    digestData.value = await response.json()
  } catch (error) {
    console.error('Ошибка при загрузке документа:', error)
  } finally {
    isLoading.value = false
  }
})

async function onRatingChange(newRating) {
  try {
    const id = route.params.id
    const postResponse = await fetch(backendURL + `digests/${id}/rate`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ digest_core_rating: newRating }),
    })
    if (!postResponse.ok) {
      $q.notify({
        message: 'Не удалось сохранить рейтинг',
        type: 'negative',
        position: 'top',
      })
      if (postResponse.status === 401) {
        router.replace('/login')
      }
      console.error('Ошибка при отправке рейтинга:', postResponse.status)
    } else {
      digestData.value.user_rating = newRating
      $q.notify({
        message: 'Рейтинг успешно обновлен',
        type: 'positive',
        position: 'top',
        badgeColor: 'yellow',
        badgeTextColor: 'dark',
      })
    }
  } catch (err) {
    console.error('Ошибка при отправке рейтинга:', err)
  }
}

function goToEditPage() {
  const currentId = route.params.id
  router.push(`/documents/${currentId}/edit`)
}

function onEditCancel() {
  console.log('Редактирование отменено')
}


async function downloadPdf() {
  try {
    const response = await fetch(
      `${backendURL}digests/${digestData.value?.id}/pdf`,
      { credentials: 'include' }
    )
    if (!response.ok) {
      throw new Error(`Сервер вернул ${response.status}`)
    }
    const blob = await response.blob()
    const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${digestData.value?.title}_${digestData.value?.id}.pdf`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('Ошибка при скачивании PDF:', err)
    $q.notify({ message: 'Ошибка скачивания PDF', type: 'negative', position: 'top' })
  }
}

async function onDeleteConfirm() {
  const currentId = route.params.id
  try {
    const response = await fetch(`http://localhost:3000/documents/${currentId}`, {
      method: 'DELETE',
      credentials: 'include'
    })
    if (!response.ok) {
      if (response.status === 401) {
        router.replace('/login')
      }
      console.error('Ошибка при удалении документа:', response.status)
    } else {
      router.push('/documents')
    }
  } catch (error) {
    console.error('Ошибка при удалении:', error)
  }
}

function onDeleteCancel() {
  console.log('Удаление отменено')
}
</script>

<style scoped lang="scss">
.q-expansion-item {
  background-color: #f7f7f7 !important;

  .q-dark & {
    background-color: #2b2b2b !important;
  }
}

.no-underline {
  text-decoration: none;
}
</style>
