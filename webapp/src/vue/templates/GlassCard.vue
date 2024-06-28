<template>
  <div class="button-card-wrapper" :style="{
    '--gradient-start': color,
    'height': height,
    'width': width,
  }">
    <svg width="355" height="104" viewBox="0 0 355 104" fill="none" xmlns="http://www.w3.org/2000/svg">
      <g filter="url(#filter0_f_26_130)">
        <ellipse cx="178.59" cy="-1.32812" rx="134.764" ry="54.4531" :fill="color" fill-opacity="0.2"/>
      </g>
      <defs>
        <filter id="filter0_f_26_130" x="-6.17426" y="-105.781" width="369.529" height="208.906"
                filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
          <feFlood flood-opacity="0" result="BackgroundImageFix"/>
          <feBlend mode="normal" in="SourceGraphic" in2="BackgroundImageFix" result="shape"/>
          <feGaussianBlur stdDeviation="25" result="effect1_foregroundBlur_26_130"/>
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
  height: {
    type: String,
    default: () => "auto"
  },
  width: {
    type: String,
    default: () => "100%"
  }
})
</script>

<style scoped lang="scss">
.button-card-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  width: 100%;
  gap: 8px;
  border-radius: 15px;
  cursor: pointer;

  &:before {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 15px;
    border: 1px solid transparent;
    background: linear-gradient(var(--gradient-start), 10%, #222536) border-box;
    -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
  }


  svg {
    position: absolute;
    top: 0;
    left: 50%;
    transform: translate(-50%, 0);
    z-index: 99;
  }
}
</style>