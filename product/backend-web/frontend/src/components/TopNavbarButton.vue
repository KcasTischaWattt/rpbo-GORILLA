<template>
  <q-btn
    flat
    no-caps
    :label="label"
    :to="to"
    :ripple="ripple"
    class="q-mr-sm flat-button-text"
  />
</template>

<script setup>
const { label, to, ripple } = defineProps({
  label: {
    type: String,
    required: true
  },
  to: {
    type: [String, Object],
    required: true
  },
  ripple: {
    type: [Boolean, Object],
    default: () => ({ center: true })
  }
})
</script>

<style scoped>
</style>
