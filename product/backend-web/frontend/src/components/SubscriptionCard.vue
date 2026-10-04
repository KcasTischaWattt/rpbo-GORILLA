<template>
  <q-card
    flat
    bordered
    :class="['q-my-sm no-shadow', $q.dark.isActive ? 'bg-dark' : 'white']"
  >
    <q-card-section>
      <div class="row items-center wrap q-mb-sm">
        <div class="row items-center q-mr-sm">
          <q-icon :name="fasCrown" class="q-mr-xs" />
          <span class="text-caption q-mr-md">{{ digest.owner }}</span>
        </div>

        <div class="row items-center q-mr-sm">
          <q-icon name="event" size="xs" class="q-mr-xs" />
          <span class="text-caption q-mr-md">{{ digest.creation_date }}</span>
        </div>

        <div class="row items-center q-mr-sm">
          <q-icon :name="digest.public ? 'public' : 'lock'" size="xs" class="q-mr-xs" />
          <span class="text-caption">
            {{ digest.public ? 'Публичный' : 'Приватный' }}
          </span>
        </div>
      </div>

      <div class="row items-center q-mt-sm">
        <div class="text-h4 ellipsis">{{ truncatedTitle }}</div>
        <subscription-button
          :digest="digest"
          triggeredFrom="subscriptions"
          @remove-subscription="handleRemove"
        />
      </div>

      <div class="text-body2 q-my-xs">
        {{ digest.description }}
      </div>
      <document-tags :tags="digest.tags" />
      <div class="text-caption q-mt-sm">
        <b>Источники: </b>
        <span v-if="digest.sources && digest.sources.length">
          {{ digest.sources.join(', ') }}
        </span>
      </div>
    </q-card-section>

    <q-card-section class="row justify-start q-pt-none">
      <q-btn
        label="Посмотреть"
        no-caps
        text-color="secondary"
        color="primary"
        @click="onViewClick"
        class="q-mr-sm"
      />
      <q-btn
        v-if="digest.is_owner"
        icon-right="edit"
        label="Редактировать"
        no-caps
        color="secondary"
        @click="onEditClick"
      />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { fasCrown } from '@quasar/extras/fontawesome-v6'
import DocumentTags from './DocumentTags.vue'
import SubscriptionButton from './SubscriptionButton.vue'

const props = defineProps({
  digest: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['subscription-removed'])
const router = useRouter()

const truncatedTitle = computed(() => {
  const t = props.digest.title
  return t.length > 22 ? t.slice(0, 22) + '...' : t
})

function handleRemove(id) {
  emit('subscription-removed', id)
}

function onEditClick() {
  router.push({ name: 'subscription-edit', params: { id: props.digest.id } })
}

function onViewClick() {
  router.push({ name: 'subscription-view', params: { id: props.digest.id } })
}
</script>

