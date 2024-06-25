<template>
  <div class="game-player-slot">
    <slot/>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
.game-player-slot {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  width: 94px;
  height: 94px;
}
</style>