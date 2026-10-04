<template>
  <q-page class="q-pa-none fit">
    <section id="hero" class="hero-section">
      <video
        class="hero-video"
        :src="videoSrc"
        :poster="posterSrc"
        autoplay
        muted
        playsinline
        loop
      ></video>

      <div class="overlay" aria-hidden="true" />

      <div class="hero-content">
        <h1 class="hero-title">Эра единого инфополя уже здесь</h1>
        <q-btn
          class="hero-cta bg-dark-surface"
          size="lg"
          label="Начать работу →"
          unelevated
          no-caps
          to="/"
        />
      </div>
    </section>

    <section id="about" class="info-section q-px-md q-pt-xl" ref="infoRef">
      <div class="container">
        <h2 class="info-title">Добро пожаловать в Crosswords</h2>
        <p class="info-subtitle">
          Инновационный аггрегатор новостей со встроенным ИИ
          <q-icon name="auto_awesome" size="md" style="color: #ffb310;" />
        </p>
      </div>
    </section>
    
    <SplitSection
  :reverseOnDesktop="false"
  title="Все, что происходит в мире — в едином потоке"
  subtitle="Следите. Погружайтесь. Опережайте."
  body="Агрегатор собирает разрозненные сигналы мира в чистый, настраиваемый поток. Без баннеров, кликбейта и принудительной повестки. вы видите не то, что вам показывают, а то, что происходит на самом деле. Новая норма потребления контента уже действует."
  cta-label="Попробовать →"
  cta-to="/"
  cta-class="bg-dark-surface text-white"
  img-src="/screenshot.jpg"
  img-alt="Скриншот SmartMax"
  :img-ratio="16/10"
/>

    <SplitSection
  :reverseOnDesktop="true"
  title="От первой новости до полной картины — одно касание"
  subtitle="Закажите дайджест, выберите интересующие темы, остальное за нами."
  body="Никаких вкладок, загрузок, всплывающих баннеров и необходимости помнить, где вы это читали. Всё собрано, взвешено и подано в той последовательности, которая экономит ваше внимание. Агрегатор сокращает путь от незнания к пониманию до минимума. остальное время — ваше."
  img-src="/screenshot2.jpg"
  img-alt="Скриншот SmartMax"
  :img-ratio="16/10"
/>
<section id="guide" class="how-section q-px-md q-py-xl">
  <div class="container">
    <div class="how-header">
      <p class="section-eyebrow">Простой старт</p>
      <h2 class="how-title">Как это работает</h2>
      <p class="how-subtitle">
        От регистрации до ежедневных персональных дайджестов — всего несколько шагов.
      </p>
    </div>

    <div class="how-list">
      <div class="how-card">
        <div class="how-number">1</div>
        <div class="how-content">
          <h3>Создайте аккаунт</h3>
          <p>Регистрация занимает минимум времени. И да — это бесплатно!</p>
        </div>
      </div>

      <div class="how-card">
        <div class="how-number">2</div>
        <div class="how-content">
          <h3>Выберите формат дайджеста</h3>
          <p>
            Закажите персонализированный дайджест или подпишитесь на один из публичных.
          </p>
        </div>
      </div>

      <div class="how-card">
        <div class="how-number">3</div>
        <div class="how-content">
          <h3>Настройте темы и источники</h3>
          <p>
            При создании дайджеста укажите интересующие вас теги и источники.
          </p>
        </div>
      </div>

      <div class="how-card">
        <div class="how-number">4</div>
        <div class="how-content">
          <h3>Добавьте получателей</h3>
          <p>
            Подключите друзей и коллег, чтобы они тоже получали ваш дайджест. У вас же есть друзья, правда?
          </p>
        </div>
      </div>

      <div class="how-card">
        <div class="how-number">5</div>
        <div class="how-content">
          <h3>Получайте готовую картину дня</h3>
          <p>
            Теперь наш ИИ будет ежедневно генерировать дайджесты по указанной вами теме.
          </p>
        </div>
      </div>
    </div>

    <div class="how-action">
      <q-btn
        class="how-cta bg-dark-surface text-white"
        label="Создать аккаунт →"
        unelevated
        no-caps
        :to="{ name: 'login' }"
      />
    </div>
  </div>
