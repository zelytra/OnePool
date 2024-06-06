<template>
  <AlertFactory/>
  <router-view v-slot="{ Component }" v-if="useUserStore().isUserInit">
    <transition mode="out-in">
      <component :is="Component"/>
    </transition>
  </router-view>
</template>

<script setup lang="ts">
import AlertFactory from "@/vue/alerts/AlertFactory.vue";
import {useUserStore} from "@/objects/stores/UserStore.ts";
</script>

<style scoped lang="scss">
</style>
