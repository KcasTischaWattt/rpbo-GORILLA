<template>
  <q-card :class="['q-mx-md q-my-sm', { 'no-shadow': $q.dark.isActive }]">
    <q-card-section>
      <div class="row items-center justify-between">
        <div><b>ID:</b> {{ props.doc.id }}</div>
        <div class="row items-center">
          <span><b>Источник:</b> {{ props.doc.source }}</span>
          <span class="q-ml-sm"><b>Дата:</b> {{ props.doc.date }}</span>
          <FolderBookmark v-if="props.is_authed" :documentId="props.doc.id" />
        </div>
      </div>

      <div class="text-h6 q-mt-sm">{{ props.doc.title }}</div>
      <div>{{ props.doc.summary }}</div>

      <div v-if="props.doc.tags" class="q-mt-sm row wrap">
        <DocumentTags :tags="props.doc.tags" />
      </div>

      <q-btn
        class="q-mt-sm"
        label="Посмотреть"
        color="primary"
        no-caps
        text-color="secondary"
        :to="`/documents/${props.doc.id}`"
      />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { useQuasar } from 'quasar'
import DocumentTags from '../components/DocumentTags.vue'
import FolderBookmark from './FolderBookmark.vue'

const props = defineProps({
  doc: { type: Object, required: true },
  is_authed: { type: Boolean, required: true }
})

const $q = useQuasar()

</script>

<style scoped>
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
