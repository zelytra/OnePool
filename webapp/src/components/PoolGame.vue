<template>
  <section>
   <GameRuleSelector/>
  </section>
</template>

<script setup lang="ts">
import {PoolSocket} from "@/objects/pool/PoolSocket.ts";
import GameRuleSelector from "@/components/pool/GameRuleSelector.vue";

const poolParty: PoolSocket = new PoolSocket();
poolParty.joinSession("")


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