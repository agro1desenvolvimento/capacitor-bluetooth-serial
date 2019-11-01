declare module "@capacitor/core" {
  interface PluginRegistry {
    BluetoothSerial: BluetoothSerialPlugin;
  }
}

export interface BluetoothSerialPlugin {
  isEnabled(): Promise<BluetoothEnabledResult>;

  echo(options: { value: string }): Promise<{value: string}>;
}

export interface BluetoothEnabledResult {
  enabled: boolean;
}