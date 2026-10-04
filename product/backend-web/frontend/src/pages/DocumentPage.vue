<template>
  <ServerResponseSpinner v-if="isLoading" />
  <div v-if="!isLoading" class="page-body q-pa-md">
    <BackButton to="/documents" />
    <q-page v-if="documentData">
      <q-card :class="['q-px-md q-py-sm', { 'no-shadow': $q.dark.isActive }]">
        <div class="row items-center justify-between">
          <div class="text-caption text-left">
            {{ documentData?.date }} | {{ documentData?.source }}
          </div>

          <div class="row items-center">
            <FolderBookmark v-if="documentData.is_authed" :documentId="documentData?.id" />
          </div>
        </div>
        <div class="text-h5 q-my-sm">
          {{ documentData?.title }}
        </div>

        <div class="q-my-sm">
          <DocumentTags :tags="documentData?.tags" />
        </div>

        <div class="q-my-sm">
          <div class="q-mb-xs" v-if="documentData.is_authed">
            Оцените подобранные теги:
            <q-rating
              v-model="documentData.rating_classification"
              max="5"
              color="primary"
              icon="star"
              size="sm"
              no-reset
              @update:model-value="onRatingChange"
            />
          </div>
        </div>

        <q-expansion-item expand-separator>
          <template v-slot:header>
            <q-item-section avatar>
              <q-avatar icon="receipt" color="primary" text-color="secondary" />
            </q-item-section>
            <q-item-section class="text-h6"> Краткое содержание </q-item-section>
            <q-item-section side>
              <div class="row items-center">
                <q-icon name="access_time" size="xs" class="q-mr-xs" />
                <span>{{ readingTime }}</span>
              </div>
            </q-item-section>
          </template>

          <div class="q-pa-sm">
            {{ documentData?.summary }}
          </div>
        </q-expansion-item>

        <div ref="textContainer" class="q-my-md">
          {{ documentData?.text }}
        </div>
        <div v-if="documentData?.URL" class="q-mt-sm text-body2">
          Оригинал:
          <a
            :href="documentData?.URL"
            target="_blank"
            rel="noopener noreferrer"
            :class="$q.dark.isActive ? 'text-primary' : 'text-secondary'"
          >
            {{ documentData?.URL }}
          </a>
        </div>
      </q-card>

      <div class="row items-start wrap justify-between q-gutter-sm q-mt-md">
        <q-btn
          v-if="documentData?.URL"
          label="Читать оригинал"
          color="primary"
          size="md"
          no-caps
          :href="documentData?.URL"
          target="_blank"
          rel="noopener noreferrer"
          text-color="secondary"
          class="col-auto"
        />

        <div v-if="documentData.is_moderator" class="row items-start wrap col-auto">
          <q-btn
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
            label="Удалить"
            color="secondary"
            size="md"
            no-caps
            icon-right="delete"
            @click="showDeleteDialog = true"
          />
        </div>
      </div>

      <CommentsSection v-if="documentData.is_authed" :article_id="documentData?.id" />
      <LockedContent
        v-else
        description="Войдите в аккаунт, чтобы ставить рейтинги, оставлять персональные заметки, аннотировать текст и добавлять статьи в папки!"
      />

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
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import DocumentTags from '../components/DocumentTags.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import BackButton from 'src/components/BackButton.vue'
import CommentsSection from 'src/components/CommentsSection.vue'
import FolderBookmark from 'src/components/FolderBookmark.vue'
import LockedContent from 'src/components/LockedContent.vue'
import ServerResponseSpinner from 'src/components/ServerResponseSpinner.vue'
import { Recogito } from '@recogito/recogito-js'
import '@recogito/recogito-js/dist/recogito.min.css'
import '../css/customRecogito.scss'
import { backendURL } from 'src/data/lookups'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const documentData = ref(null)
const textContainer = ref(null)
let recogitoInstance = null

const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const isLoading = ref(true)

const readingTime = computed(() => {
  const summary = documentData.value?.summary || ''
  const wordsCount = summary.split(/\s+/).filter(Boolean).length
  const minutes = wordsCount / 120
  return minutes < 1 ? '<1 мин' : Math.ceil(minutes) + ' мин'
})

