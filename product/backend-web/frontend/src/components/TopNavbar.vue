<template>
  <q-header reveal class="no-pointer custom-navbar">
    <q-toolbar class="q-py-md">
      <!-- гамбургер меню показывается только на мобильных экранах -->
      <q-btn
        v-if="isMobile"
        flat
        dense
        round
        @click="toggleDrawer"
        icon="menu"
        aria-label="Toggle navigation"
        class="q-ml-sm"
      />

      <div class="row items-center">
        <q-toolbar-title>
          <router-link to="/" class="flex items-center no-underline text-black">
            <q-img :src="logo" alt="логотип" style="width: 120px; max-width: 100%" />
          </router-link>
        </q-toolbar-title>
        <div class="hidden-sm-and-down row items-center">
          <TopNavBarButton label="Домой" :to="{ name: 'home' }" />
          <TopNavBarButton label="Документы" :to="{ name: 'documents' }" />
          <TopNavBarButton label="Дайджесты" :to="{ name: 'digests' }" />
          <q-btn
            flat
            dense
            no-caps
            class="q-mr-sm flat-button-text"
            label="Статистика"
            href="https://grafana.nt.fyi/login"
            target="_blank"
            rel="noopener"
            aria-label="Open statistics site"
          />
        </div>
      </div>

      <q-space />
      <q-btn flat icon="brightness_4" @click="toggleDarkMode" aria-label="Toggle dark mode" />

      <template v-if="isAuthenticated">
        <q-btn flat round icon="account_circle" :to="{ name: 'privacy' }" aria-label="Profile" />
      </template>
      <template v-else>
        <q-btn flat label="Войти" :to="{ name: 'login' }" aria-label="Login" />
      </template>
    </q-toolbar>

    <q-drawer v-model="drawerOpen" side="left" overlay behavior="mobile" class="mobile-drawer">
      <q-list padding>
        <q-item clickable :to="{ name: 'home' }" @click="toggleDrawer">
          <q-item-section>
            <q-item-label>Домой</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable :to="{ name: 'documents' }" @click="toggleDrawer">
          <q-item-section>
            <q-item-label>Документы</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable :to="{ name: 'digests' }" @click="toggleDrawer">
          <q-item-section>
            <q-item-label>Дайджесты</q-item-label>
          </q-item-section>
        </q-item>

        <q-item
          clickable
          tag="a"
          href="https://grafana.nt.fyi/login"
          target="_blank"
          rel="noopener"
          @click="toggleDrawer"
        >
          <q-item-section>
            <q-item-label>Статистика</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>
  </q-header>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useQuasar } from 'quasar'
import TopNavBarButton from '../components/TopNavbarButton.vue'
import { backendURL } from 'src/data/lookups'
import { emitter } from 'src/boot/emitter'
import logo from 'src/assets/crosswords.png'

const drawerOpen = ref(false)
function toggleDrawer() {
  drawerOpen.value = !drawerOpen.value
}

const $q = useQuasar()
const isMobile = computed(() => $q.screen.lt.md)
const isDark = ref($q.dark.isActive)
const isAuthenticated = ref(false)

function toggleDarkMode() {
  $q.dark.set(!isDark.value)
  isDark.value = !isDark.value
}

onMounted(() => {
  checkAuth()
  emitter.on('auth-changed', checkAuth)
})
onBeforeUnmount(() => {
  emitter.off('auth-changed', checkAuth)
})

async function checkAuth() {
  try {
    const response = await fetch(backendURL + 'users/check_auth', { credentials: 'include' })
    if (response.ok) {
      isAuthenticated.value = true
      console.log('Пользователь авторизован')
    } else {
      isAuthenticated.value = false
      console.log('Пользователь не авторизован')
    }
  } catch {
    isAuthenticated.value = false
    console.log('Пользователь не авторизован')
  }
}
</script>

<style lang="scss">
.logo {
  height: 40px;
  max-height: 100%;
}

/* Скрываем блок кнопок на маленьких экранах */
.hidden-sm-and-down {
  display: none;
}

@media (min-width: 992px) {
  .hidden-sm-and-down {
    display: flex;
  }
}

.no-pointer {
  cursor: default;
}

.mobile-drawer {
  color: $secondary;
}

.custom-navbar {
  color: $secondary;
}
</style>
