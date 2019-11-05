import { WebPlugin } from '@capacitor/core';
import { BluetoothSerialPlugin } from './definitions';
import { OptionsRequiredError } from "./utils/errors";

export class BluetoothSerialWeb extends WebPlugin implements BluetoothSerialPlugin {
  constructor() {
    super({
      name: 'BluetoothSerial',
      platforms: ['web']
    });
  }

  async isEnabled(): Promise<import("./definitions").BluetoothEnabledResult> {
    throw new Error("Method not implemented.");
  }

  async scan(): Promise<import("./definitions").BluetoothScanResult> {
    throw new Error("Method not implemented.");
  }

  async connect(options: import("./definitions").BluetoothConnectOptions): Promise<void> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

  async disconnect(options: import("./definitions").BluetoothConnectOptions): Promise<void> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

  async isConnected(options: import("./definitions").BluetoothConnectOptions): Promise<import("./definitions").BluetoothConnectResult> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

  async read(options: import("./definitions").BluetoothReadOptions): Promise<import("./definitions").BluetoothDataResult> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

  async readUntil(options: import("./definitions").BluetoothReadUntilOptions): Promise<import("./definitions").BluetoothDataResult> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

  async enableNotifications(options: import("./definitions").BluetoothEnableNotificationsOptions): Promise<import("./definitions").BluetoothEnableNotificationsResult> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

  async disableNotifications(options: import("./definitions").BluetoothDisableNotificationsOptions): Promise<void> {
    if(!options){
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error("Method not implemented.");
  }

}

const BluetoothSerial = new BluetoothSerialWeb();

export { BluetoothSerial };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BluetoothSerial);
