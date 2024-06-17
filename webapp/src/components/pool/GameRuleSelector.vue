<template>
  <h1>{{ t('pool.rules.selection') }}</h1>
  <RulesCard
      :title="t('pool.rules.card.8pool.title')"
      :amount="t('pool.rules.card.8pool.amount')"
      :description="t('pool.rules.card.8pool.description')"
      :color="'#D8E445'"
  />
  <AlertCard color="#27A27A">
    <p class="button-title">{{ t('pool.action.continue') }}</p>
  </AlertCard>
</template>

<script setup lang="ts">
import RulesCard from "@/vue/templates/RulesCard.vue";
import {useI18n} from "vue-i18n";
import AlertCard from "@/vue/templates/AlertCard.vue";

const {t} = useI18n();
</script>

<style scoped lang="scss">
h1 {
  text-align: center;
}

p.button-title {
  color: var(--primary);
  font-weight: 800;
  font-size: 25px;
  cursor: pointer;
}
</style>