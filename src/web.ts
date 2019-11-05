import { WebPlugin } from '@capacitor/core';
import { BluetoothSerialPlugin } from './definitions';

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
    throw new Error("Method not implemented.");
  }

  async disconnect(options: import("./definitions").BluetoothConnectOptions): Promise<void> {
    throw new Error("Method not implemented.");
  }

  async isConnected(options: import("./definitions").BluetoothConnectOptions): Promise<import("./definitions").BluetoothConnectResult> {
    throw new Error("Method not implemented.");
  }

  async read(options: import("./definitions").BluetoothReadOptions): Promise<import("./definitions").BluetoothDataResult> {
    throw new Error("Method not implemented.");
  }

  async readUntil(options: import("./definitions").BluetoothReadUntilOptions): Promise<import("./definitions").BluetoothDataResult> {
    throw new Error("Method not implemented.");
  }

  async enableNotifications(options: import("./definitions").BluetoothEnableNotificationsOptions): Promise<import("./definitions").BluetoothEnableNotificationsResult> {
    throw new Error("Method not implemented.");
  }

  async disableNotifications(options: import("./definitions").BluetoothDisableNotificationsOptions): Promise<void> {
    throw new Error("Method not implemented.");
  }

}

const BluetoothSerial = new BluetoothSerialWeb();

export { BluetoothSerial };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BluetoothSerial);
