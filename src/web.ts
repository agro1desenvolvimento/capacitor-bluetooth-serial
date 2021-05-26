
import { WebPlugin } from '@capacitor/core';
import { BluetoothConnectOptions, BluetoothConnectResult, BluetoothDataResult, BluetoothDisableNotificationsOptions, BluetoothEnabledResult, BluetoothEnableNotificationsOptions, BluetoothEnableNotificationsResult, BluetoothReadOptions, BluetoothReadUntilOptions, BluetoothScanResult, BluetoothSerialPlugin, BluetoothWriteOptions } from './definitions';
import { OptionsRequiredError } from './utils/errors';

export class BluetoothSerialWeb extends WebPlugin implements BluetoothSerialPlugin {

  async isEnabled(): Promise<BluetoothEnabledResult> {
    throw new Error('Method not implemented.');
  }

  async enable(): Promise<BluetoothEnabledResult> {
    throw new Error('Method not implemented.');
  }

  async scan(): Promise<BluetoothScanResult> {
    throw new Error('Method not implemented.');
  }

  async connect(options: BluetoothConnectOptions): Promise<void> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async connectInsecure(options: BluetoothConnectOptions): Promise<void> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async disconnect(options: BluetoothConnectOptions): Promise<void> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async isConnected(options: BluetoothConnectOptions): Promise<BluetoothConnectResult> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async read(options: BluetoothReadOptions):
      Promise<BluetoothDataResult> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async readUntil(options: BluetoothReadUntilOptions): Promise<BluetoothDataResult> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async write(options: BluetoothWriteOptions): Promise<void> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async enableNotifications(options: BluetoothEnableNotificationsOptions):
      Promise<BluetoothEnableNotificationsResult> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

  async disableNotifications(options: BluetoothDisableNotificationsOptions):
      Promise<void> {
    if (!options) {
      return Promise.reject(new OptionsRequiredError());
    }
    throw new Error('Method not implemented.');
  }

}

const BluetoothSerial = new BluetoothSerialWeb();

export { BluetoothSerial };