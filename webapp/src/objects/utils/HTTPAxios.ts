import axios, { AxiosResponse } from 'axios';
import { keycloakStore } from "@/objects/stores/LoginStates.ts";

export class HTTPAxios {
  private readonly path: string;
  private static readonly header = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, DELETE',
    'Authorization': ''
  };
  private readonly url = import.meta.env.VITE_BACKEND_HOST + "/";

  constructor(path: string) {
    HTTPAxios.header.Authorization = 'Bearer ' + keycloakStore.keycloak.token;
    this.path = path;
  }

  async get(): Promise<AxiosResponse<any>> {
    const urlPath = this.url + this.path;
    console.debug("[HTTPAxios.ts][GET] " + urlPath);
    return await axios.get(urlPath, { headers: HTTPAxios.header });
  }

  async post(body: any): Promise<AxiosResponse<any>> {
    const urlPath = this.url + this.path;
    console.debug("[HTTPAxios.ts][POST] " + urlPath);
    return await axios.post(urlPath, body, { headers: HTTPAxios.header });
  }

  /*
      async delete() {
        const urlPath = this.url + this.path;
        return await axios.delete(urlPath, { headers: HTTPAxios.header });
      }

      async patch(body: any) {
        const urlPath = this.url + this.path;
        return await axios.patch(urlPath, body, { headers: HTTPAxios.header });
      }
  */

  public static async updateToken() {
    await keycloakStore.keycloak.updateToken(60).then((refresh: boolean) => {
      if (refresh) console.debug("Token was successfully refreshed");
    });
    HTTPAxios.header.Authorization = 'Bearer ' + keycloakStore.keycloak.token;
  }
}