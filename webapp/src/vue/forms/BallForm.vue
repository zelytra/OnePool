<template>
  <div class="balls-wrapper">
    <div class="flex-wrapper" v-for="ball of balls">
    <span :class="{
      ball:true,
      selected:ball.selected,
      disabled:ball.disable,
      striped:ball.ball > 8,
    }"
          :style="{
      '--color-ball':getBallColor(ball.ball)
    }" @click="ball.selected = !ball.disable? !ball.selected: !ball.disable">
      {{ ball.ball }}
    </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import {BallsFormInterfaces} from "@/vue/forms/BallsFormInterfaces.ts";

const balls = defineModel<BallsFormInterfaces[]>('balls')

function getBallColor(ballValue: number) {
  switch (ballValue) {
    case 1:
    case 9:
      return "#D8BF0F";
    case 2:
    case 10:
      return "#213360";
    case 3:
    case 11:
      return "#B5191B";
    case 4:
    case 12:
      return "#452778";
    case 5:
    case 13:
      return "#EB7914";
    case 6:
    case 14:
      return "#33A936";
    case 7:
    case 15:
      return "#4E150D";
    case 8:
      return "#000000";
    default:
      return "#FFF"
  }
}
</script>

<style scoped lang="scss">
.balls-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;

  .flex-wrapper {
    flex: 1 0 12%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  span.ball {
    text-align: center;
    width: 40px;
    height: 40px;
    border: solid 4px white;
    border-radius: 50px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 99;

    &:hover {
      border: solid 4px var(--color-ball);
      color: var(--color-ball);

      &.striped {
        background: linear-gradient(to bottom, var(--color-ball) 20%, white 20%, white 80%, var(--color-ball) 80%);
      }
    }

    &.selected {
      border: solid 4px var(--color-ball);
      color: var(--color-ball);

      &.striped {
        background: linear-gradient(to bottom, var(--color-ball) 20%, white 20%, white 80%, var(--color-ball) 80%);
      }
    }

    &.disabled {
      border: solid 4px var(--secondary-text);
      color: var(--secondary-text);
    }
  }
}
</style>