onMounted(async () => {
  const id = route.params.id
  try {
    const response = await fetch(
     //`https://a1561caa2fca4614bc614fc099f6af6e.api.mockbin.io/`
     backendURL + `documents/${id}`, {credentials: 'include'}
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

    documentData.value = await response.json()
    if (documentData.value.rating_classification == null) {
      documentData.value.rating_classification = 0;
    }
    console.log(documentData)
  } catch (error) {
    console.error('Ошибка при загрузке документа:', error)
  } finally {
    isLoading.value = false
  }
})

watch(
  [documentData, isLoading],
  async ([newDocument, loading]) => {
    // Ждем, пока DOM обновится (то есть элемент с ref появится)
    await nextTick()
    //console.log('Watcher triggered:', newDocument, loading, textContainer.value)

    if (newDocument && !loading && textContainer.value) {
      let widgets = []
      widgets.push('COMMENT')
      recogitoInstance = new Recogito({
        content: textContainer.value,
        readOnly: !newDocument.is_authed,
        allowEmpty: true,
        locale: 'RU',
        widgets: widgets,
      })

      nextTick(() => {
        const observer = new MutationObserver((mutations) => {
          mutations.forEach((mutation) => {
            if (
              mutation.target.matches('.r6o-editable-text') &&
              mutation.target.hasAttribute('disabled')
            ) {
              mutation.target.removeAttribute('disabled')
            }
          })
        })

        observer.observe(document.body, {
          attributes: true,
          subtree: true,
        })
      })

      const annotations = newDocument.annotations.map((annot) => ({
        id: annot.id,
        type: 'Annotation',
        // Преобразуем массив комментариев в массив объектов TextualBody
        body: annot.comments.map((comment) => ({
          type: 'TextualBody',
          value: comment,
          purpose: 'commenting',
        })),
        target: {
          // Передаём селектор как массив для поддержки нескольких селекторов
          selector: [
            {
              type: 'TextPositionSelector',
              start: annot.start,
              end: annot.end,
            },
          ],
        },
      }))

      recogitoInstance.setAnnotations(annotations)

      recogitoInstance.on('createAnnotation', async (annotation, overrideId) => {
        // Получаем текст из annotation.body, если он есть
        const note = annotation.body && annotation.body[0] ? annotation.body[0].value : ''

        // Ищем селектор с типом 'TextPositionSelector'
        const posSelector = annotation.target.selector.find(
          (s) => s.type === 'TextPositionSelector',
        )
        const quoteSelector = annotation.target.selector.find(
          (s) => s.type === 'TextQuoteSelector',
        )
        if (!quoteSelector.exact) {
          recogitoInstance.removeAnnotation(annotation)
          return
        }

        const payload = {
          start: posSelector ? posSelector.start : null,
          end: posSelector ? posSelector.end : null,
          comments: [note],
        }

        try {
          const response = await fetch(
            //'https://55236949e4444103acfe1c01326c4084.api.mockbin.io/',
            backendURL + `documents/${newDocument.id}/annotate`,
            {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(payload),
              credentials: 'include',
            },
          )
          if (response.ok) {
            const data = await response.json()
            overrideId(data.id)
          } else {
            console.error('Ошибка при сохранении аннотации:', response.status)
            recogitoInstance.removeAnnotation(annotation)
            if (response.status === 401) {
              router.replace('/login')
            }
          }
        } catch (error) {
          console.error('Ошибка при сохранении аннотации:', error)
          recogitoInstance.removeAnnotation(annotation)
        }
      })

      recogitoInstance.on('deleteAnnotation', async (annotation) => {
        console.log('delete-annotation')
        try {
          const response = await fetch(
            //'https://8be73c6cb1434fa6a55467ff489377b5.api.mockbin.io/',
            backendURL + `documents/${newDocument.id}/annotate/${annotation.id}`,
            {
              method: 'DELETE',
              credentials: 'include',
            },
          )
          if (response.ok) {
            console.log('Аннотация успешно удалена')
          } else {
            recogitoInstance.addAnnotation(annotation)
            if (response.status === 401) {
              router.replace('/login')
            } else {
              console.error('Ошибка при удалении аннотации:', response.status)
            }
          }
        } catch (error) {
          console.error('Ошибка при удалении аннотации:', error)
          recogitoInstance.addAnnotation(annotation)
        }
      })

      recogitoInstance.on('updateAnnotation', async (annotation, previous) => {
        const posSelector = annotation.target.selector.find(
          (s) => s.type === 'TextPositionSelector',
        )

        const updatedPayload = {
          start: posSelector ? posSelector.start : null,
          end: posSelector ? posSelector.end : null,
          comments: annotation.body.map((bodyItem) => bodyItem.value),
        }

        try {
          const response = await fetch(
            // 'https://8be73c6cb1434fa6a55467ff489377b5.api.mockbin.io/',
            backendURL + `documents/${newDocument.id}/annotate/${annotation.id}`,
            {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(updatedPayload),
              credentials: 'include',
            },
          )
          if (response.ok) {
            console.log('Аннотация успешно обновлена')
          } else {
            recogitoInstance.setAnnotations([previous])
            if (response.status === 401) {
              router.replace('/login')
            } else {
              console.error('Ошибка при обновлении аннотации:', response.status)
            }
          }
        } catch (error) {
          console.error('Ошибка при обновлении аннотации:', error)
          // Откат изменений
          recogitoInstance.setAnnotations([previous])
        }
      })
    }
  },
  { immediate: true },
)
async function onRatingChange(newRating) {
  try {
    const id = route.params.id
    const postResponse = await fetch(backendURL + `documents/${id}/rate`, {
      method: 'PATCH',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ classification_rating: newRating, summary_rating: documentData.value.rating_summary }),
    })
    if (!postResponse.ok) {
      if (postResponse.status === 401) {
        router.replace('/login')
      } else {
        console.error('Ошибка при отправке рейтинга:', postResponse.status)
      }
    } else {
      documentData.value.rating_classification = newRating
      $q.notify({ message: "Рейтинг сохранен, спасибо!", type: 'positive', position: 'top' })
    }
  } catch (err) {
    $q.notify({ message: "Ошибка при сохранении рейтинга", type: 'negative', position: 'top' })
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

async function onDeleteConfirm() {
  const currentId = route.params.id
  try {
    const response = await fetch(backendURL + `documents/${currentId}`, {
      method: 'DELETE',
      credentials: 'include',
    })
    if (!response.ok) {
      console.error('Ошибка при удалении документа:', response.status)
    } else {
      console.log('Документ удалён')
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

.r6o-editable-text {
  cursor: text !important;
  color: #b61212 !important;
  opacity: 1 !important;
}
</style>
