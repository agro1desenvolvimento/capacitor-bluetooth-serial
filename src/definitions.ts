export interface BluetoothSerialPlugin {

  isEnabled(): Promise<BluetoothEnabledResult>;

  enable(): Promise<BluetoothEnabledResult>;

  scan(): Promise<BluetoothScanResult>;

  connect(options: BluetoothConnectOptions): Promise<void>;

  connectInsecure(options: BluetoothConnectOptions): Promise<void>;

  disconnect(options: BluetoothConnectOptions): Promise<void>;

  isConnected(options: BluetoothConnectOptions): Promise<BluetoothConnectResult>;

  read(options: BluetoothReadOptions): Promise<BluetoothDataResult>;

  readUntil(options: BluetoothReadUntilOptions): Promise<BluetoothDataResult>;

  write(options: BluetoothWriteOptions): Promise<void>;

  enableNotifications(options: BluetoothEnableNotificationsOptions): Promise<BluetoothEnableNotificationsResult>;

  disableNotifications(options: BluetoothDisableNotificationsOptions): Promise<void>;

}

export interface BluetoothEnabledResult {
  enabled: boolean;
}

export interface BluetoothScanResult {
  devices: BluetoothDevice[];
}

export interface BluetoothConnectResult {
  connected: boolean;
}

export interface BluetoothDataResult {
  data: string;
}

export interface BluetoothEnableNotificationsResult {
  eventName: string;
}

export interface BluetoothDevice {
  name: string;
  id: string;
  address: string;
  class: number;
  uuid: string;
  rssi: number;
}

export interface BluetoothConnectOptions {
  address: string;
}

export interface BluetoothReadOptions {
  address: string;
}

export interface BluetoothReadUntilOptions {
  address: string;
  delimiter: string;
}

export interface BluetoothWriteOptions {
    address: string;
    value: string;
}

export interface BluetoothEnableNotificationsOptions {
  address: string;
  delimiter: string;
}

export interface BluetoothDisableNotificationsOptions {
  address: string;
}