</section>

<section id="founder" class="founder-section q-px-md q-py-xl">
  <div class="container">
    <div class="founder-card">
      <div class="founder-photo-wrap">
        <q-img
          class="founder-photo"
          src="/tataurov.jpg"
          alt="Основатель Crosswords"
          ratio="1"
          fit="cover"
        />
      </div>

      <div class="founder-content">
        <p class="section-eyebrow">Слово основателя</p>

        <h2 class="founder-title">
          Мы создаём не просто агрегатор новостей
        </h2>

        <blockquote class="founder-quote">
          «Все началось с того, что в магистратуре, на курсе "ИИ для бизнеса", нам дали задание создать какой-нибудь прикладной продукт при помощи ИИ. Тут мне в голову и пришла идея о создании умного агрегатора новостей. Мы с ребятами хотим, чтобы Crosswords стал для людей способом вернуть контроль над информацией. Не бесконечно листать ленты, не тонуть в шуме, а каждый день быстро понимать, что действительно происходит в мире и почему это важно».
        </blockquote>

        <div class="founder-person">
          <p class="founder-name">Матвей Татауров</p>
          <p class="founder-role">Сооснователь Crosswords</p>
        </div>
      </div>
    </div>

    <div id="us" class="founders-team">
      <div class="founders-team-header">
        <p class="section-eyebrow">Команда</p>
        <h3 class="founders-team-title">Основатели Crosswords</h3>
      </div>

      <div class="founders-grid">
        <div class="founder-mini-card">
          <q-img
            class="founder-mini-photo"
            src="/Matvey.jpg"
            alt="Основатель Crosswords"
            ratio="1"
            fit="cover"
          />

          <div class="founder-mini-info">
            <h4>Матвей Татауров</h4>
            <p>CEO / Сооснователь</p>
          </div>
        </div>

        <div class="founder-mini-card">
          <q-img
            class="founder-mini-photo"
            src="/Max.jpg"
            alt="Основатель Crosswords"
            ratio="1"
            fit="cover"
          />

          <div class="founder-mini-info">
            <h4>Максим Пересторонин</h4>
            <p>CTO / Сооснователь</p>
          </div>
        </div>

        <div class="founder-mini-card">
          <q-img
            class="founder-mini-photo"
            src="/Andrey.jpg"
            alt="Основатель Crosswords"
            ratio="1"
            fit="cover"
          />

          <div class="founder-mini-info">
            <h4>Андрей Цейтин</h4>
            <p>CPO / Сооснователь</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>
<section id="contacts" class="contacts-section q-px-md q-py-xl">
  <div class="container">
    <div class="contacts-card">
      <div class="contacts-content">
        <p class="section-eyebrow">Связаться с нами</p>

        <h2 class="contacts-title">
          Есть вопросы или хотите обсудить сотрудничество?
        </h2>

        <p class="contacts-subtitle">
          Напишите нам — мы ответим, расскажем подробнее о Crosswords и поможем начать работу.
        </p>
      </div>

      <div class="contacts-links">
        <a class="contact-link" href="mailto:mdperestoronin@edu.hse.ru">
          <q-icon name="mail" size="28px" />
          <div>
            <span>Почта</span>
            <strong>mdperestoronin@edu.hse.ru</strong>
          </div>
        </a>

        <a
          class="contact-link"
          href="https://t.me/Maxim_Perestoronin"
          target="_blank"
          rel="noopener noreferrer"
        >
          <q-icon name="telegram" size="28px" />
          <div>
            <span>Telegram</span>
            <strong>@Maxim_Perestoronin</strong>
          </div>
        </a>
      </div>
    </div>
  </div>
</section>
  </q-page>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import SplitSection from "src/components/landing/SplitSection.vue"
import { sendAnalyticsEvent } from 'src/utils/analytics.js'

const videoSrc = ref('fullhd.mp4')
const posterSrc = ref('business.jpg')

const infoRef = ref(null)

const HEADER_SELECTOR = '.q-header'
let rafId
let ro
let scrollTracked = false

