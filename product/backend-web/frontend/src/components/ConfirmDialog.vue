<template>
  <q-dialog v-model="internalModelValue" persistent>
    <q-card class="q-pa-md" style="min-width: 300px; max-width: 400px">
      <div class="text-h6 q-mb-md">{{ title }}</div>

      <div class="q-mb-sm">
        {{ message }}
      </div>

      <q-checkbox v-model="checked" :label="checkboxLabel" color="primary" />

      <div class="row justify-end q-gutter-sm q-mt-md">
        <q-btn label="Отмена" color="primary" text-color="secondary" no-caps @click="onCancel" />

        <q-btn
          :label="confirmLabel"
          color="secondary"
          no-caps
          :disable="!checked"
          @click="onConfirm"
        />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: 'Внимание!',
  },
  message: {
    type: String,
    default: '',
  },
  checkboxLabel: {
    type: String,
    default: 'Я понимаю, что делаю',
  },
  confirmLabel: {
    type: String,
    default: 'ОК',
  },
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const internalModelValue = ref(props.modelValue)
const checked = ref(false)


watch(
  () => props.modelValue,
  (newVal) => {
    internalModelValue.value = newVal
    if (!newVal) {
      checked.value = false
    }
  },
)

watch(
  () => internalModelValue.value,
  (newVal) => {
    emit('update:modelValue', newVal)
    if (!newVal) {
      checked.value = false
    }
  },
)

function onCancel() {
  internalModelValue.value = false
  emit('cancel')
}

function onConfirm() {
  emit('confirm')
  internalModelValue.value = false
}
</script>
