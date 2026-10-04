<template>
  <q-header reveal class="no-pointer custom-navbar bg-dark-surface">
    <q-toolbar class="q-py-md">
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
          <button class="logo-button" type="button" @click="scrollToSection('hero')">
            <q-img :src="logo" alt="логотип" style="width: 120px; max-width: 100%" />
          </button>
        </q-toolbar-title>

        <div class="hidden-sm-and-down row items-center">
          <TopNavBarButton label="О продукте" section-id="about" @navigate="scrollToSection" />
          <TopNavBarButton label="Как пользоваться" section-id="guide" @navigate="scrollToSection" />
          <TopNavBarButton label="Наша миссия" section-id="founder" @navigate="scrollToSection" />
          <TopNavBarButton label="Команда" section-id="us" @navigate="scrollToSection" />
          <TopNavBarButton label="Контакты" section-id="contacts" @navigate="scrollToSection" />
        </div>
      </div>

      <q-space />
    </q-toolbar>

    <q-drawer v-model="drawerOpen" side="left" overlay behavior="mobile" class="mobile-drawer">
      <q-list padding>
        <q-item clickable @click="scrollToSectionFromDrawer('about')">
          <q-item-section>
            <q-item-label>О продукте</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable @click="scrollToSectionFromDrawer('guide')">
          <q-item-section>
            <q-item-label>Как пользоваться</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable @click="scrollToSectionFromDrawer('founder')">
          <q-item-section>
            <q-item-label>Наша миссия</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable @click="scrollToSectionFromDrawer('us')">
          <q-item-section>
            <q-item-label>Команда</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable @click="scrollToSectionFromDrawer('contacts')">
          <q-item-section>
            <q-item-label>Контакты</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>
  </q-header>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import TopNavBarButton from '../landing/LandingNavbarButton.vue'

import logo from 'src/assets/crosswords.png'

const drawerOpen = ref(false)

function toggleDrawer() {
  drawerOpen.value = !drawerOpen.value
}

const $q = useQuasar()
const isMobile = computed(() => $q.screen.lt.md)

function getHeaderHeight() {
  const header = document.querySelector('.q-header')
  return header?.offsetHeight || 0
}

function scrollToSection(sectionId) {
  const section = document.getElementById(sectionId)

  if (!section) return

  const headerHeight = getHeaderHeight()
  const sectionTop = section.getBoundingClientRect().top + window.scrollY

  window.scrollTo({
    top: sectionTop - headerHeight,
    behavior: 'smooth'
  })
}

async function scrollToSectionFromDrawer(sectionId) {
  drawerOpen.value = false

  await nextTick()

  setTimeout(() => {
    scrollToSection(sectionId)
  }, 250)
}
</script>

<style lang="scss">
.logo {
  height: 40px;
  max-height: 100%;
}

.logo-button {
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
}

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