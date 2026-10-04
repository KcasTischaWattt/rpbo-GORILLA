<template>
  <div class="folder-bookmark">
    <q-icon name="bookmark" color="primary" @click="toggleMenu" class="cursor-pointer" size="sm" />

    <q-menu anchor="bottom left" self="top left" transition-show="scale" transition-hide="scale">
      <q-card style="min-width: 250px; max-height: 300px; overflow-y: auto">
        <q-card-section>
          <div v-if="loading" class="text-center q-pa-md">
            <q-spinner />
          </div>
          <div v-else>
            <q-item
              clickable
              v-for="(folder, index) in folders"
              :key="index"
              @click="toggleFolder(folder)"
              :class="folder.is_included ? ($q.dark.isActive ? 'text-primary' : 'text-accent') : ''"
            >
              <q-item-section>
                <div class="folder-name">{{ folder.name }}</div>
              </q-item-section>
              <q-item-section side>
                <q-spinner-dots
                  v-if="folder.pending"
                  :color="$q.dark.isActive ? 'primary' : 'accent'"
                  size="18px"
                />
                <q-icon
                  v-else-if="folder.is_included"
                  name="check"
                  :color="$q.dark.isActive ? 'primary' : 'accent'"
                />
                <!-- Если ни одно из условий не выполнено, можно оставить секцию пустой -->
              </q-item-section>
            </q-item>

            <div v-if="errorMsg" class="text-negative q-pa-sm">
              {{ errorMsg }}
            </div>
            <div v-if="newFolderVisible" class="row items-center q-pa-xs">
              <q-input v-model="newFolderName" dense placeholder="Название папки" style="flex: 1" />
              <q-icon name="check" class="cursor-pointer q-ml-xs" @click.stop="createFolder" />
              <q-icon name="close" class="cursor-pointer q-ml-xs" @click.stop="cancelNewFolder" />
            </div>

            <q-separator />
            <q-btn
              flat
              no-caps
              label="Добавить новую папку"
              @click="showNewFolderInput"
              class="full-width"
            />
          </div>
        </q-card-section>
      </q-card>
    </q-menu>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import { backendURL } from 'src/data/lookups'

const router = useRouter()

const props = defineProps({
  documentId: {
    type: Number,
    required: true,
  },
})

const $q = useQuasar()
const folders = ref([])
const loading = ref(false)
const errorMsg = ref('')
const newFolderVisible = ref(false)
const newFolderName = ref('')

function toggleMenu() {
  fetchFolders()
}

async function fetchFolders() {
  loading.value = true
  errorMsg.value = ''
  try {
    const response = await fetch(
      // 'https://eb7e097b1cf3426985d690585bff64e0.api.mockbin.io/'
      backendURL + `documents/${props.documentId}/packages`,
      { credentials: 'include' },
      )
    if (!response.ok) {
      if (response.status === 401) {
        router.replace('/login')
      } else {
        throw new Error('Ошибка при загрузке папок')
      }
    }
    const data = await response.json()
    folders.value = data.folders.map((folder) => ({ ...folder, pending: false }))
  } catch (error) {
    errorMsg.value = error.message || 'Ошибка при загрузке папок'
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function toggleFolder(folder) {
  if (folder.pending) return
  errorMsg.value = ''
  folder.pending = true
  try {
    if (folder.is_included) {
      // Удаление документа из папки
      const response = await fetch(
        // 'https://a8135a0ccf3549c2a69c7a1ea0203288.api.mockbin.io/',
        backendURL + `documents/${props.documentId}/remove_from/${folder.name}`,
         {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
      })
      if (!response.ok) {
        throw new Error('Ошибка при обновлении папки')
      }
      folder.is_included = false
    } else {
      // Добавление документа в папку
      const response = await fetch(
        //'https://a8135a0ccf3549c2a69c7a1ea0203288.api.mockbin.io/',
        backendURL + `documents/${props.documentId}/put_into/${folder.name}`,
        {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
      })
      if (!response.ok) {
        throw new Error('Ошибка при обновлении папки')
      }
      folder.is_included = true
    }
  } catch (error) {
    errorMsg.value = error.message || 'Ошибка при обновлении папки'
    console.error(error)
  } finally {
    folder.pending = false
  }
}

function showNewFolderInput() {
  newFolderVisible.value = true
  errorMsg.value = ''
  newFolderName.value = ''
}

function cancelNewFolder() {
  newFolderVisible.value = false
  newFolderName.value = ''
  errorMsg.value = ''
}

async function createFolder() {
  errorMsg.value = ''
  const trimmedName = newFolderName.value.trim()
  if (!trimmedName) {
    errorMsg.value = 'Название папки не может быть пустым'
    return
  }
  const exists = folders.value.some(
    (folder) => folder.name.toLowerCase() === trimmedName.toLowerCase(),
  )
  if (exists) {
    errorMsg.value = 'Папка с таким названием уже существует'
    return
  }
  try {
    const response = await fetch(
      backendURL + `packages/create`,
      // 'https://a8135a0ccf3549c2a69c7a1ea0203288.api.mockbin.io/',
       {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({
        name: trimmedName,
      }),
    })
    if (!response.ok) {
      let errMsg = 'Ошибка при создании папки'
      try {
        const errData = await response.json()
        if (errData.message) {
          errMsg = errData.message
        }
      } catch (jsonError) {
        console.error(jsonError)
      }
      throw new Error(errMsg)
    }
    folders.value.push({
      name: trimmedName,
      is_included: false,
      pending: false,
    })
    newFolderVisible.value = false
    newFolderName.value = ''
  } catch (error) {
    errorMsg.value = error.message || 'Ошибка при создании папки'
    console.error(error)
  }
}
</script>

<style scoped>
.folder-bookmark {
  display: inline-block;
}

.folder-name {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
