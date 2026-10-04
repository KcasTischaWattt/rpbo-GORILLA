<template>
  <q-banner v-if="showBanner" class="confirm-banner-bottom bg-primary text-black" dense elevated>
    <div class="row items-center wrap q-col-gutter-sm justify-center" style="width: 100%">
      <div
        class="col-12 col-md-auto q-gutter-x-sm row items-center justify-center justify-start-md text-center text-md-left"
      >
        <div class="self-start">
          <q-icon name="warning" size="sm" color="secondary" />
        </div>
        <div>
          Для того, чтобы получать дайджесты на почту, необходимо подтвердить её электронный адрес
        </div>
      </div>
      <div class="col-12 col-md-auto text-center q-mt-xs q-mt-none--md">
        <q-btn label="Подтвердить" color="secondary" no-caps class="q-ml-md" @click="goToVerification" />
        <q-btn label="В другой раз" color="secondary" outline no-caps class="q-ml-md" @click="hideBanner" />
      </div>
    </div>
  </q-banner>
</template>

<script setup>
import { backendURL } from 'src/data/lookups'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const showBanner = ref(false)
const router = useRouter()

onMounted(async () => {
  try {
    const response = await fetch(backendURL + "users/check_verification", {
      credentials: 'include',
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()

    showBanner.value = !!data.needs_confirmation
  } catch (err) {
    console.error('Не удалось получить статус подтверждения:', err)
    showBanner.value = false
  }
})

function goToVerification() {
  router.push({ name: 'confirm-email' })
}

function hideBanner() {
  showBanner.value = false
}
</script>

<style scoped>
.confirm-banner-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 90000;
}


</style>
