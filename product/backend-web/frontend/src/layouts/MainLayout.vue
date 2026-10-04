<template>
  <q-layout view="hhh lpr fff">
    <Navbar />
    

    <q-page-container>
      <q-page >
        <router-view />
      </q-page>
    </q-page-container>
    <Footer />
  </q-layout>
</template>

<script setup>
  import Footer from 'src/components/FooterComponent.vue';
  import Navbar from '../components/TopNavbar.vue'
</script>
