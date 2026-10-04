<template>
  <div>
    <q-btn label="Открыть меню" @click="toggleDrawer" />

    <q-drawer v-model="drawerOpen" side="left" overlay bordered>
      <q-list>
        <q-item clickable v-close-popup @click="onPrivacy">
          <q-item-section>
            <div class="row items-center justify-center">
              <q-icon name="lock" class="q-mr-sm" />
              <span>Приватность</span>
            </div>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="onExport" v-ripple>
          <q-item-section>
            <div class="row items-center justify-center">
              <q-icon name="download" class="q-mr-sm" />
              <span>Экспорт данных</span>
            </div>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>
  </div>
</template>

<script setup>
import useDrawer from 'src/composables/useDrawer'
import { useRouter } from 'vue-router'

const router = useRouter()
const { drawerOpen, toggleDrawer } = useDrawer()

function onPrivacy() {
  router.push({ name: 'privacy' })
}

function onExport() {
  router.push({ name: 'data' })
}
</script>

