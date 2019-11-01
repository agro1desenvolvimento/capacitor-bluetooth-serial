import { WebPlugin } from '@capacitor/core';
import { BluetoothSerialPlugin } from './definitions';

export class BluetoothSerialWeb extends WebPlugin implements BluetoothSerialPlugin {
  constructor() {
    super({
      name: 'BluetoothSerial',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const BluetoothSerial = new BluetoothSerialWeb();

export { BluetoothSerial };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BluetoothSerial);
