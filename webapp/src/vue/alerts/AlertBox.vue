<template>
  <AlertCard :color="getGradient(alert.type)" @click="emitEvent(alert.event)">
    <p class="title">{{ props.alert.title }}</p>
  </AlertCard>
</template>

<script setup lang="ts">
import {PropType} from "vue";
import {Alert, AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";
import AlertCard from "@/vue/templates/AlertCard.vue";
import eventBus, {MittEvents} from "@/objects/bus/EventBus.ts";

const props = defineProps({
  alert: {
    type: Object as PropType<Alert>,
    required: true,
  },
});

function getGradient(type: AlertType) {
  switch (type) {
    case AlertType.VALID :
      return "#27A27A"
    case AlertType.ERROR :
      return "#44FBF0"
    case AlertType.WARNING :
      return "#D8E445"
    default :
      return "#FFF"
  }
}

function emitEvent(eventName: string | undefined) {
  if (eventName) {
    eventBus.emit(eventName as keyof MittEvents, props.alert.data);
    if (props.alert.id) {
      useAlertStore().removeAlert(props.alert.id)
    }
  }
}
</script>

<style scoped lang="scss">
</style>
