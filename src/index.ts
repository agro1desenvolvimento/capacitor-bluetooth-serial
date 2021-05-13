import { registerPlugin } from '@capacitor/core';

import type { BluetoothSerialPlugin } from './definitions';

const BluetoothSerial= registerPlugin<BluetoothSerialPlugin>(
  'BluetoothSerial',
  {
    web: () => import('./web').then(m => new m.BluetoothSerialWeb()),
  },
);

export * from './definitions';
export { BluetoothSerial};