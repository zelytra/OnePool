<template>
  <section>
    <h1>{{ t('pool.rules.selection') }}</h1>
    <RulesCard
        :title="t('pool.rules.card.8pool.title')"
        :amount="t('pool.rules.card.8pool.amount')"
        :description="t('pool.rules.card.8pool.description')"
        :color="'#D8E445'"
        :selected="selectedRules == GameRule.AMERICAN_8"
        @click="selectedRules = GameRule.AMERICAN_8"
    />
    <RulesCard
        :title="t('pool.rules.card.manual.title')"
        :amount="t('pool.rules.card.manual.amount')"
        :description="t('pool.rules.card.manual.description')"
        :color="'#457BE4'"
        :selected="selectedRules == GameRule.MANUAL"
        @click="selectedRules = GameRule.MANUAL"
    />
    <AlertCard color="#27A27A" @click="setGameRule()">
      <p class="button-title">{{ t('pool.action.continue') }}</p>
    </AlertCard>
  </section>
</template>

<script setup lang="ts">
import RulesCard from "@/vue/templates/RulesCard.vue";
import {useI18n} from "vue-i18n";
import AlertCard from "@/vue/templates/AlertCard.vue";
import {GameRule, GameState} from "@/objects/pool/Pool.ts";
import {ref} from "vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";

const {t} = useI18n();
const poolStore = usePoolParty();
const selectedRules = ref<GameRule>(GameRule.AMERICAN_8)

function setGameRule() {
  poolStore.poolSocket.setGameRule(selectedRules.value)
  poolStore.poolSocket.setGameStatus(GameState.INVITE_PLAYER)
}
</script>

<style scoped lang="scss">
section {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

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