<template>
  <section>
    <GlassCard color="#FFF">
      <p class="timer"><strong>{{ t('pool.game.chrono') }}</strong>: {{ elapsedTime }}</p>
    </GlassCard>
    <GlassCard color="#27A27A">
      <div class="team-content">
        <div class="content">
          <p v-for="user of poolStore.pool.game.teams.team1">{{ user }}</p>
        </div>
        <div class="content">
          <p>
            <strong :class="{'high-score':getScoreTeam(1)>getScoreTeam(2)}">
              {{ getScoreTeam(1) }}
            </strong> : <strong
              :class="{'high-score':getScoreTeam(2)>getScoreTeam(1)}">
            {{ getScoreTeam(2) }}
          </strong>
          </p>
        </div>
        <div class="content">
          <p v-for="user of poolStore.pool.game.teams.team2">{{ user }}</p>
        </div>
      </div>
    </GlassCard>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import GlassCard from "@/vue/templates/GlassCard.vue";
import {onMounted, onUnmounted, ref} from "vue";
import {Utils} from "@/objects/utils/Utils.ts";
import {PoolTeams} from "@/objects/pool/Pool.ts";

const {t} = useI18n();
const poolStore = usePoolParty();
const elapsedTime = ref<string>('0:00:00');
let interval: number;

onMounted(() => {
  interval = window.setInterval(() => {
    elapsedTime.value = Utils.updateElapsedTime(poolStore.pool.game.startingTime!)
  }, 100);
});

onUnmounted(() => {
  clearInterval(interval);
});

function getScoreTeam(teamNumber: number): number {
  let score = 0;
  for (let action of poolStore.pool.game.history) {
    const teams: PoolTeams = poolStore.pool.game.teams;
    if (teamNumber == 1 && teams.team1.includes(action.username)) {
      score += action.balls.length
    } else if (teamNumber == 2 && teams.team2.includes(action.username)) {
      score += action.balls.length
    }
  }
  return score;
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