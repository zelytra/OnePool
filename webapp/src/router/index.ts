import {createWebHistory, createRouter} from "vue-router";
import Home from "../components/Home.vue";

declare module 'vue-router' {
  interface RouteMeta {
    icon?: string,
    role?: string,
    tooltip?: string
    requiresAuth?: boolean
    displayInNav: boolean
  }
}

export const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: {
      displayInNav: false
    }
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

/*
router.beforeEach((to, _from) => {
  if (to.meta.requiresAuth) {
    if (!keycloakStore.isAuthenticated || !keycloakStore.keycloak.authenticated) {
      router.push('auth')
    }
  }
})
*/

export default router;
