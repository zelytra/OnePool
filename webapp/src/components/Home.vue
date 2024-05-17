<template>
  <section class="home-wrapper">
    <hr/>
    <h1>{{ t('home.title') }}</h1>
    <nav>
      <router-link
          v-for="route in routes.filter((x)=> x.meta.displayInNav)"
          :key="route.name"
          class="router-link"
          :to="route.path"
      >
        <ButtonCard color="FFF">
          <h1>{{ route.name }}</h1>
          <p class="secondary">{{ route.meta.subTitle}}</p>
        </ButtonCard>
      </router-link>
    </nav>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {routes} from "@/router";
import ButtonCard from "@/vue/templates/ButtonCard.vue";

const {t} = useI18n()
</script>

<style scoped lang="scss">
section.home-wrapper {
  h1 {
    text-align: center;
  }

  nav {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    gap: 25px;
    justify-content: center;
    align-items: center;
  }
}
</style>