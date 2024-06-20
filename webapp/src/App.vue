<template>
  <AlertFactory/>
  <router-view v-slot="{ Component }" v-if="useUserStore().isUserInit">
    <transition mode="out-in">
      <component :is="Component"/>
    </transition>
  </router-view>
  <NavigationBar/>
</template>

<script setup lang="ts">
import AlertFactory from "@/vue/alerts/AlertFactory.vue";
import {useUserStore} from "@/objects/stores/UserStore.ts";
import NavigationBar from "@/vue/NavigationBar.vue";
import {useNotification} from "@/objects/stores/NotificationStore.ts";
import {onUnmounted} from "vue";

onUnmounted(() => {
  useNotification().closeSocket();
})

</script>

<style scoped lang="scss">
</style>
