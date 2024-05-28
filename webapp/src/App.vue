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
import {onMounted} from "vue";
import {AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";


onMounted(() => {
  setInterval(() => {
    useAlertStore().send({
      content: "", timeout: 1000, title: "Une alerte", type: AlertType.VALID
    })
  }, 3000)
})
</script>

<style scoped lang="scss">
</style>
