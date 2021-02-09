# Capacitor Bluetooth Serial Plugin

A client implementation for interacting with Bluetooth

Supported platforms

- [ ] Web
- [x] Android
- [ ] iOS

## Usage

Install the plugin via npm
```
npm install --save capacitor-bluetooth-serial
```

In your capacitor project, make sure to register the Android plugin in
in the projects `MainActivity` as follows

```java
import com.bluetoothserial.plugin.BluetoothSerial;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
      add(BluetoothSerial.class);
    }});
  }
}
```



```typescript
import {Plugins} from "@capacitor/core";

const { BluetoothSerial } = Plugins;

//...do something with plugin

```

## API Documentation

Interface and type definitions can be found [here](./src/definitions.ts).

# API

## Methods

- [BluetoothSerial.isEnabled](#isEnabled)
- [BluetoothSerial.enable](#enable)
- [BluetoothSerial.scan](#scan)
- [BluetoothSerial.connect](#connect)
- [BluetoothSerial.connectInsecure](#connectInsecure)
- [BluetoothSerial.disconnect](#disconnect)
- [BluetoothSerial.isConnected](#isConnected)
- [BluetoothSerial.read](#read)
- [BluetoothSerial.readUntil](#readUntil)
- [BluetoothSerial.enableNotifications](#enableNotifications)
- [BluetoothSerial.disableNotifications](#disableNotifications)
- [BluetoothSerial.enableRawNotifications](#enableRawNotifications)
- [BluetoothSerial.disableRawNotifications](#disableRawNotifications)
- [BluetoothSerial.write](#write)

## isEnabled

Reports if bluetooth is enabled.

  `isEnabled(): Promise<BluetoothEnabledResult>;`

### Description

Function `isEnabled` calls the success whatever bluetooth is enabled or not. The promise will contain an attribute `enabled` indicating if bluetooth is enabled or *not* enabled. The failure callback will be called only if an error occurs.

If you want to enable bluetooth afterwards, you can use #enable directly, once #enable also check if the bluetooth is conected or not.

### Parameters

None.

### Quick Example

```typescript
BluetoothSerial
  .isEnabled()
  .then((response: BluetoothEnabledResult) => {
    const message = response.enabled ? 'enabled' : 'disabled';
    console.log(`Bluetooth is ${message}`);
  })
  .catch(() => {
    console.log('Error checking bluetooth status');
  });
```

## enable

  Enable bluetooth if it is not enabled. Also request permissions for bluetooth access if it is necessary.

  `enable(): Promise<BluetoothEnabledResult>;`

### Description

Function `enable` calls the success whatever bluetooth is successfully enabled or not. The promise will contain an attribute `enabled` indicating if bluetooth is enabled or *not* enabled after the process. The failure callback will be called only if an error occurs.

If the app does not have permission to use bluetooth, it will request it.

### Parameters

None.

### Quick Example

```typescript
BluetoothSerial
  .enable()
  .then((response: BluetoothEnabledResult) => {
    const message = response.enabled ? 'enabled' : 'disabled';
    console.log(`Bluetooth is ${message}`);
  })
  .catch(() => {
    console.log('Error enabling bluetooth');
  });
```

## scan

Discover devices visible and close to the device

  `scan(): Promise<BluetoothScanResult>;`

### Description

#### Android

Function `scan` discovers Bluetooth devices close to the device and visible. The success callback is called with a list of objects similar to `list`, or an empty list if no devices are found.

Example list passed to success callback.

```json
[{
    "class": 0,
    "id": "00:11:22:33:44:55",
    "address": "00:11:22:33:44:55",
    "name": "Device 1"
}, {
    "class": 7936,
    "id": "01:23:6645:4D67:89:00",
    "address": "01:23:6645:4D67:89:00",
    "name": "Device 2"
}]
```

The discovery process takes a while to happen.
You may want to show a progress indicator while waiting for the discover proces to finish, and the sucess callback to be invoked.

Calling `connect` on an unpaired Bluetooth device should begin the Android pairing process.

### Parameters

None.

### Quick Example

```typescript
BluetoothSerial
  .scan()
  .then((result: BluetoothScanResult) => {
    result.devices.forEach((device: BluetoothDevice) {
        console.log(device.id);
    });
  })
  .catch(() => {
    console.log('Error scanning devices');
  });
```

## connect

Connect to a Bluetooth device.

  `connect(options: BluetoothConnectOptions): Promise<void>`;

### Description

Function `connect` connects to a Bluetooth device.  The callback Success will be called when the connection is successful.  Failure is called if the connection fails.

#### Android
For Android, `connect` takes a MAC address of the remote device.

### Parameters

- { __address__ }: Identifier of the remote device.

### Quick Example

```typescript
BluetoothSerial
  .connect({
    address: '00:11:22:33:44:55',
  })
  .then(() => {
    console.log('Successfully connected')
  })
  .catch(() => {
    console.log('Error connecting...');
  });
```

## connectInsecure

Connect insecurely to a Bluetooth device.

  `connectInsecure(options: BluetoothConnectOptions): Promise<void>`;

### Description

Function `connectInsecure` connects to a Bluetooth device.  The callback Success will be called when the connection is successful.  Failure is called if the connection fails.
Function `connectInsecure` works like [BluetoothSerial.connect](#connect), but creates an insecure connection to a Bluetooth device. See the [Android docs](https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createInsecureRfcommSocketToServiceRecord(java.util.UUID)) for more information.

#### Android
For Android, `connectInsecure` takes a MAC address of the remote device.

### Parameters

- { __address__ }: Identifier of the remote device.

### Quick Example

```typescript
BluetoothSerial
  .connectInsecure({
    address: '00:11:22:33:44:55',
  })
  .then(() => {
    console.log('Successfully connected')
  })
  .catch(() => {
    console.log('Error connecting...');
  });
```

## disconnect

Disconnect a Bluetooth device.

  `disconnect(options: BluetoothConnectOptions): Promise<void>`;

### Description

Function `disconnect` disconnects a Bluetooth device.  The callback Success will be called when the disconnection is successful.  Failure is called if the disconnection fails.

#### Android
For Android, `disconnect` takes a MAC address of the remote device.

**Warning**: If no address is passed, all devices will be disconnected.

### Parameters

- { __address__ }: Identifier of the remote device.

### Quick Example

```typescript
BluetoothSerial
  .disconnect({
    address: '00:11:22:33:44:55',
  })
  .then(() => {
    console.log('Successfully disconnected')
  })
  .catch(() => {
    console.log('Error disconnecting...');
  });
```

## isConnected

Reports the connection status.

  `isConnected(options: BluetoothConnectOptions): Promise<BluetoothConnectResult>`;

### Description

Function `isConnected` calls the success callback with the connection status (connected or not connected). Failure will be called only if an error occurs.

#### Android
For Android, `isConnected` takes a MAC address of the remote device.

### Parameters

- { __address__ }: Identifier of the remote device.

### Quick Example

```typescript
BluetoothSerial
  .isConnected({
    address: '00:11:22:33:44:55',
  })
  .then((result: BluetoothConnectResult) => {
    const status = result.connected ? 'connected' : 'disconnected';
    console.log(`Device is ${status}`);
  })
  .catch(() => {
    console.log('Error checking connection status');
  });
```

## read

Reads data from the buffer.

  `read(options: BluetoothReadOptions): Promise<BluetoothDataResult>`;

### Description

Function `read` reads the data from the buffer. The data is passed to the success callback as a String.  Calling `read` when no data is available will pass an empty String to the callback.

### Parameters

- { __address__ }: Identifier of the remote device.

### Quick Example

```typescript
BluetoothSerial
  .read({
    address: '00:11:22:33:44:55',
  })
  .then((result: BluetoothDataResult) => {
    console.log(result.data);
  })
  .catch(() => {
    console.log('Error reading data from device');
  });
```

## readUntil

Reads data from the buffer until it reaches a delimiter.

  `readUntil(options: BluetoothReadUntilOptions): Promise<BluetoothDataResult>`;

### Description

Function `readUntil` reads the data from the buffer until it reaches a delimiter.  The data is passed to the success callback as a String.  If the buffer does not contain the delimiter, an empty String is passed to the callback.

### Parameters

- { __address__ }: Identifier of the remote device.
- { __delimiter__ }: Delimiter.

### Quick Example

```typescript
BluetoothSerial
  .readUntil({
    address: '00:11:22:33:44:55',
    delimiter: '\n',
  })
  .then((result: BluetoothDataResult) => {
    console.log(result.data);
  })
  .catch(() => {
    console.log('Error reading data from device');
  });
```

## enableNotifications

Enable and be notified when any data is received.

  `enableNotifications(options: BluetoothEnableNotificationsOptions): Promise<BluetoothEnableNotificationsResult>`;

### Description

Function `enableNotifications` enable notifications. The success callback will return an event name. In order to retrieve the values, one has to use an Event Listener with the returned event name.

``` typescript
const listener = BluetoothSerial.addListener( eventName , (data: BluetoothDataResult) => {

    const { value } = data.data;
    //Do something with the data
q
});
```

### Parameters

- { __address__ }: Identifier of the remote device.
- { __delimiter__ }: Delimiter for notification.

### Quick Example

```typescript
BluetoothSerial
  .enableNotifications({
    address: '00:11:22:33:44:55',
    delimiter: '\n',
  })
  .then((result: BluetoothEnableNotificationsResult) => {
    event = BluetoothSerial.addListener(result.eventName, (data: BluetoothDataResult) => {
        console.log(data.data);
      });
  })
  .catch(() => {
    console.log('Error enabling listener for device');
  });
```

## disableNotifications

Stops the propagation of value changes.

  `disableNotifications(options: BluetoothDisableNotificationsOptions): Promise<void>`;

### Description

Function `disableNotifications` disable notifications. Additionally, the event listener has to be removed.

```typescript
listener.remove();
```

### Parameters

- { __address__ }: Identifier of the remote device.

### Quick Example

```typescript
BluetoothSerial
  .disableNotifications({
    address: '00:11:22:33:44:55',
  })
  .then(() => {
    event.remove();
  })
  .catch(() => {
    console.log('Error disabling listener for device');
  });
```

## enableRawNotifications

  Under development.

## disableRawNotifications

  Under development.

## write

Write data to the buffer.

  `write(options: BluetoothWriteOptions): Promise<void>`;

### Description

Function `write` writes data to the buffer.

### Parameters

- { __address__ }: Identifier of the remote device.
- { __value__ }: String to send.

### Quick Example

```typescript
BluetoothSerial
  .write({
    address: '00:11:22:33:44:55',
    value: 'Hello world',
  })
  .then(() => {
    console.log('Value sent to device');
  })
  .catch(() => {
    console.log('Error writing data to device');
  });
```
