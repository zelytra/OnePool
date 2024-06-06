<template>
  <div class="loading-wrapper">
    <div class="triangle-wrapper">
      <div class="row">
        <div class="box">
          <img class="eleven" src="@/assets/icons/balls/eleven.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="two" src="@/assets/icons/balls/two.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="thirteen" src="@/assets/icons/balls/thirteen.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="four" src="@/assets/icons/balls/four.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="five" src="@/assets/icons/balls/five.svg" alt="pool-ball"/>
        </div>
      </div>
      <div class="row">
        <div class="box">
          <img class="six" src="@/assets/icons/balls/six.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="ten" src="@/assets/icons/balls/ten.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="three" src="@/assets/icons/balls/three.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="fourteen" src="@/assets/icons/balls/fourteen.svg" alt="pool-ball"/>
        </div>

      </div>
      <div class="row">
        <div class="box">
          <img class="fifteen" src="@/assets/icons/balls/fifteen.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="eight" src="@/assets/icons/balls/eight.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="one" src="@/assets/icons/balls/one.svg" alt="pool-ball"/>
        </div>

      </div>
      <div class="row">
        <div class="box">
          <img class="seven" src="@/assets/icons/balls/seven.svg" alt="pool-ball"/>
        </div>
        <div class="box">
          <img class="twelve" src="@/assets/icons/balls/twelve.svg" alt="pool-ball"/>
        </div>

      </div>
      <div class="row">
        <div class="box">
          <img class="nine" src="@/assets/icons/balls/nine.svg" alt="pool-ball"/>
        </div>
      </div>
    </div>
    <img class="white" src="@/assets/icons/balls/white.svg" alt="pool-ball"/>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
$gap: 4px;
$animation-duration: 5s;
$ball-size: 40px;

.loading-wrapper {
  //background: red;
  width: 600px;
  height: 800px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  //background: red;

  .triangle-wrapper {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: -4;
    position: absolute;
    top: 10%;
    left: 50%;
    transform: translate(-50%, 0);

    .box {
      width: $ball-size;
      height: $ball-size;
      position: relative;
    }


    .row {
      display: flex;
      align-items: center;
      gap: $gap;
      position: relative;
    }
  }

  img {
    width: 40px;

    &.white {
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translate(-50%, 0);
      animation: white-hit $animation-duration infinite ease-in-out;
    }

    &.eleven {
      animation: eleven-hit $animation-duration infinite ease-in-out;
    }
  }
}

@keyframes white-hit {
  0% {
    bottom: 0;
  }
  5% {
    opacity: 1;
    bottom: 480px;
  }
  25% {
    opacity: 0;
    bottom: 15%;
    left: 80%;
  }
  90% {
    opacity: 0;
  }
  100% {
    opacity: 1;
    left: 50%;
    bottom: 0;
  }
}

@keyframes eleven-hit {
  0% {
    opacity: 1;
  }
  4% {
    position: absolute;
    left: 0;
    bottom: 0;
  }
  25% {
    opacity: 1;
    left: -100px;
    bottom: 200px;
  }
  30% {
    opacity: 0;
  }
  90% {
    opacity: 0;
    left: 0;
    bottom: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>