<template>
  <q-select
    filled
    v-model="selected"
    :label="label"
    :options="filteredOptions"
    use-input
    input-debounce="0"
    @filter="filterItems"
    @popup-show="fetchFolders"
    multiple
    dense
    :loading="loading"
    :color="$q.dark.isActive ? 'primary' : 'accent'"
    clearable
    class="full-width q-my-sm"
  >
    <template v-slot:no-option>
      <q-item>
        <q-item-section class="text-grey">Нет результатов</q-item-section>
      </q-item>
    </template>
  </q-select>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { backendURL } from 'src/data/lookups'

const $q = useQuasar()
const router = useRouter()

const props = defineProps({
  label: {
    type: String,
    default: 'Папка'
  },
  value: {
    type: [String, Object, null],
    default: null
  }
})
const emit = defineEmits(['input'])

const selected = ref(props.value)
const options = ref([])
const filteredOptions = ref([])
const loading = ref(false)


watch(
  () => props.value,
  newVal => {
    selected.value = newVal
  }
)

watch(
  selected,
  newVal => {
    emit('input', newVal)
  }
)

async function fetchFolders () {
  loading.value = true
  try {
    const response = await fetch(
      `${backendURL}packages`,
      { credentials: 'include' }
    )
    if (response.status === 401) {
      router.replace('/login')
      return
    }
    const data = await response.json()
    options.value = data.folders.map(folder => ({ label: folder, value: folder }))
    filteredOptions.value = options.value
  } catch (err) {
    console.error('Ошибка при загрузке папок:', err)
    options.value = []
    filteredOptions.value = []
  } finally {
    loading.value = false
  }
}

function filterItems (val, update) {
  update(() => {
    if (!val) {
      filteredOptions.value = options.value
    } else {
      const needle = val.toLowerCase()
      filteredOptions.value = options.value.filter(option =>
        option.label.toLowerCase().includes(needle)
      )
    }
  })
}
</script>

<style scoped>
.full-width {
  width: 100%;
}
</style>
