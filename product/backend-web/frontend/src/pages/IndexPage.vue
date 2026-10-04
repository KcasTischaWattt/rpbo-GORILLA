<template>
  <q-page class="q-pa-md flex flex-center">
    <div class="text-center q-mx-auto page-container">
      <h1 class="text-h3 text-md-h4 text-weight-bold q-mb-lg">Все важные СМИ - в одном месте</h1>
      <p class="text-body1">
        Умный корпус текстов СМИ автоматически собирает и обрабатывает новости из крупных
        издательств, собирая их в одном месте.
      </p>
      <ul class="q-mb-lg">
        <li class="text-body1 q-mb-sm">Ежедневно собираем свежие новости с самых разных источников</li>
        <li class="text-body1 q-mb-sm">При помощи LLM анализируем новости, присваиваем теги, составляем краткие содержания и формируем тематические дайджесты</li>
        <li class="text-body1 q-mb-sm">Отправляем результат в корпус, в мобильное приложение или на почту</li>
      </ul>

      <DefaultButton label="Посмотреть новости" :to="{ name: 'documents' }" class="q-mb-xl" />

      <h1 class="text-h5 text-md-h4 text-weight-bold q-mb-md">Наши источники:</h1>
      <div
        class="horizontal-scroll q-mt-md row items-start q-gutter-sm"
        ref="horizontalScroll"
        @mouseenter="enableMouseWheelScroll"
        @mouseleave="disableMouseWheelScroll"
      >
        <SourceCard
          v-for="source in sources"
          :key="source.name"
          :link="source.link"
          :background="source.background"
          class="col-auto"
        />
      </div>
      <p class="q-mt-md">и многие другие...</p>
      <div class="text-warning q-mt-lg">
        <q-icon name="warning" color="orange" class="q-mr-xs"/>
        Материалы, сгенерированные ИИ могут содержать неточности. Для проверки информации обращайтесь к первоисточнику.
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import DefaultButton from '../components/DefaultButton.vue'
import SourceCard from '../components/SourceCard.vue'
import kommersantLogo from "/src/assets/kommersant.jpg"
import CBRULogo from "/src/assets/CBRU.jpg"
import interfaxLogo from "/src/assets/interfax.jpg"
import CBTJLogo from "/src/assets/CBTJ.jpg"
import CBKGLogo from "/src/assets/CBKG.jpg"
import CBUZLogo from "/src/assets/CBUZ.jpg"

const sources = ref([
  { name: 'Издательство 1', link: "https://www.kommersant.ru/", background: kommersantLogo },
  { name: 'Издательство 2', link: "https://www.cbr.ru/", background: CBRULogo },
  { name: 'Издательство 3', link: "https://www.interfax.ru/", background: interfaxLogo },
  { name: 'Издательство 5', link: "https://www.nbt.tj/ru/", background: CBTJLogo },
  { name: 'Издательство 6', link: "https://www.nbkr.kg/index.jsp?lang=RUS", background: CBKGLogo },
  { name: 'Издательство 4', link: "https://cbu.uz/ru/", background: CBUZLogo }
])

const horizontalScroll = ref(null)

const enableMouseWheelScroll = () => {
  if (horizontalScroll.value) {
    horizontalScroll.value.addEventListener('wheel', handleWheel)
  }
}

const disableMouseWheelScroll = () => {
  if (horizontalScroll.value) {
    horizontalScroll.value.removeEventListener('wheel', handleWheel)
  }
}

const handleWheel = (event) => {
  if (event.deltaY !== 0) {
    const target = horizontalScroll.value
    const scrollAmount = event.deltaY

    // Плавная прокрутка с использованием requestAnimationFrame
    const start = target.scrollLeft
    const end = start + scrollAmount
    const duration = 150 
    let startTime

    function animateScroll(timestamp) {
      if (!startTime) startTime = timestamp
      const timeElapsed = timestamp - startTime
      const progress = Math.min(timeElapsed / duration, 1)
      const newScrollLeft = start + (end - start) * progress
      target.scrollLeft = newScrollLeft

      if (timeElapsed < duration) {
        requestAnimationFrame(animateScroll)
      }
    }

    requestAnimationFrame(animateScroll)

    event.preventDefault()
  }
}
</script>

<style scoped lang="scss">
.horizontal-scroll {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  gap: 10px;
  padding: 10px 0;
}

.horizontal-scroll::-webkit-scrollbar {
  height: 6px;
}

.horizontal-scroll::-webkit-scrollbar-thumb {
  background-color: var(--q-primary);
  border-radius: 4px;
}

.horizontal-scroll::-webkit-scrollbar-track {
  background-color: $scrollbar-light;
}

body.body--dark .horizontal-scroll::-webkit-scrollbar-track {
  background-color: $scrollbar-dark;
}

.source-card {
  flex: 0 0 auto;
  width: 150px;
  height: 90px;
  margin: 5px;
}

@media (max-width: 768px) {
  .source-card {
    width: 120px;
    height: 70px;
  }
}

.page-container {
  max-width: 650px;
  padding: 0 16px;
  width: 100%;
  box-sizing: border-box;
}
</style>
