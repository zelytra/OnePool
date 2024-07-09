<template>
  <section>
    <GlassCard color="#27A27A">
      <div class="team-content">
        <div class="content">
          <p v-for="user of poolStore.pool.game.teams.team1">{{ user }}</p>
        </div>
        <div class="content"/>
        <div class="content">
          <p v-for="user of poolStore.pool.game.teams.team2">{{ user }}</p>
        </div>
      </div>
    </GlassCard>
    <div class="victory-selector">
      <GlassCard :color="whoWin == PoolVictoryState.TEAM1 ? '#27A27A' : '#E44545'"
                 @click="whoWin = PoolVictoryState.TEAM1">
        <div class="glass-content">
          <h2>{{ t('pool.game.team1') }}</h2>
          <p v-if="whoWin == PoolVictoryState.TEAM1">{{ t('pool.game.win') }}</p>
          <p v-else>{{ t('pool.game.loose') }}</p>
        </div>
      </GlassCard>
      <GlassCard :color="whoWin == PoolVictoryState.TEAM2 ? '#27A27A' : '#E44545'"
                 @click="whoWin = PoolVictoryState.TEAM2">
        <div class="glass-content">
          <h2>{{ t('pool.game.team2') }}</h2>
          <p v-if="whoWin == PoolVictoryState.TEAM2">{{ t('pool.game.win') }}</p>
          <p v-else>{{ t('pool.game.loose') }}</p>
        </div>
      </GlassCard>
    </div>
    <div class="action-wrapper">
      <GlassCard color="#FF8717" @click="playAction">
        <div class="action-content-wrapper">
          <h2 class="end-turn-button">{{ t('pool.action.continue') }}</h2>
        </div>
      </GlassCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import GlassCard from "@/vue/templates/GlassCard.vue";
import {ref} from "vue";
import {GameAction, PoolVictoryState} from "@/objects/pool/Pool.ts";

const {t} = useI18n();
const poolStore = usePoolParty();
const whoWin = ref<PoolVictoryState>(PoolVictoryState.TEAM1)

function playAction() {
  const currentAction: GameAction = poolStore.pool.game.currentAction;
  if (whoWin.value == PoolVictoryState.TEAM1) {
    currentAction.username = poolStore.pool.game.teams.team2[0];
  } else {
    currentAction.username = poolStore.pool.game.teams.team1[0];
  }
  currentAction.balls.push(8)
  poolStore.poolSocket.runGameAction(poolStore.pool.game.currentAction)
}

</script>

<style scoped lang="scss">
section {
  display: flex;
  flex-direction: column;
  gap: 21px;

  p.timer {
    padding: 16px;

    strong {
      color: var(--primary);
    }
  }

  .team-content {
    width: 100%;
    padding: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .content {
      display: flex;
      flex-direction: column;
      gap: 6px;

      p {
        font-size: 18px;
        font-weight: 400;

        strong.high-score {
          color: var(--primary);
        }
      }
    }
  }

  .action-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;

    .action-content-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 8px;
      height: 60px;
    }

    h2.end-turn-button {
      color: #FF8717;
      font-weight: 700;
      font-size: 26px;
    }

    img.chrono {
      width: 29px;
    }
  }

  .victory-selector {
    display: flex;
    align-items: center;
    gap: 8px;

    .glass-content {
      padding: 16px;
      display: flex;
      gap: 32px;
      flex-direction: column;
      align-items: center;

      h2 {
        font-weight: 800;
        font-size: 24px;
      }
    }
  }

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