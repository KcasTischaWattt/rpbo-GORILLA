<template>
    <q-layout view="hhh Lpr fff">
      <Navbar />
  
      <Drawer />
  
      <q-page-container>
        <router-view />
      </q-page-container>
  
      <Footer />
    </q-layout>
  </template>
  
  <script setup>
  import Footer from 'src/components/FooterComponent.vue'
  import Navbar from '../components/TopNavbar.vue'
  import Drawer from '../components/SettingsDrawer.vue'

  </script>
  