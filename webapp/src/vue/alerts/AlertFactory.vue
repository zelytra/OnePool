<template>
  <div class="alert-container" ref="alertBox" :style="{'height':boxHeight+ 'px'}">
    <transition-group>
      <AlertBox
          v-for="alert in alerts.getAlerts"
          :key="alert.id"
          :alert="alert"
      />
    </transition-group>
  </div>
</template>

<script setup lang="ts">
import AlertBox from "./AlertBox.vue";
import {useAlertStore} from "@/vue/alerts/AlertStore.ts";
import {nextTick, ref, watch} from "vue";

const alerts = useAlertStore();
const alertBox = ref<HTMLDivElement>()
const boxHeight = ref<number>(0)

watch(alerts.getAlerts, () => {
  nextTick(() => {
    if (!alertBox.value) return;

    if (alerts.getAlerts.length == 0) {
      boxHeight.value = 0
      return
    }

    let boxTotalSize: number = 0;
    for (const element of alertBox.value.childNodes) {
      if (element.hasChildNodes()) {
        boxTotalSize += (element as HTMLDivElement).clientHeight
      }
    }
    boxHeight.value = boxTotalSize + (8 * (alerts.getAlerts.length - 1)) // 8px for the gap, minus first one
  })
})
</script>

<style scoped lang="scss">
.alert-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 90%;
  margin: 8px auto auto;
  overflow: hidden;
  transition: all 200ms;
}

.v-move,
.v-enter-active,
.v-leave-active {
  transition: all 0.3s ease-in-out;
}

.v-leave-active {
  position: absolute;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
  height: 0;
  transform: translatex(200px);
}
</style>
