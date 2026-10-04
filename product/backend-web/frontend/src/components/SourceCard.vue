<template>
  <a :href="link" target="_blank" class="source-card">
    <div class="card-background" :style="{ backgroundImage: `url(${background})` }"></div>
  </a>
</template>

<script setup>
const props = defineProps({
  background: {
    type: String,
    required: true,
  },
  link: {
    type: String,
    required: true,
  },
})


const { background } = props
</script>

<style scoped>
.source-card {
  width: 200px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
}

.card-content {
  position: relative;
  padding: 8px;
  text-align: center;
}

.source-name {
  color: rgb(176, 13, 13);
  font-weight: bold;
  font-size: 14px;
}
</style>