function handleLandingScroll() {
  if (scrollTracked) return

  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const viewportHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  const scrollBottom = scrollTop + viewportHeight
  const threshold = 120

  if (scrollBottom >= documentHeight - threshold) {
    scrollTracked = true

    sendAnalyticsEvent(
      'landing_scrolled_to_end',
      {
        scroll_depth: 'end'
      },
      {
        once: true
      }
    )

    window.removeEventListener('scroll', handleLandingScroll)
  }
}

function setHeaderVar () {
  const el = document.querySelector(HEADER_SELECTOR)
  const rect = el?.getBoundingClientRect()
  const dpr = window.devicePixelRatio || 1
  const raw = rect ? rect.height : 0
  const px = Math.round(raw * dpr) / dpr
  document.documentElement.style.setProperty('--header-h', px + 'px')
}

const scheduleMeasure = () => {
  cancelAnimationFrame(rafId)
  rafId = requestAnimationFrame(setHeaderVar)
}

onMounted(async () => {
  await nextTick()
  const header = document.querySelector(HEADER_SELECTOR)

sendAnalyticsEvent(
    'landing_view',
    {
      source: document.referrer || null
    },
    {
      once: true
    }
  )

  window.addEventListener('scroll', handleLandingScroll, { passive: true })
  handleLandingScroll()
  scheduleMeasure()

  if (document?.fonts?.ready) {
    document.fonts.ready.then(scheduleMeasure).catch(() => {})
  }


  if ('ResizeObserver' in window && header) {
    ro = new ResizeObserver(scheduleMeasure)
    ro.observe(header)
  }

  window.addEventListener('load', scheduleMeasure, { passive: true })
  window.addEventListener('resize', scheduleMeasure, { passive: true })
  window.addEventListener('orientationchange', scheduleMeasure, { passive: true })

})

onBeforeUnmount(() => {
  cancelAnimationFrame(rafId)
  if (ro) ro.disconnect()
  window.removeEventListener('scroll', handleLandingScroll)
  window.removeEventListener('load', scheduleMeasure)
  window.removeEventListener('resize', scheduleMeasure)
  window.removeEventListener('orientationchange', scheduleMeasure)
})
</script>

<style scoped>
.contacts-section {
  background: #ffffff;
}

.contacts-card {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 3rem;
  align-items: center;
  padding: clamp(1.5rem, 3vw, 4rem);
  border-radius: 36px;
  background:
    radial-gradient(circle at top left, rgba(120, 12, 221, 0.12), transparent 34%),
    radial-gradient(circle at bottom right, rgba(255, 179, 16, 0.16), transparent 34%),
    linear-gradient(135deg, #ffffff, #f7f7fb);
  border: 1px solid rgba(17, 17, 17, 0.08);
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.08);
}

.contacts-title {
  margin: 0;
  max-width: 820px;
  font-size: clamp(2.25rem, 2vw + 1.5rem, 4rem);
  font-weight: 900;
  line-height: 1.1;
  color: #111111;
  text-wrap: balance;
}

.contacts-subtitle {
  margin: 1rem 0 0;
  max-width: 680px;
  font-size: clamp(1.05rem, 0.45vw + 1rem, 1.3rem);
  font-weight: 600;
  line-height: 1.55;
  color: #555555;
}

.contacts-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contact-link {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  border-radius: 24px;
  text-decoration: none;
  color: #111111;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(17, 17, 17, 0.08);
  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.07);
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.contact-link:hover {
  transform: translateY(-4px);
  border-color: rgba(120, 12, 221, 0.25);
  box-shadow: 0 22px 70px rgba(0, 0, 0, 0.11);
}

