<template>
  <section>
    <transition name="fade">
      <div class="progress-bar" v-if="poolStore.pool.state!==GameState.RUNNING">
        <div class="progression" :style="{width:getProgressionPercentage(poolStore.pool.state)+'%'}"/>
      </div>
    </transition>
    <transition
        name="slide-fade"
        mode="out-in"
    >
      <GameRuleSelector v-if="poolStore.pool.state==GameState.SETUP" key="setup"/>
      <FriendInvitation v-else-if="poolStore.pool.state==GameState.INVITE_PLAYER" key="invite"/>
      <TeamingPlayers v-else-if="poolStore.pool.state==GameState.TEAMING_PLAYERS" key="teaming"/>
      <EightPoolGame v-else-if="poolStore.pool.state==GameState.RUNNING && poolStore.pool.rules == GameRule.AMERICAN_8"
                     key="8-pool"/>
    </transition>
  </section>
</template>

<script setup lang="ts">
import GameRuleSelector from "@/components/pool/GameRuleSelector.vue";
import {onMounted} from "vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import FriendInvitation from "@/components/pool/FriendInvitation.vue";
import {GameRule, GameState} from "@/objects/pool/Pool.ts";
import TeamingPlayers from "@/components/pool/TeamingPlayers.vue";
import EightPoolGame from "@/components/pool/pools/EightPoolGame.vue";

const poolStore = usePoolParty();

onMounted(() => {
  if (!poolStore.poolSocket.socket) {
    poolStore.poolSocket?.joinSession("")
  }
});

function getProgressionPercentage(currentState: GameState): number {
  const states = Object.values(GameState);
  const currentIndex = states.indexOf(currentState) + 1;

  if (currentIndex === -1) {
    throw new Error("Invalid game state");
  }

  const totalStates = states.length;
  return (currentIndex / (totalStates - 1)) * 100;
}

</script>

<style scoped lang="scss">
@keyframes fade-slide-out-left {
  0% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateX(-100px) scale(0.9);
  }
}

@keyframes fade-slide-in-right {
  0% {
    opacity: 0;
    transform: translateX(100px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

.slide-fade-enter-active {
  animation: fade-slide-in-right 0.2s ease forwards;
}

.slide-fade-leave-active {
  animation: fade-slide-out-left 0.2s ease forwards;
}

section {
  width: 90%;
  margin: 32px auto 12px;

  .progress-bar {
    width: 100%;
    background: var(--secondary-background);
    height: 8px;
    border-radius: 8px;
    position: relative;

    .progression {
      height: 8px;
      background: var(--primary);
      border-radius: 8px;
      position: absolute;
      top: 0;
      left: 0;
    }
  }
}
</style>
