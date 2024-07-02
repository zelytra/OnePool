<template>
  <div class="pool-game-wrapper">
    {{ poolStore.pool.game.victoryState }}
  </div>
</template>

<script setup lang="ts">
import {usePoolParty} from "@/objects/stores/PoolStore.ts";

const poolStore = usePoolParty();
</script>

<style scoped lang="scss">

</style>