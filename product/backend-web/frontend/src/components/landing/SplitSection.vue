<template>
  <section :class="['split-section']">
    <div class="container row items-center q-col-gutter-md">
      <!-- TEXT COLUMN -->
      <div
        :class="[
          'col-12 col-md-6',
          reverseOnDesktop ? 'order-md-2' : 'order-md-1',
          'order-2'
        ]"
      >
        <div class="content">
          <slot name="title">
            <h2 v-if="title" class="title">{{ title }}</h2>
          </slot>

          <slot name="subtitle">
            <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>
          </slot>

          <slot>
            <p v-if="body" class="body">{{ body }}</p>
          </slot>

          <div v-if="ctaLabel" class="cta">
            <q-btn
              class="bg-dark-surface"
              :label="ctaLabel"
              :to="ctaTo"
              :unelevated="true"
              :no-caps="true"
              size="lg"
            />
          </div>
        </div>
      </div>

      <!-- IMAGE COLUMN -->
      <div
        :class="[
          'col-12 col-md-6',
          reverseOnDesktop ? 'order-md-1' : 'order-md-2',
          'order-1'
        ]"
      >
        <q-img
          v-if="imgSrc"
          :src="imgSrc"
          :alt="imgAlt"
          :ratio="imgRatio"
          :fit="imgFit"
          :placeholder-src="placeholderSrc"
          class="image radius-xl"
          loading="lazy"
          decoding="async"
        />
        <div v-else class="image-placeholder radius-xl">
          <slot name="image-placeholder">Изображение</slot>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>



defineProps({
  reverseOnDesktop: { type: Boolean, default: false },
  paddingY: { type: String, default: 'xl' },
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  body: { type: String, default: '' },
  ctaLabel: { type: String, default: '' },
  ctaTo: { type: [String, Object], default: null },
  ctaHref: { type: String, default: null },
  imgSrc: { type: String, default: '' },
  imgAlt: { type: String, default: '' },
  imgRatio: { type: [Number, String], default: 16 / 9 },
  imgFit: { type: String, default: 'contain' },
  placeholderSrc: { type: String, default: '' },
})

</script>

<style scoped>
.split-section {
  width: 100%;
}

.container {
  max-width: 1680px; 
  margin: 0 auto;
  padding: var(--py) 3rem; 
}

:host, .split-section { --py: 1rem; }
:host([paddingY="md"]), .split-section.q-py-md { --py: 2rem; }
:host([paddingY="lg"]), .split-section.q-py-lg { --py: 3rem; }
:host([paddingY="xl"]), .split-section.q-py-xl { --py: 5rem; }

.content {
  max-width: 680px;
}

.title {
  margin: 0 0 .5rem 0;
  font-weight: 800;
  font-size: clamp(1.75rem, 1.25vw + 1.25rem, 2.5rem);
  line-height: 1.15;
}

.subtitle {
  margin: 0 0 1rem 0;
  font-weight: 700;
  font-size: clamp(1.05rem, .75vw + .9rem, 1.25rem);
  opacity: .9;
}

.body {
  margin: 0 0 1.25rem 0;
  font-size: clamp(1.05rem, .75vw + .9rem, 1.25rem);
  opacity: .85;
}

.cta {
  margin-top: .75rem;
}

.image :deep(img) {
  border-radius: 1rem;
}
.radius-xl {
  border-radius: 1rem;
  overflow: hidden;
}

@media (min-width: 1024px) {
  .order-md-1 { order: 1 !important; }
  .order-md-2 { order: 2 !important; }
}

@media (max-width: 1023px) {
  .container {
    padding: 2rem 1rem;
    max-width: 100%;
  }
}
</style>
