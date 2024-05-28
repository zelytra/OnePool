import {defineStore} from "pinia";

export type AlertState = {
  alerts: Alert[]
};

export type Alert = {
  type: AlertType;
  title: string;
  content: string;
  id?: number;
  timeout?: number
}

export enum AlertType {
  VALID,
  ERROR,
  WARNING
}

export const useAlertStore = defineStore({
  id: "alertStore",
  state: () => ({
    alerts: []
  } as AlertState),
  actions: {
    send(alert: Alert) {
      alert.id = Math.floor(Math.random() * 1000);
      this.alerts.push(alert);

      setTimeout(() => {
        const index = this.alerts.indexOf(alert, 0);
        if (index > -1) {
          this.alerts.splice(index, 1);
        }
      }, alert.timeout || 5000);
    }
  },
  getters: {
    getAlerts(): Alert[] {
      return this.alerts
    }
  }
})