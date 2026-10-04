<template>
  <div>
    <div class="q-py-md">
      <q-input
        v-model="newCommentText"
        placeholder="Оставьте персональную заметку"
        outlined
        type="textarea"
        class="q-mb-md"
        style="border-radius: 8px"
      />
      <div class="row justify-end">
        <q-btn
          label="Отправить"
          no-caps
          text-color="secondary"
          color="primary"
          @click="submitComment"
          :disable="isPosting || !newCommentText.trim()"
        />
      </div>
    </div>

    <div v-if="isLoadingComments" class="row justify-center q-pa-md">
      <q-spinner-dots size="30px" color="primary" />
    </div>

    <div v-else>
      <q-card
        v-for="comment in comments"
        :key="comment.id"
        class="q-pa-sm q-mb-md"
        :class="['q-px-md q-py-sm', { 'no-shadow': $q.dark.isActive }]"
        style="position: relative; border-radius: 8px"
      >
        <div class="row items-center justify-between">
          <div class="row items-center text-caption">
            <q-icon name="event" class="q-mr-xs" />
            {{ formatCommentDate(comment) }}
          </div>
          <div>
            <q-btn flat round dense size="sm" icon="edit" @click="enableEdit(comment)" />
            <q-btn flat round dense size="sm" icon="delete" @click="deleteComment(comment)" />
          </div>
        </div>

        <div class="q-mt-xs">
          <!-- Редактируемый комментарий -->
          <div v-if="editingCommentId === comment.id">
            <q-input v-model="editCommentText" outlined dense autogrow style="border-radius: 8px" />
            <div class="row q-mt-sm">
              <q-btn
                label="Сохранить"
                no-caps
                text-color="secondary"
                color="primary"
                @click="updateComment(comment)"
                class="q-mr-sm"
              />
              <q-btn label="Отмена" no-caps color="secondary" flat @click="cancelEdit" />
            </div>
          </div>
          <div v-else>
            {{ comment.text }}
          </div>
        </div>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { backendURL } from 'src/data/lookups'

const props = defineProps({
  article_id: {
    type: [String, Number],
    required: true
  }
})

const $q = useQuasar()
const router = useRouter()

const comments = ref([])
const newCommentText = ref('')
const isLoadingComments = ref(false)
const isPosting = ref(false)
const editingCommentId = ref(null)
const editCommentText = ref('')

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return isNaN(date.getTime()) ? '' : date.toLocaleDateString('ru-RU')
}

function formatCommentDate(comment) {
  const created = formatDate(comment.created_at)
  if (comment.updated_at && comment.updated_at !== comment.created_at) {
    const updated = formatDate(comment.updated_at)
    return `${created} (изм. ${updated})`
  }
  return created
}

async function fetchComments() {
  isLoadingComments.value = true
  try {
    const res = await fetch(
      `${backendURL}documents/${props.article_id}/comment`,
      { credentials: 'include' }
    )
    if (!res.ok) throw new Error(`Ошибка загрузки комментариев: ${res.status}`)
    const data = await res.json()
    comments.value = data.comments
  } catch (err) {
    console.error(err)
    $q.notify({ message: err.message, type: 'negative', position: 'top' })
  } finally {
    isLoadingComments.value = false
  }
}

async function submitComment() {
  if (!newCommentText.value.trim()) return
  isPosting.value = true
  try {
    const res = await fetch(
      `${backendURL}documents/${props.article_id}/comment`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ text: newCommentText.value })
      }
    )
    if (!res.ok) {
      if (res.status === 401) { router.replace('/login'); return }
      throw new Error(`Ошибка отправки комментария: ${res.status}`)
    }
    const posted = await res.json()
    comments.value.push(posted)
    newCommentText.value = ''
  } catch (err) {
    console.error(err)
    $q.notify({ message: err.message, type: 'negative', position: 'top' })
  } finally {
    isPosting.value = false
  }
}

function enableEdit(comment) {
  editingCommentId.value = comment.id
  editCommentText.value = comment.text
}

function cancelEdit() {
  editingCommentId.value = null
  editCommentText.value = ''
}

async function updateComment(comment) {
  if (!editCommentText.value.trim()) return
  try {
    const res = await fetch(
      `${backendURL}documents/${props.article_id}/comment/${comment.id}`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ text: editCommentText.value })
      }
    )
    if (!res.ok) {
      if (res.status === 401) { router.replace('/login'); return }
      throw new Error(`Ошибка обновления комментария: ${res.status}`)
    }
    const updated = await res.json()
    const idx = comments.value.findIndex(c => c.id === comment.id)
    if (idx !== -1) comments.value.splice(idx, 1, updated)
    cancelEdit()
  } catch (err) {
    console.error(err)
    $q.notify({ message: err.message, type: 'negative', position: 'top' })
  }
}

async function deleteComment(comment) {
  try {
    const res = await fetch(
      `${backendURL}documents/${props.article_id}/comment/${comment.id}`,
      { method: 'DELETE', credentials: 'include' }
    )
    if (!res.ok) {
      if (res.status === 401) { router.replace('/login'); return }
      throw new Error(`Ошибка удаления комментария: ${res.status}`)
    }
    const idx = comments.value.findIndex(c => c.id === comment.id)
    if (idx !== -1) comments.value.splice(idx, 1)
  } catch (err) {
    console.error(err)
    $q.notify({ message: err.message, type: 'negative', position: 'top' })
  }
}

onMounted(fetchComments)
</script>

<style scoped></style>