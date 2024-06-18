<template>
  <section v-if="poolStore.pool!=null">
    <GameRuleSelector v-if="!poolStore.pool.rules"/>
  </section>
</template>

<script setup lang="ts">
import GameRuleSelector from "@/components/pool/GameRuleSelector.vue";
import {onMounted} from "vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";

const poolStore = usePoolParty();

onMounted(() => {
  if (!poolStore.poolSocket.socket){
    poolStore.poolSocket?.joinSession("")
  }
})

</script>

<style scoped lang="scss">
section {
  width: 90%;
  display: flex;
  flex-direction: column;
  gap: 32px;
  margin: 32px auto 12px;
}
</style>