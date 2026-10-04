<template>
  <div class="page-container">
    <div class="grafana-container">
      <iframe :src="grafanaUrl" frameborder="0" width="100%" height="100%"></iframe>
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue';
import { useQuasar } from 'quasar';

const $q = useQuasar();

const grafanaUrl = ref('');
const initialGrafanaUrl = 'https://grafana.nt.fyi/login'; 


const updateGrafanaUrl = () => {
  const path = window.location.hash.substring(1); // это не работает, есть другой вариант, но там проблема с CORS, можно сделать, как разберемся с nginx и сделаем единый логин
  const theme = $q.dark.isActive ? 'dark' : 'light'; 
  grafanaUrl.value = `${initialGrafanaUrl}${path}?theme=${theme}`;
};

watchEffect(() => {
  updateGrafanaUrl();
});

updateGrafanaUrl();
</script>


<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  height: 100vh; 
}


.grafana-container {
  flex-grow: 1;
  overflow: hidden; 
}
</style>