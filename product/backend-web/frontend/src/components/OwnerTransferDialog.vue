<template>
  <q-dialog v-model="show">
    <q-card>
      <q-card-section>
        <div class="text-h6 caption">Выберите нового владельца подписки</div>
        <div>
          Похоже, вы пытаетесь отписаться от дайджеста, который принадлежит вам.
          Выберите, кому передать права редактирования:
          <q-input
            v-model="filter"
            placeholder="Фильтр по почте"
            clearable
          />
        </div>
      </q-card-section>

      <q-list
        bordered
        separator
        style="max-height: 300px; overflow-y: auto;"
      >
        <q-item
          v-for="(email, index) in filteredEmails"
          :key="index"
          clickable
          :active-class="$q.dark.isActive ? 'text-primary' : 'text-accent'"
          :active="selectedEmail === email"
          @click="selectEmail(email)"
        >
          <q-item-section>{{ email }}</q-item-section>
          <q-item-section side>
            <q-icon name="check" v-if="selectedEmail === email" />
          </q-item-section>
        </q-item>
      </q-list>

      <div v-if="transferError" class="q-mt-sm text-negative text-caption">
        {{ transferError }}
      </div>

      <q-card-actions class="justify-between">
        <q-btn
          label="Отмена"
          color="secondary"
          no-caps
          @click="onCancel"
          :disable="transferLoading"
        />
        <q-btn
          label="Удтвердить владельца и отписаться"
          no-caps
          color="primary"
          text-color="secondary"
          :disable="!selectedEmail || transferLoading"
          @click="onConfirm"
        >
          <q-spinner-dots
            v-if="transferLoading"
            size="18px"
            class="q-ml-sm"
          />
        </q-btn>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  subscriberEmails: {
    type: Array,
    required: true
  },
  modelValue: {
    type: Boolean,
    default: false
  },
  transferLoading: {
    type: Boolean,
    default: false
  },
  transferError: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'cancel', 'confirm'])

const show = ref(props.modelValue)
const selectedEmail = ref(null)
const filter = ref('')

const filteredEmails = computed(() => {
  if (!filter.value) {
    return props.subscriberEmails
  }
  return props.subscriberEmails.filter(email =>
    email.toLowerCase().startsWith(filter.value.toLowerCase())
  )
})

watch(() => props.modelValue, val => {
  show.value = val
})
watch(show, val => {
  emit('update:modelValue', val)
})

function selectEmail(email) {
  selectedEmail.value = email
}

function onCancel() {
  selectedEmail.value = null
  emit('cancel')
  show.value = false
}

function onConfirm() {
  if (selectedEmail.value) {
    emit('confirm', selectedEmail.value)
  }
}
</script>
