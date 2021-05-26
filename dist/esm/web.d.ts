import { WebPlugin } from '@capacitor/core';
import { BluetoothConnectOptions, BluetoothConnectResult, BluetoothDataResult, BluetoothDisableNotificationsOptions, BluetoothEnabledResult, BluetoothEnableNotificationsOptions, BluetoothEnableNotificationsResult, BluetoothReadOptions, BluetoothReadUntilOptions, BluetoothScanResult, BluetoothSerialPlugin, BluetoothWriteOptions } from './definitions';
export declare class BluetoothSerialWeb extends WebPlugin implements BluetoothSerialPlugin {
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
declare const BluetoothSerial: BluetoothSerialWeb;
export { BluetoothSerial };
