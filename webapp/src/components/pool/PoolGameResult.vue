<template>
  <div class="pool-game-wrapper" v-if="poolStore.pool.gameReport">
    <GlassCard color="#27A27A">
      <div class="glass-wrapper">
        <h1 class="winner">Winner</h1>
        <div class="players" v-for="playerReport of poolStore.pool.gameReport.victoryPlayer">
          <p>{{ playerReport.username }} - {{ playerReport.pp }} <span
              :class="{
          upper:getPPDelta(playerReport)>0,
          lower:getPPDelta(playerReport)<0,
            }">{{ getPPDelta(playerReport) > 0 ? '+' : '-' }}{{ getPPDelta(playerReport) }}</span></p>
        </div>
      </div>
    </GlassCard>
    <GlassCard color="#BD3A3A">
      <div class="glass-wrapper">
        <h1 class="looser">Looser</h1>
        <div class="players" v-for="playerReport of poolStore.pool.gameReport.looserPlayer">
          <p>{{ playerReport.username }} - {{ playerReport.pp }} <span
              :class="{
          upper:getPPDelta(playerReport)>0,
          lower:getPPDelta(playerReport)<0,
            }">{{ getPPDelta(playerReport) > 0 ? '+' : '' }}{{ getPPDelta(playerReport) }}</span></p>
        </div>
      </div>
    </GlassCard>
  </div>
</template>

<script setup lang="ts">
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import GlassCard from "@/vue/templates/GlassCard.vue";
import {GameReportPlayer} from "@/objects/pool/Pool.ts";

const poolStore = usePoolParty();

function getPPDelta(playerReport: GameReportPlayer) {
  return playerReport.pp - playerReport.previousPP;
}
</script>

<style scoped lang="scss">
.pool-game-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 32px;

  .glass-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 16px;
    gap: 32px;

    h1 {
      font-size: 26px;

      &.looser {
        color: #BD3A3A;
      }

      &.winner {
        color: var(--primary);
      }
    }

    .players {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;

      p {
        font-size: 16px;

        span {
          font-weight: 800;

          &.upper {
            color: var(--primary);
          }

          &.lower {
            color: #BD3A3A;
          }
        }
      }
    }
  }
}
</style>