.contact-link .q-icon {
  width: 56px;
  height: 56px;
  flex: 0 0 56px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: linear-gradient(135deg, #780cdd, #de12a8, #ffb310);
  box-shadow: 0 12px 28px rgba(120, 12, 221, 0.25);
}

.contact-link span {
  display: block;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
  font-weight: 700;
  color: #777777;
}

.contact-link strong {
  display: block;
  font-size: clamp(1rem, 0.5vw + 0.9rem, 1.25rem);
  font-weight: 850;
  color: #111111;
  word-break: break-word;
}
@media (max-width: 900px) {
  .contacts-card {
    grid-template-columns: 1fr;
    gap: 2rem;
    text-align: center;
  }

  .contacts-subtitle {
    margin-left: auto;
    margin-right: auto;
  }

  .contacts-links {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
  }

  .contact-link {
    text-align: left;
  }
}

@media (max-width: 600px) {
  .contacts-section {
    padding-top: 3rem;
    padding-bottom: 3rem;
  }

  .contacts-card {
    padding: 1.25rem;
    border-radius: 26px;
  }

  .contacts-title {
    font-size: clamp(2rem, 9vw, 3rem);
  }

  .contacts-subtitle {
    font-size: 1rem;
  }

  .contact-link {
    padding: 1rem;
    border-radius: 20px;
  }

  .contact-link .q-icon {
    width: 48px;
    height: 48px;
    flex-basis: 48px;
    border-radius: 16px;
  }

  .contact-link strong {
    font-size: 1rem;
  }
}
.founder-section {
  background: #0b0b12;
  color: #ffffff;
}
.founders-team {
  margin-top: 3.5rem;
}

.founders-team-header {
  margin-bottom: 2rem;
  text-align: center;
}

.founders-team-title {
  margin: 0;
  font-size: clamp(2rem, 1.5vw + 1.5rem, 3.25rem);
  font-weight: 900;
  line-height: 1.1;
  color: #ffffff;
  text-wrap: balance;
}

.founders-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1.25rem;
}

.founder-mini-card {
  position: relative;
  padding: 1rem;
  border-radius: 30px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.11), rgba(255, 255, 255, 0.04));
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.28);
  overflow: hidden;
  transition: transform 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
}

.founder-mini-card::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top left, rgba(120, 12, 221, 0.18), transparent 38%),
    radial-gradient(circle at bottom right, rgba(255, 179, 16, 0.16), transparent 36%);
  opacity: 0;
  transition: opacity 0.25s ease;
  pointer-events: none;
}

.founder-mini-card:hover {
  transform: translateY(-6px);
  border-color: rgba(255, 179, 16, 0.28);
  box-shadow: 0 30px 90px rgba(0, 0, 0, 0.38);
}

.founder-mini-card:hover::before {
  opacity: 1;
}

.founder-mini-photo {
  position: relative;
  z-index: 1;
  width: 100%;
  border-radius: 24px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.08);
}

.founder-mini-info {
  position: relative;
  z-index: 1;
  padding: 1.25rem 0.25rem 0.25rem;
  text-align: center;
}

.founder-mini-info h4 {
  margin: 0;
  font-size: 1.35rem;
  font-weight: 850;
  line-height: 1.25;
  color: #ffffff;
}

.founder-mini-info p {
  margin: 0.45rem 0 0;
  font-size: 1rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.62);
}
@media (max-width: 900px) {
  .founders-grid {
    grid-template-columns: 1fr;
    max-width: 520px;
    margin: 0 auto;
  }

  .founder-mini-card {
    border-radius: 26px;
  }

  .founder-mini-photo {
    border-radius: 20px;
  }
}

@media (min-width: 601px) and (max-width: 900px) {
  .founder-mini-card {
    display: grid;
    grid-template-columns: 160px 1fr;
    gap: 1.25rem;
    align-items: center;
  }

  .founder-mini-info {
    padding: 0;
    text-align: left;
  }
}

@media (max-width: 600px) {
  .founders-team {
    margin-top: 2.5rem;
  }

  .founders-team-header {
    margin-bottom: 1.5rem;
  }

  .founders-team-title {
    font-size: clamp(1.8rem, 9vw, 2.6rem);
  }

  .founder-mini-card {
    padding: 0.85rem;
    border-radius: 22px;
  }

  .founder-mini-photo {
    border-radius: 18px;
  }

  .founder-mini-info {
    padding-top: 1rem;
  }

  .founder-mini-info h4 {
    font-size: 1.15rem;
  }

  .founder-mini-info p {
    font-size: 0.95rem;
  }
}

