<template>
  <div class="alert-card-container" :style="{
    '--gradient-start': color,
  }">
    <svg width="157" height="87" viewBox="0 0 157 87" fill="none" xmlns="http://www.w3.org/2000/svg">
      <g filter="url(#filter0_f_26_339)">
        <ellipse cx="34" cy="-2.03906" rx="73" ry="48.2578" :fill="color" fill-opacity="0.2"/>
      </g>
      <defs>
        <filter id="filter0_f_26_339" x="-89" y="-100.297" width="246" height="196.516" filterUnits="userSpaceOnUse"
                color-interpolation-filters="sRGB">
          <feFlood flood-opacity="0" result="BackgroundImageFix"/>
          <feBlend mode="normal" in="SourceGraphic" in2="BackgroundImageFix" result="shape"/>
          <feGaussianBlur stdDeviation="25" result="effect1_foregroundBlur_26_339"/>
        </filter>
      </defs>
    </svg>
    <slot/>
  </div>
</template>

<script setup lang="ts">
defineProps({
  color: {
    type: String,
    required: true
  },
})
</script>

<style scoped lang="scss">

.alert-card-container {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  width: 100%;
  gap: 8px;
  padding: 16px;
  border-radius: 15px;

  &:before {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 15px;
    border: 1px solid transparent;
    background: linear-gradient(110deg, var(--gradient-start), 10%, #222536) border-box;
    -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
  }

  &:deep(p) {
    font-size: 14px;

    strong{
      color: var(--gradient-start);
      font-weight: 400;
    }
  }

  svg {
    position: absolute;
    top: 0;
    left: 0;
    overflow: hidden;
  }
}
</style>