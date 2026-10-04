<template>
  <q-layout view="hhh lpR fff">

    <navbar />


    <q-page-container>
      <router-view />
    </q-page-container>


    <footer-comp />
  </q-layout>
</template>

<script setup>
import Navbar from 'src/components/landing/LandingNavbar.vue'
import FooterComp from 'src/components/FooterComponent.vue'
</script>

<style scoped>
</style>
