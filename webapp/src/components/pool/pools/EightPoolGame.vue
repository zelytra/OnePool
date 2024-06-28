<template>
  <section>
    <ButtonCard color="#FFF">
      <p class="timer"><strong>{{ t('pool.game.chrono') }}</strong>: {{ elapsedTime }}</p>
    </ButtonCard>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import ButtonCard from "@/vue/templates/GlassCard.vue";
import {onMounted, onUnmounted, ref} from "vue";
import {Utils} from "@/objects/utils/Utils.ts";

const {t} = useI18n();
const poolStore = usePoolParty();
const elapsedTime = ref<string>('0:00:00');
let interval: number;

onMounted(() => {
  interval = window.setInterval(() => {
    elapsedTime.value = Utils.updateElapsedTime(poolStore.pool.game.startingTime!)
  }, 10);
});

onUnmounted(() => {
  clearInterval(interval);
});


</script>

<style scoped lang="scss">
section {
  display: flex;
  flex-direction: column;
  gap: 32px;

  p.timer {
    padding: 16px;

    strong {
      color: var(--primary);
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