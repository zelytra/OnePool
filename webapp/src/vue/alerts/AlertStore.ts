import { defineStore } from "pinia";

export type AlertState = {
  alerts: Alert[];
};

export type Alert = {
  type: AlertType;
  title: string;
  content: string;
  id?: number;
  timeout?: number;
  event?: string;
  data?: any;
};

export enum AlertType {
  VALID,
  ERROR,
  WARNING,
}

export const useAlertStore = defineStore({
  id: "alertStore",
  state: () => ({
    alerts: [],
  } as AlertState),
  actions: {
    send(alert: Alert) {
      alert.id = Math.floor(Math.random() * 1000);
      this.alerts.push(alert);

      setTimeout(() => {
        this.removeAlert(alert.id!);
      }, alert.timeout || 5000);
    },
    removeAlert(alertId: number) {
      const index = this.alerts.findIndex(alert => alert.id === alertId);
      if (index !== -1) {
        this.alerts.splice(index, 1);
      }
    }
  },
  getters: {
    getAlerts(): Alert[] {
      return this.alerts;
    }
  }
});