.founder-card {
  display: grid;
  grid-template-columns: minmax(280px, 420px) 1fr;
  gap: 4rem;
  align-items: center;
  padding: clamp(1.5rem, 3vw, 4rem);
  border-radius: 36px;
  background:
    radial-gradient(circle at top right, rgba(222, 18, 168, 0.2), transparent 34%),
    radial-gradient(circle at bottom left, rgba(120, 12, 221, 0.22), transparent 36%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.03));
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 30px 90px rgba(0, 0, 0, 0.35);
  overflow: hidden;
}

.founder-photo-wrap {
  position: relative;
}

.founder-photo-wrap::before {
  content: "";
  position: absolute;
  inset: -14px;
  border-radius: 36px;
  background: linear-gradient(135deg, #780cdd, #de12a8, #ffb310);
  opacity: 0.9;
  filter: blur(0);
}

.founder-photo {
  position: relative;
  z-index: 1;
  width: 100%;
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.45);
}

.founder-content {
  position: relative;
  z-index: 1;
}

.founder-title {
  margin: 0;
  max-width: 760px;
  font-size: clamp(2.25rem, 2vw + 1.5rem, 4rem);
  font-weight: 900;
  line-height: 1.1;
  text-wrap: balance;
}

.founder-quote {
  position: relative;
  margin: 2rem 0 0;
  padding: 0 0 0 1.5rem;
  max-width: 820px;
  border-left: 4px solid #ffb310;
  font-size: clamp(1.15rem, 0.7vw + 1rem, 1.55rem);
  font-weight: 600;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.88);
}

.founder-quote::before {
  content: "“";
  position: absolute;
  top: -2.2rem;
  left: 0.75rem;
  font-size: 7rem;
  font-weight: 900;
  line-height: 1;
  color: rgba(255, 255, 255, 0.08);
  pointer-events: none;
}

.founder-person {
  margin-top: 2rem;
}

.founder-name {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 850;
  color: #ffffff;
}

.founder-role {
  margin: 0.3rem 0 0;
  font-size: 1rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 900px) {
  .founder-card {
    grid-template-columns: 1fr;
    gap: 2.5rem;
  }

  .founder-photo-wrap {
    max-width: 420px;
    margin: 0 auto;
  }

  .founder-content {
    text-align: center;
  }

  .founder-quote {
    padding-left: 0;
    border-left: none;
  }

  .founder-quote::before {
    left: 50%;
    transform: translateX(-50%);
  }
}

@media (max-width: 600px) {
  .founder-section {
    padding-top: 3rem;
    padding-bottom: 3rem;
  }

  .founder-card {
    padding: 1.25rem;
    border-radius: 26px;
    gap: 2rem;
  }

  .founder-photo-wrap::before {
    inset: -8px;
    border-radius: 26px;
  }

  .founder-photo {
    border-radius: 22px;
  }

  .founder-title {
    font-size: clamp(2rem, 9vw, 3rem);
  }

  .founder-quote {
    margin-top: 1.5rem;
    font-size: 1rem;
    line-height: 1.6;
  }

  .founder-quote::before {
    top: -1.7rem;
    font-size: 5rem;
  }

  .founder-name {
    font-size: 1.1rem;
  }

  .founder-role {
    font-size: 0.95rem;
  }
}

.how-section {
  background:
    radial-gradient(circle at top left, rgba(232, 209, 4, 0.12), transparent 32%),
    radial-gradient(circle at bottom right, rgba(255, 179, 16, 0.14), transparent 30%),
    #ffffff;
}

.how-header {
  max-width: 860px;
  margin: 0 auto 3rem;
  text-align: center;
}

.section-eyebrow {
  margin: 0 0 0.75rem;
  font-size: 0.95rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #780cdd;
}

.how-title {
  margin: 0;
  font-weight: 900;
  line-height: 1.1;
  font-size: clamp(2.5rem, 2vw + 2rem, 4.5rem);
  color: #111111;
  text-wrap: balance;
}

.how-subtitle {
  margin: 1rem auto 0;
  max-width: 720px;
  font-size: clamp(1.1rem, 0.5vw + 1rem, 1.35rem);
  font-weight: 600;
  line-height: 1.5;
  color: #555555;
}

