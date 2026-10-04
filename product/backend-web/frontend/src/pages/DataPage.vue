<template>
  <q-page class="q-pa-md page-body">
    <q-btn label="к другим настройкам" @click="toggleDrawer" class="q-mb-xl" outline />
    <div class="text-h4 caption q-mb-lg">Управление папками</div>
    <q-card>
      <q-card-section>
        <div v-if="folders.length === 0" class="text-center q-pa-md">
          У вас нет папок, создайте их на странице с документами
        </div>
        <div v-else>
          <div v-for="(folder, index) in folders" :key="folder.name" class="q-mb-md">
            <div class="row items-center">
              <div class="col">
                <div v-if="!folder.editing">
                  <span>{{ folder.name }}</span>
                  <div v-if="folder.error" class="text-negative text-caption">
                    {{ folder.error }}
                  </div>
                </div>
                <div v-else>
                  <q-input v-model="folder.newName" dense autofocus />
                  <div v-if="folder.error" class="text-negative text-caption">
                    {{ folder.error }}
                  </div>
                </div>
              </div>
              <div class="col-auto">
                <!-- Кнопка редактирования/сохранения -->
                <q-btn
                  round
                  dense
                  flat
                  v-if="!folder.editing"
                  icon="edit"
                  :color="$q.dark.isActive ? 'primary' : 'secondary'"
                  @click="enableEditing(index)"
                >
                  <q-tooltip
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-primary text-secondary"
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    Изменить название папки
                  </q-tooltip>
                </q-btn>

                <template v-else>
                  <q-btn
                    round
                    dense
                    flat
                    v-if="!folder.loading"
                    icon="check"
                    @click="saveFolder(index)"
                  />
                  <q-spinner-dots v-else size="18px" color="primary" />
                </template>
                <q-btn
                  round
                  dense
                  flat
                  icon="download"
                  :color="$q.dark.isActive ? 'primary' : 'secondary'"
                  @click="downloadFolder(folder.name)"
                >
                  <q-tooltip
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-primary text-secondary"
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    Выгрузить документы из папки в формате JSON
                  </q-tooltip>
                </q-btn>
                <q-btn round dense flat icon="delete" color="negative" @click="deleteFolder(index)">
                  <q-tooltip
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-primary text-secondary"
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    Убрать все документы из папки и удалить ее
                  </q-tooltip>
                </q-btn>
              </div>
            </div>
            <q-separator v-if="index < folders.length - 1" />
          </div>
          <div class="q-mt-lg">
            <q-toggle v-model="includeAnnotations" label="Включить аннотации в выгрузку">
              <q-tooltip
                anchor="bottom middle"
                self="top middle"
                class="bg-primary text-secondary"
                transition-show="scale"
                transition-hide="scale"
              >
                Добавляет аннотации в выгрузку папок в формате JSON
              </q-tooltip>
            </q-toggle>
          </div>
        </div>
      </q-card-section>
    </q-card>
    <ConfirmDialog
      v-model="deleteDialog"
      title="Внимание!"
      message="Вы уверены, что хотите удалить папку?"
      checkbox-label="Я понимаю, что делаю"
      confirm-label="ОК"
      @confirm="confirmDelete"
    />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import useDrawer from 'src/composables/useDrawer'
import ConfirmDialog from 'src/components/ConfirmDialog.vue'
import { backendURL } from 'src/data/lookups'
import { useQuasar } from 'quasar'
const { toggleDrawer } = useDrawer()
const $q = useQuasar()

const folders = ref([])


const includeAnnotations = ref(false)

const fetchFolders = async () => {
  try {
    const response = await fetch(backendURL + 'packages', { credentials: 'include' })
    const data = await response.json()
    folders.value = data.folders.map((name) => ({
      name,
      newName: name,
      editing: false,
      error: false,
      loading: false,
    }))
  } catch (error) {
    console.error('Ошибка при загрузке папок', error)
  }
}

const enableEditing = (index) => {
  folders.value[index].editing = true
  folders.value[index].error = false
}

const saveFolder = async (index) => {
  const folder = folders.value[index]
  const newName = folder.newName.trim()

  if (newName === folder.name) {
    folder.editing = false
    return
  }

  const duplicate = folders.value.find(
    (f, idx) => idx !== index && f.name.toLowerCase() === newName.toLowerCase(),
  )
  if (duplicate) {
    folder.error = 'Папка с таким названием уже существует'
    folder.newName = folder.name
    return
  }

  // Если дошли сюда, значит обновление разрешено (даже если изменение только регистра)
  folder.loading = true
  try {
    const params = new URLSearchParams()
    params.append('new_name', newName)
    const response = await fetch(
      // 'https://8be73c6cb1434fa6a55467ff489377b5.api.mockbin.io/'
      backendURL + `packages/${folder.name}/change_name?${params.toString()}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
    })
    folder.loading = false
    if (!response.ok) {
      folder.error = 'Что-то пошло не так при изменении имени папки'
      folder.newName = folder.name
      folder.editing = false
      return
    }
    folder.name = newName
    folder.editing = false
    folder.error = false
  } catch (error) {
    console.error('Ошибка при обновлении папки', error)
    folder.loading = false
    folder.error = 'Что-то пошло не так при изменении имени папки'
    folder.newName = folder.name
    folder.editing = false
  }
}

const downloadFolder = async (folderName) => {
  try {
    const params = new URLSearchParams()
    params.append('include_annotations', includeAnnotations.value.toString())
    const response = await fetch(backendURL + `packages/${folderName}/docs?${params.toString()}`, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    })
    if (!response.ok) {
      throw new Error('Ошибка скачивания')
    }
    const jsonData = await response.json()
    const jsonString = JSON.stringify(jsonData, null, 2)
    const blob = new Blob([jsonString], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${folderName}_export.json`);
    link.click()
    setTimeout(() => {
      URL.revokeObjectURL(url)
    }, 100)
  } catch (error) {
    console.error('Download error:', error)
    $q.notify({
      type: 'negative',
      message: 'Не удалось выгрузить папку',
      position: 'top',
    })
  }
}

const deleteDialog = ref(false)
const folderToDelete = ref(null)

const deleteFolder = async (index) => {
  folderToDelete.value = { index, name: folders.value[index].name }
  deleteDialog.value = true
}

const confirmDelete = async () => {
  if (folderToDelete.value !== null) {
    try {
      const folder = folders.value[folderToDelete.value.index]
      const response = await fetch(backendURL + `packages/${folder.name}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ folderName: folder.name }),
        credentials: 'include',
      })
      if (!response.ok) {
        throw new Error('Ошибка удаления')
      }
      folders.value.splice(folderToDelete.value.index, 1)
    } catch (error) {
      console.error('Ошибка при удалении папки', error)
    }
    folderToDelete.value = null
  }
}

onMounted(() => {
  fetchFolders()
})
</script>

<style scoped>
</style>
