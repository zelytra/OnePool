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
    <GlassCard color="#44FBF0" v-if="poolStore.pool.game.currentAction">
      <div class="round-wrapper">
        <img src="@/assets/icons/move-location.svg" alt="next player"/>
        <p>{{ t('pool.game.round') }} -> {{ poolStore.pool.game.currentAction.username }}</p>
      </div>
    </GlassCard>
    <DropdownTemplate color="#27A27A">
      <template #title>
        <img class="dropdown-icon" src="@/assets/icons/pool.svg" alt="pool"/>
        <p>{{ t('pool.game.balls') }}</p>
      </template>
      <template #content>
        <BallForm :balls="getBallsForm()" @update:balls="onBallsUpdate($event)"/>
      </template>
    </DropdownTemplate>
    <DropdownTemplate color="#BD3A3A">
      <template #title>
        <img class="dropdown-icon" src="@/assets/icons/strategy.svg" alt="pool"/>
        <p>{{ t('pool.game.faults.title') }}</p>
      </template>
      <template #content>
        <div class="faults-wrapper">
          <FaultSelector v-for="fault of Object.values(PoolFault)" :fault="fault"
                         @update:selected="onFaultUpdate(fault)"
                         :selected="poolStore.pool.game.currentAction.faults.includes(fault)"/>
        </div>
      </template>
    </DropdownTemplate>
    <GlassCard color="#FFF">
      <div class="summary-wrapper">
        <div class="content-wrapper balls">
          <BallForm :balls="getBallsForm().filter(x=>x.selected)" :disable-form="true"/>
        </div>
        <hr/>
        <div class="content-wrapper faults">
          <img v-for="fault of poolStore.pool.game.currentAction.faults" :src="getFaultIcon(fault)" alt="fault-icon"/>
        </div>
      </div>
    </GlassCard>
    <div class="action-wrapper">
      <GlassCard color="#FFF" width="20%">
        <div class="action-content-wrapper chrono">
          <img class="chrono" src="@/assets/icons/timer-pause.svg" alt="chrono"/>
        </div>
      </GlassCard>
      <GlassCard color="#FF8717" @click="playAction">
        <div class="action-content-wrapper">
          <h2 class="end-turn-button">{{ t('pool.game.endTurn') }}</h2>
        </div>
      </GlassCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import GlassCard from "@/vue/templates/GlassCard.vue";
import {onMounted, onUnmounted, ref} from "vue";
import {Utils} from "@/objects/utils/Utils.ts";
import {getFaultIcon, getScoreTeam, PoolFault} from "@/objects/pool/Pool.ts";
import DropdownTemplate from "@/vue/templates/DropdownTemplate.vue";
import BallForm from "@/vue/forms/BallForm.vue";
import {BallsFormInterfaces} from "@/vue/forms/BallsFormInterfaces.ts";
import FaultSelector from "@/vue/templates/FaultSelector.vue";

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

function getBallsForm(): BallsFormInterfaces[] {
  const balls: Map<number, BallsFormInterfaces> = Utils.getRawBallsMap();

  // Disable ball in history
  for (let action of poolStore.pool.game.history) {
    for (let ball of action.balls) {
      balls.get(ball)!.disable = true;
    }
  }

  // Select ball in current action
  for (let ball of poolStore.pool.game.currentAction.balls) {
    balls.get(ball)!.selected = true;
  }

  return Array.from(balls.values());
}

function playAction() {
  poolStore.poolSocket.runGameAction(poolStore.pool.game.currentAction)
}

function onBallsUpdate(event: any) {
  poolStore.pool.game.currentAction.balls = event.value.filter((x: BallsFormInterfaces) => x.selected).map((x: BallsFormInterfaces) => x.ball);
  poolStore.poolSocket.updateGameAction(poolStore.pool.game.currentAction)
}

function onFaultUpdate(fault: PoolFault) {
  if (poolStore.pool.game.currentAction.faults.includes(fault)) {
    poolStore.pool.game.currentAction.faults.splice(poolStore.pool.game.currentAction.faults.indexOf(fault), 1);
  } else {
    poolStore.pool.game.currentAction.faults.push(fault)
  }
  poolStore.poolSocket.updateGameAction(poolStore.pool.game.currentAction)
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