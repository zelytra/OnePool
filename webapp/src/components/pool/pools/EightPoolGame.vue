<template>
  <section>
    <p>pool</p>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";

const {t} = useI18n();
const poolStore = usePoolParty();

</script>

<style scoped lang="scss">
section {
  display: flex;
  flex-direction: column;
  gap: 32px;
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