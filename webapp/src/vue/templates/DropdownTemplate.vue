<template>
  <GlassCard :color="color || '#FFF'">
    <div class="dropdown">
      <div class="title" @click="drop = !drop">
        <slot name="title"/>
      </div>
      <div class="content" :style="{maxHeight: drop ? '1000px':'0px'}">
        <div class="hidden-wrapper">
          <slot name="content"/>
        </div>
      </div>
    </div>
  </GlassCard>
</template>

<script setup lang="ts">
import GlassCard from "@/vue/templates/GlassCard.vue";

const drop = defineModel<boolean>("drop")
defineProps({
  color: String
})
</script>

<style scoped lang="scss">
.dropdown {
  padding: 8px;
  display: flex;
  flex-direction: column;
  width: 100%;
  justify-content: center;
  align-items: center;

  .title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    width: 100%;
    z-index: 99;
  }

  .content {
    width: 100%;

    .hidden-wrapper {
      margin-top: 12px;
      width: 100%;
    }
  }
}
</style>