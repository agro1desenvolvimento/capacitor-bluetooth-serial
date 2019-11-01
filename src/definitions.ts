declare module "@capacitor/core" {
  interface PluginRegistry {
    BluetoothSerial: BluetoothSerialPlugin;
  }
}

export interface BluetoothSerialPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
