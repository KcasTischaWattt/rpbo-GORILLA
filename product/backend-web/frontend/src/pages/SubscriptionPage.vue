<template>
  <ServerResponseSpinner v-if="isLoading" />
  <q-page v-else>
    <div class="full-width q-pa-md page-body">
      <BackButton to="/digests" />
      <div class="row items-center">
        <div
          :class="{
            'text-secondary': !$q.dark.isActive,
            'text-white': $q.dark.isActive,
          }"
          class="caption text-h4 q-mt-md"
        >
          {{ subscription.title }}
        </div>
        <subscription-button
          :digest="subscription"
          triggeredFrom="subscription_page"
          v-if="isAuthed"
        />
      </div>
      <div v-if="subscription.description">
        <p
          :class="[
            'text-italic',
            { 'text-yellow': $q.dark.isActive, 'text-accent': !$q.dark.isActive },
          ]"
        >
          "{{ subscription?.description }}"
        </p>
      </div>
      <q-card>
        <q-card-section>
          <div class="row items-center wrap q-mb-sm">
            <div class="row items-center q-mr-xs">
              <q-icon :name="fasCrown" class="q-mr-xs" />
              <span class="text-caption q-mr-md">
                {{ subscription.owner }}
              </span>
            </div>

            <div class="row items-center q-mr-xs">
              <q-icon name="event" size="xs" class="q-mr-xs" />
              <span class="text-caption q-mr-md">
                {{ subscription.creation_date }}
              </span>
            </div>

            <div class="row items-center q-mr-xs">
              <q-icon :name="subscription.public ? 'public' : 'lock'" size="xs" class="q-mr-xs" />
              <span class="text-caption q-mr-md">
                {{ subscription.public ? 'Публичный' : 'Приватный' }}
              </span>
            </div>
            <div class="row items-center" v-if="subscription.average_rating != null">
              <q-icon name="star" color="primary" class="q-mr-xs" />
              <span class="text-caption q-mr-md">
                {{ displayRating }}
              </span>
            </div>
            <div class="row items-center" v-else>
              <span class="text-caption"> Нет оценок </span>
            </div>
          </div>
          <document-tags :tags="subscription.tags" />
          <div class="text-caption">
          <b>Источники: </b>
          <span v-if="subscription.sources && subscription.sources.length">
            {{ subscription.sources.join(', ') }}
          </span>
        </div>
        </q-card-section>
      </q-card>
      <div
        :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
        class="caption text-h4 text-center q-mt-xl q-mb-md"
      >
        Дайджесты
      </div>
      <div v-if="digests.length > 0">
        <digest-card
          v-for="digest in digests"
          :key="digest.id"
          :is_authed="isAuthed"
          :digest="digest"
        />
      </div>
      <div
      v-else
        style="display: flex; flex-direction: column; align-items: center"
        class="flex flex-center flex-col q-pa-md"
      >
        <q-icon name="search" size="100px" color="grey-5" />
        <div class="q-mt-md text-h6 text-center">Похоже, эта подписка еще не сгенерировала ни одного дайджеста.</div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useQuasar } from 'quasar'
import digestCard from 'components/DigestCard.vue'
import ServerResponseSpinner from 'components/ServerResponseSpinner.vue'
import { fasCrown } from '@quasar/extras/fontawesome-v6'
import DocumentTags from "src/components/DocumentTags.vue";
import BackButton from 'src/components/BackButton.vue'
import SubscriptionButton from 'src/components/SubscriptionButton.vue'
import { backendURL } from 'src/data/lookups'
import { useRoute } from 'vue-router'

const $q = useQuasar()
const route = useRoute()
const isLoading = ref(true)
const subscription = ref({
  title: '',
  description: '',
  average_rating: 0,
  is_authed: false,
  public: false,
  is_owner: false,
  owner: '',
  sources: [],
  tags: [],
  subscribe_options: {},
  digests: [],
})
const digests = ref([])
const isAuthed = ref(false)

const displayRating = computed(() => {
  const r = subscription.value.average_rating
  return r != null && r !== -1 ? r : 'Нет оценок'
})

async function fetchSubscription() {
  try {
    const id = route.params.id
    const response = await fetch(
      //'https://6d9e7a119e7c45aa8ba0100093e326f9.api.mockbin.io/'
      backendURL + `subscriptions/${id}/digests`,
      {credentials: 'include'}
    )
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }
    const { subscription: sub } = await response.json()
    // Заполняем реактивные данные
    subscription.value = sub
    digests.value = sub.digests || []
    isAuthed.value = !!sub.is_authed
  } catch (err) {
    console.error('fetchSubscription error:', err)
    $q.notify({
      type: 'negative',
      message: 'Не удалось загрузить подписку',
      position: 'top'
    })
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchSubscription)
</script>
