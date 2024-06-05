<template>
  <p>User profile</p>
  <button @click="keycloakStore.keycloak.logout()">logout</button>
</template>

<script setup lang="ts">

import {keycloakStore} from "@/objects/stores/LoginStates.ts";
</script>

<style scoped lang="scss">

</style>