.how-list {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 1rem;
}

.how-card {
  position: relative;
  min-height: 260px;
  padding: 1.5rem;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(17, 17, 17, 0.08);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(12px);
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.how-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 26px 80px rgba(0, 0, 0, 0.12);
  border-color: rgba(120, 12, 221, 0.25);
}

.how-number {
  width: 52px;
  height: 52px;
  margin-bottom: 1.5rem;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 1.35rem;
  font-weight: 900;
  background: linear-gradient(135deg, #FFF3A0, #F9D423, #F2A007);
  box-shadow: 0 12px 28px rgba(120, 12, 221, 0.28);
}

.how-content h3 {
  margin: 0 0 0.75rem;
  font-size: 1.25rem;
  font-weight: 850;
  line-height: 1.25;
  color: #111111;
}

.how-content p {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  line-height: 1.55;
  color: #555555;
}

.how-action {
  display: flex;
  justify-content: center;
  margin-top: 2.5rem;
}

.how-cta {
  border-radius: 16px;
  padding: 0.9rem 1.75rem;
  font-size: 1.1rem;
  font-weight: 800;
}
@media (max-width: 600px) {
  .how-section {
    padding-top: 3rem;
    padding-bottom: 3rem;
  }

  .how-header {
    margin-bottom: 2rem;
  }

  .how-title {
    font-size: clamp(2rem, 10vw, 3rem);
  }

  .how-subtitle {
    font-size: 1rem;
  }

  .how-list {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .how-card {
    min-height: auto;
    padding: 1.25rem;
    border-radius: 22px;
  }

  .how-number {
    width: 44px;
    height: 44px;
    margin-bottom: 1rem;
    border-radius: 14px;
    font-size: 1.1rem;
  }

  .how-content h3 {
    font-size: 1.15rem;
  }

  .how-content p {
    font-size: 0.95rem;
  }

  .how-cta {
    width: 100%;
    padding: 0.85rem 1rem;
    font-size: 1rem;
  }
}
.hero-section {
  position: relative;
  height: calc(100dvh - var(--header-h, 0px));
  height: calc(100svh - var(--header-h, 0px));
  height: calc(100vh  - var(--header-h, 0px));
  width: 100%;
  overflow: hidden;
  background: black;
  box-sizing: border-box;
  isolation: isolate;
}

.hero-video {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.35), rgba(0, 0, 0, 0.5));
  pointer-events: none;
}

.hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 0 1rem;
  gap: 1.5rem;
}

.hero-title {
  margin: 0;
  color: #ffffff;
  font-weight: 900;
  line-height: 1.2;
  font-size: clamp(2.5rem, 4vw + 2rem, 5rem);
  text-align: center;
}

.hero-cta {
  --btn-radius: 16px;
  border-radius: var(--btn-radius);
  padding: 1rem 2rem;
  font-size: 1.25rem;
  font-weight: 700;
}

.info-section {
  background: #ffffff;
  margin-top: 0;
}

.container {
  max-width: 1440px;
  margin: 0 auto;
}

.info-title {
  margin: 0 0 0.5rem 0;
  font-weight: 800;
  font-size: clamp(2.5rem, 1.25vw + 2rem, 4.25rem);
  text-align: center;
  text-wrap: balance;
}

.info-subtitle {
  margin: 0;
  font-weight: 700;
  font-size: clamp(2rem, 0.75vw + 1.85rem, 2rem);
  background: linear-gradient(90deg, #780cdd, #de12a8, #ffb310);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-align: center;
}

@media (max-width: 600px) {
  .hero-cta {
    padding: 0.75rem 1rem;
    font-size: 1rem;
  }
  .hero-title {
    font-size: clamp(1.75rem, 10vw, 3rem);
  }
  .info-title {
  font-size: clamp(2rem, 1.25vw + 0.7rem, 4.25rem);
  line-height: 1.58;
  

}

.info-subtitle {

  font-size: clamp(0.7rem, 0.75vw + 1rem, 3rem);

}
}
</style>
