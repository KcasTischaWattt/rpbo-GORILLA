<template>
  <q-btn
    flat
    no-caps
    :label="label"
    :ripple="ripple"
    class="q-mr-sm flat-button-text"
    @click="handleClick"
  />
</template>

<script setup>
const props = defineProps({
  label: {
    type: String,
    required: true
  },
  sectionId: {
    type: String,
    required: true
  },
  ripple: {
    type: [Boolean, Object],
    default: () => ({ center: true })
  }
})

const emit = defineEmits(['navigate'])

function handleClick() {
  emit('navigate', props.sectionId)
}
</script>