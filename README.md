# Capacitor Bluetooth Serial Plugin

A client implementation for interacting with Bluetooth

Supported platforms

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
import com.bluetoothserial.plugin.BluetoothLEClient;

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

# TODO - VALIDAR PROMISESs

# API

## Methods

- [bluetoothSerial.isEnabled](#isenabled)

## isEnabled

Reports if bluetooth is enabled.

  `isEnabled(): Promise<BluetoothEnabledResult>;`

### Description

Function `isEnabled` calls the success whatever bluetooth is enabled or disabled. The promise will contain an attribute `enabled` indicating if bluetooth is enabled or *not* enabled. The failure callback will be called only if an error occurs (e.g. app does not have permission to access bluetooth).

### Parameters

None.

### Quick Example

    bluetoothSerial
      .isEnabled()
      .then((response) => {
        const message = response.enabled ? 'enabled' : 'disabled';
        console.log(`Bluetooth is ${message}`);
      })
      .catch((error) => {
        console.log('Error checking bluetooth status');
      });
    );