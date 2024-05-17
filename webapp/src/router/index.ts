import {createWebHistory, createRouter} from "vue-router";
import Home from "../components/Home.vue";
import {i18n} from "@/objects/i18n";
import UserProfile from "@/components/UserProfile.vue";
import FriendsList from "@/components/FriendsList.vue";
import LeaderBoard from "@/components/LeaderBoard.vue";
import PoolGame from "@/components/PoolGame.vue";

const {t} = i18n.global;

declare module 'vue-router' {
  interface RouteMeta {
    icon?: string,
    role?: string,
    tooltip?: string
    requiresAuth?: boolean
    displayInNav: boolean,
    subTitle?: string
  }
}

export const routes = [
  {
    path: "/",
    name: t('router.home'),
    component: Home,
    meta: {
      displayInNav: false
    }
  },
  {
    path: "/pool",
    name: t('router.pool.title'),
    component: PoolGame,
    meta: {
      displayInNav: true,
      subTitle: t('router.pool.subtitle'),
    }
  },
  {
    path: "/leaderboard",
    name: t('router.leaderboard.title'),
    component: LeaderBoard,
    meta: {
      displayInNav: true,
      subTitle: t('router.leaderboard.subtitle'),
    }
  },
  {
    path: "/friends",
    name: t('router.friends.title'),
    component: FriendsList,
    meta: {
      displayInNav: true,
      subTitle: t('router.friends.subtitle'),
    }
  },
  {
    path: "/profile/:id",
    name: t('router.profile.title'),
    component: UserProfile,
    meta: {
      displayInNav: true,
      subTitle: t('router.profile.subtitle'),
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
