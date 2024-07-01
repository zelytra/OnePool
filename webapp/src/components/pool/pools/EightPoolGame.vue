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
    <GlassCard color="#44FBF0" v-if="currentAction">
      <div class="round-wrapper">
        <img src="@/assets/icons/move-location.svg" alt="next player"/>
        <p>{{ t('pool.game.round') }} -> {{ currentAction.username }}</p>
      </div>
    </GlassCard>
    <DropdownTemplate color="#27A27A">
      <template #title>
        <img class="dropdown-icon" src="@/assets/icons/pool.svg" alt="pool"/>
        <p>{{ t('pool.game.balls') }}</p>
      </template>
      <template #content>
        <BallForm :balls="balls"/>
      </template>
    </DropdownTemplate>
    <DropdownTemplate color="#BD3A3A">
      <template #title>
        <img class="dropdown-icon" src="@/assets/icons/strategy.svg" alt="pool"/>
        <p>{{ t('pool.game.faults.title') }}</p>
      </template>
      <template #content>
        <div class="faults-wrapper">
          <FaultSelector v-for="fault of Object.values(PoolFault)" :fault="fault"/>
        </div>
      </template>
    </DropdownTemplate>
    <GlassCard color="#FFF">
      <div class="summary-wrapper">
        <div class="content-wrapper balls">
          <BallForm :balls="balls.filter(x=>x.selected)" :disable-form="true"/>
        </div>
        <hr/>
        <div class="content-wrapper faults">
          <img v-for="fault of currentAction.faults" :src="getFaultIcon(fault)" alt="fault-icon"/>
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
import {GameAction, getFaultIcon, PoolFault, PoolTeams} from "@/objects/pool/Pool.ts";
import DropdownTemplate from "@/vue/templates/DropdownTemplate.vue";
import BallForm from "@/vue/forms/BallForm.vue";
import {BallsFormInterfaces} from "@/vue/forms/BallsFormInterfaces.ts";
import FaultSelector from "@/vue/templates/FaultSelector.vue";

const {t} = useI18n();
const poolStore = usePoolParty();
const elapsedTime = ref<string>('0:00:00');
const balls = ref<BallsFormInterfaces[]>([
  {ball: 1, selected: false, disable: false},
  {ball: 2, selected: false, disable: false},
  {ball: 3, selected: false, disable: false},
  {ball: 4, selected: false, disable: false},
  {ball: 5, selected: false, disable: false},
  {ball: 6, selected: false, disable: false},
  {ball: 7, selected: false, disable: false},
  {ball: 8, selected: false, disable: false},
  {ball: 9, selected: false, disable: false},
  {ball: 10, selected: false, disable: false},
  {ball: 11, selected: false, disable: false},
  {ball: 12, selected: false, disable: false},
  {ball: 13, selected: false, disable: false},
  {ball: 14, selected: false, disable: false},
  {ball: 15, selected: false, disable: false},
])
const currentAction = ref<GameAction>({
  balls: [1, 2, 3],
  faults: [PoolFault.PLAYER_NO_GROUND_TOUCH, PoolFault.NO_BAND],
  roundId: 0,
  username: "admin"
})
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

  .round-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
  }

  img.dropdown-icon {
    width: 50px;
    height: 50px;
  }

  .faults-wrapper {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 8px;
  }

  .summary-wrapper {
    display: flex;
    align-items: center;
    padding: 8px;
    justify-content: space-between;
    width: 100%;
    height: auto;
    gap: 8px;

    hr {
      width: 2px;
      height: 60px;
      background: rgba(255, 255, 255, 0.5);
    }

    img {
      width: 36px;
      height: 36px;
    }

    .content-wrapper {
      width: 50%;
      height: 100%;

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