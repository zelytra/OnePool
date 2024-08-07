import {reactive} from 'vue'
import Keycloak, {KeycloakConfig} from "keycloak-js";
import {useUserStore} from "@/objects/stores/UserStore.ts";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";

export interface KeycloakUser {
  username: string
}

let initOptions: KeycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_HOST,
  realm: 'OnePool',
  clientId: 'application',
}

export const keycloakStore = reactive({
  keycloak: new Keycloak(initOptions),
  isAuthenticated: false,
  user: {} as KeycloakUser,

  init() {
    this.keycloak.init({
      onLoad: 'login-required',
      redirectUri: "http://10.0.2.2:5173",
      checkLoginIframe: false
    }).then((auth: boolean) => {
      this.isAuthenticated = auth;

      if (!auth) {
        return;
      }

      this.keycloak.loadUserInfo().then((userInfo: any) => {
        useUserStore().init(userInfo.preferred_username)
      })
    })

    this.keycloak.onTokenExpired = () => {
      HTTPAxios.updateToken();
    }
  },
  loginUser(redirectionUrl: string) {
    if (!keycloakStore.isAuthenticated || !keycloakStore.keycloak.authenticated) {
      window.open(keycloakStore.keycloak.createLoginUrl({redirectUri: redirectionUrl}), '_self')
    }
  }
})
