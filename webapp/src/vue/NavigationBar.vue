<template>
  <nav>
    <router-link to="/">
      <img src="@/assets/icons/info.svg" alt="nav-icon"/>
    </router-link>
    <router-link to="/" v-if="router.currentRoute.value.name !== t('router.home')">
      <img src="@/assets/icons/note.svg" alt="nav-icon"/>
    </router-link>
    <router-link to="/">
      <img src="@/assets/icons/home.svg" alt="nav-icon"/>
    </router-link>
    <router-link to="/friends" v-if="router.currentRoute.value.name !== t('router.home')">
      <img src="@/assets/icons/friend.svg" alt="nav-icon"/>
    </router-link>
    <router-link to="/profile/:id">
      <img src="@/assets/icons/parameters.svg" alt="nav-icon"/>
    </router-link>
  </nav>
</template>

<script setup lang="ts">
import router from "@/router";
import {useI18n} from "vue-i18n";

const {t} = useI18n();
</script>

<style scoped lang="scss">
nav {
  position: fixed;
  bottom: 0;
  width: 100%;
  max-width: 600px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px;
  background: var(--secondary-background);
  margin-top: 4px;
  border-radius: 0 0 4px 4px;
}
</style>