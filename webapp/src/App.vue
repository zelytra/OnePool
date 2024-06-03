<template>
  <AlertFactory/>
  <router-view v-slot="{ Component }">
    <transition mode="out-in">
      <component :is="Component"/>
    </transition>
  </router-view>
</template>

<script setup lang="ts">

import AlertFactory from "@/vue/alerts/AlertFactory.vue";
</script>

<style scoped lang="scss">
</style>
