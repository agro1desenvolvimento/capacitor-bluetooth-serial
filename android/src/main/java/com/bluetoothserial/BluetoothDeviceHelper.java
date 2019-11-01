package com.bluetoothserial;

import android.bluetooth.BluetoothDevice;
import android.util.Base64;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class BluetoothDeviceHelper implements Serializable {

    public static JSArray devicesToJSArray(Set<BluetoothDevice> devices) {
        JSArray devicesAsJson = new JSArray();

        for (BluetoothDevice device : devices) {
            devicesAsJson.put(deviceToJSObject(device));
        }

        return devicesAsJson;
    }

    public static JSObject deviceToJSObject(BluetoothDevice device) {
        JSObject json = new JSObject();
        json.put("name", device.getName());
        json.put("address", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    public static byte[] toByteArray(String value) {
        if (value == null) {
            return new byte[0];
        }

        return value.getBytes(StandardCharsets.UTF_8);
        //return Base64.decode(encoded, Base64.DEFAULT);
    }

}
