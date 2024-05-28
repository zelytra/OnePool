<template>
  <AlertCard :color="getGradient(alert.type)">
    <p class="title">{{ props.alert.title }}</p>
  </AlertCard>
</template>

<script setup lang="ts">
import {PropType} from "vue";
import {Alert, AlertType} from "@/vue/alerts/AlertStore.ts";
import AlertCard from "@/vue/templates/AlertCard.vue";

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
</script>

<style scoped lang="scss">
</style>
