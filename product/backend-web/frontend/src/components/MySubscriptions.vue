<template>
  <q-card :class="['my-subscriptions-container q-pa-md q-mb-md no-shadow', $q.dark.isActive ? 'bg-dark' : 'bg-grey-1']">
    <q-card-section>
      <div
        :class="{ 'text-secondary': !$q.dark.isActive, 'text-white': $q.dark.isActive }"
        class="text-h6 caption q-mb-md"
      >
        Мои подписки
      </div>

      <template v-if="isLoading">
        <div class="q-pa-md">
          <q-spinner size="50px" color="primary" />
        </div>
      </template>

      <template v-else-if="subscriptions.length > 0">
        <div>
          <subscription-card
            v-for="digest in displayedSubscriptions"
            :key="digest.id"
            :digest="digest"
            @subscription-removed="onSubscriptionRemoved"
          />
        </div>

        <div class="row justify-between q-mt-md">
          <div class="col-auto">
            <q-btn
              v-if="subscriptions.length > 2"
              :label="showAll ? 'Скрыть' : 'Показать ещё'"
              color="primary"
              no-caps
              text-color="secondary"
              @click="toggleShowAll"
            />
          </div>
          <div class="col-auto">
            <q-btn
              label="Заказать дайджест"
              color="primary"
              no-caps
              text-color="secondary"
              @click="createDigest"
            />
          </div>
        </div>
      </template>

      <template v-else>
        <div
          style="display: flex; flex-direction: column; align-items: center"
          class="flex flex-center flex-col q-pa-md"
        >
          <q-icon name="search" size="100px" color="grey-5" />
          <div class="q-mt-md text-h6 text-center">
            Похоже, подписок нет. Давайте это исправим!
          </div>
          <q-btn
            label="Заказать дайджест"
            color="primary"
            no-caps
            text-color="secondary"
            class="q-mt-md"
            @click="createDigest"
          />
        </div>
      </template>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { backendURL } from 'src/data/lookups'
import SubscriptionCard from './SubscriptionCard.vue'

const $q = useQuasar()
const router = useRouter()
const subscriptions = ref([])
const showAll = ref(false)
const isLoading = ref(true)
const displayedSubscriptions = computed(() =>
  showAll.value ? subscriptions.value : subscriptions.value.slice(0, 2)
)

async function fetchSubscriptions() {
  isLoading.value = true
  try {
    const response = await fetch(
      `${backendURL}subscriptions`,
      { method: 'GET', credentials: 'include' }
    )
    if (!response.ok) throw new Error('Ошибка при получении подписок')
    const data = await response.json()
    subscriptions.value = data.digest_subscriptions || []
  } catch (err) {
    console.error(err)
  } finally {
    isLoading.value = false
  }
}

function onSubscriptionRemoved(id) {
  subscriptions.value = subscriptions.value.filter(d => d.id !== id)
}

function toggleShowAll() {
  showAll.value = !showAll.value
}

function createDigest() {
  router.push('/subscriptions/create')
}

onMounted(fetchSubscriptions)
</script>

<style scoped>
</style>
