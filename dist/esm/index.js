import { registerPlugin } from '@capacitor/core';
const BluetoothSerial = registerPlugin('BluetoothSerial', {
    web: () => import('./web').then(m => new m.BluetoothSerialWeb()),
});
export * from './definitions';
export { BluetoothSerial };
//# sourceMappingURL=index.js.map