<template>
  <div :class="{'pool-fault-wrapper':true,selected:selected}" v-if="fault" @click="selected=!selected">
    <img :src="getFaultIcon(fault)" alt="fault-icon"/>
    <p>{{ t('pool.game.faults.' + poolStore.pool.rules + '.' + fault) }}</p>
  </div>
</template>

<script setup lang="ts">
import {getFaultIcon, PoolFault} from "@/objects/pool/Pool.ts";
import {useI18n} from "vue-i18n";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";

const fault = defineModel<PoolFault>('fault')
const selected = defineModel<boolean>("selected", {default: () => false})
const poolStore = usePoolParty();
const {t} = useI18n();
</script>

<style scoped lang="scss">
.pool-fault-wrapper {
  display: flex;
  align-items: center;
  padding: 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  overflow: hidden;
  gap: 12px;
  border: solid 1px transparent;
  z-index: 9999;

  img {
    width: 28px;
    height: 28px;
  }

  p {
    text-wrap: wrap;
  }

  &.selected {
    border: solid 1px #BD3A3A;
    background: rgba(189, 58, 58, 0.3);
  }

}
</style>