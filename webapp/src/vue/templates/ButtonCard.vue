<template>
  <div class="button-card-wrapper">
    <slot/>
  </div>
</template>

<script setup lang="ts">
defineProps({
  color: {
    type: String,
    required: true
  }
})
</script>

<style scoped lang="scss">

</style>