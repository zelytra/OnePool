<template>
  <div class="rules-wrapper" :style="{
    '--gradient-start': color || '#FFF',
  }">
    <svg width="373" height="85" viewBox="0 0 373 85" fill="none" xmlns="http://www.w3.org/2000/svg">
      <g filter="url(#filter0_f_334_412)">
        <ellipse cx="187.651" cy="-0.851559" rx="141.602" ry="34.9141" :fill="color || '#FFFF'" fill-opacity="0.2"/>
      </g>
      <defs>
        <filter id="filter0_f_334_412" x="-3.95068" y="-85.7656" width="383.204" height="169.828"
                filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
          <feFlood flood-opacity="0" result="BackgroundImageFix"/>
          <feBlend mode="normal" in="SourceGraphic" in2="BackgroundImageFix" result="shape"/>
          <feGaussianBlur stdDeviation="25" result="effect1_foregroundBlur_334_412"/>
        </filter>
      </defs>
    </svg>

    <div class="title">
      <h2>{{ props.title }}</h2>
      <p>{{ props.amount }}</p>
    </div>
    <p class="description">{{ props.description }}</p>
    <transition>
      <img v-if="selected" src="@/assets/icons/check-circle.svg" alt="selected"/>
    </transition>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  title: String,
  description: String,
  amount: String,
  color: String,
  selected: Boolean
})
</script>

<style scoped lang="scss">
.rules-wrapper {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  width: 100%;
  min-height: 56px;
  gap: 8px;
  padding: 16px;
  border-radius: 15px;
  z-index: 1;
  cursor: pointer;

  &:before {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 15px;
    border: 1px solid transparent;
    background: linear-gradient(180deg, var(--gradient-start), 10%, #222536) border-box;
    -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    z-index: -1;
  }

  .title {
    display: flex;
    justify-content: space-between;

    h2 {
      color: var(--gradient-start);
      font-weight: 600;
      font-size: 18px;
    }

    p {
      color: var(--green);
      font-weight: 800;
      font-size: 15px;
    }
  }

  svg {
    position: absolute;
    top: 0;
    left: 50%;
    transform: translate(-50%);
    overflow: hidden;
  }

  img {
    position: absolute;
    bottom: 10px;
    right: 10px;
  }
}
</style>