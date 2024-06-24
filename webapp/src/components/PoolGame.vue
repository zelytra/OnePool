<template>
  <section>
    <transition>
      <GameRuleSelector v-if="poolStore.pool.state==GameState.SETUP"/>
      <FriendInvitation v-else-if="poolStore.pool.state==GameState.INVITE_PLAYER"/>
      <TeamingPlayers v-else-if="poolStore.pool.state==GameState.TEAMING_PLAYERS"/>
    </transition>
  </section>
</template>

<script setup lang="ts">
import GameRuleSelector from "@/components/pool/GameRuleSelector.vue";
import {onMounted} from "vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import FriendInvitation from "@/components/pool/FriendInvitation.vue";
import {GameState} from "@/objects/pool/Pool.ts";
import TeamingPlayers from "@/components/pool/TeamingPlayers.vue";

const poolStore = usePoolParty();

onMounted(() => {
  if (!poolStore.poolSocket.socket) {
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