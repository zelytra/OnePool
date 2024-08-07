import {keycloakStore} from "@/objects/stores/LoginStates.ts";
import {fetch} from "@tauri-apps/plugin-http";

export class HTTPAxios {

  private readonly path: string;
  private static readonly header = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, DELETE',
    'Authorization': ''
  };
  private readonly url = import.meta.env.VITE_BACKEND_HOST + "/";

  constructor(path: string) {
    this.path = path;
  }

  async get() {
    const urlPath = this.url + this.path;
    console.debug("[HTTPAxios.ts][GET] " + urlPath)
    return await fetch(urlPath, {
      method: "GET",
      headers: HTTPAxios.header
    });
  }

  async post(body: any) {
    const urlPath = this.url + this.path;
    console.debug("[HTTPAxios.ts][POST] " + urlPath)
    return await fetch(urlPath, {
      method: "POST",
      body: body,
      headers: HTTPAxios.header
    });
  }

  public static async updateToken() {
    await keycloakStore.keycloak.updateToken(60).then((refresh: boolean) => {
      if (refresh) console.debug("Token was successfully refreshed");
    });
    HTTPAxios.header.Authorization = 'Bearer ' + keycloakStore.keycloak.token;
  }
}
