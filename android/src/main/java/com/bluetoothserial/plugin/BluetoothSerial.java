package com.bluetoothserial.plugin;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.bluetoothserial.BluetoothDeviceHelper;
import com.bluetoothserial.BluetoothSerialService;
import com.bluetoothserial.KeyConstants;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@NativePlugin(
    permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    }
)
public class BluetoothSerial extends Plugin {
    private static final String ERROR_ADDRESS_MISSING = "Propriedade endereço do dispositivo é obrigatória.";
    private static final String ERROR_DEVICE_NOT_FOUND = "Dispositivo não encontrado.";
    private static final String ERROR_CONNECTION_FAILED = "Falha ao conectar ao dispositivo.";
    private static final String ERROR_DISCONNECT_FAILED = "Falha ao desconectar do dispositivo.";
    private static final String ERROR_WRITING= "Falha ao enviar dados ao dispositivo.";
    private static final String TAG = "BluetoothSerial";

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothSerialService service;

    private void unregisterReceiver(BroadcastReceiver receiver) {
        getContext().unregisterReceiver(receiver);
    }

    private void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getContext().registerReceiver(receiver, filter);
    }

    private void resolveDevices(Set<BluetoothDevice> devices) {
        PluginCall call = getSavedCall();

        JSObject response = new JSObject();
        JSArray devicesAsJson = BluetoothDeviceHelper.devicesToJSArray(devices);

        response.put("devices", devicesAsJson);
        call.resolve(response);

        saveCall(null);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        private Set<BluetoothDevice> devices = new HashSet<>();

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    resolveDevices(devices);
                    unregisterReceiver(this);
                    break;
            }
        }
    };

    @PluginMethod()
    public void isEnabled(PluginCall call) {
        if(hasNotBluetoothPermission()) {
            Log.e(TAG, "App does not have permission to access bluetooth");
            call.reject("Error verifying if bluetooth is enabled!");
            return;
        }

        boolean enabled = bluetoothAdapter.isEnabled();

        JSObject ret = new JSObject();
        ret.put(KeyConstants.ENABLED, enabled);
        call.resolve(ret);
    }

    @PluginMethod()
    public void scan(PluginCall call) {
      // TODO - se não tiver permissão, solicitar permissão
          /*if (cordova.hasPermission(ACCESS_COARSE_LOCATION)) {
               cordova.requestPermission(this, CHECK_PERMISSIONS_REQ_CODE, ACCESS_COARSE_LOCATION);
          }*/
        if(hasNotBluetoothPermission()) {
            Log.e(TAG, "App does not have permission to access bluetooth");

            call.reject("Error searching devices!");
            return;
        }

        try {
            saveCall(call);

            IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter filterFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            registerReceiver(receiver, filterFound);
            registerReceiver(receiver, filterFinished);

            bluetoothAdapter.startDiscovery();

            final BluetoothSerial serial = this;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              serial.stopScan();
            }
          }, 5000);
        } catch (Exception e) {
            saveCall(null);
            call.reject("Não foi possível buscar os dispositivos", e);
        }
    }

    private void stopScan() {
        bluetoothAdapter.cancelDiscovery();
    }

    @PluginMethod()
    public void connect(PluginCall call) {
        connect(call, true);
    }

    @PluginMethod()
    public void connectInsecure(PluginCall call) {
        connect(call, false);
    }

    private void connect(PluginCall call, boolean secure) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        // TODO - Check already connected

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if(device == null) {
            call.reject(ERROR_DEVICE_NOT_FOUND);
            return;
        }

        /* TODO - autoConnect
        Boolean autoConnect = call.getBoolean(keyAutoConnect);
        autoConnect = autoConnect == null ? false : autoConnect;
         */

        saveCall(call);
        getService().connect(device, secure, this);
    }

    public void connected() {
        PluginCall call = getSavedCall();
        if(call != null) {
            call.resolve();
            freeSavedCall();
        }
    }

    public void connectionFailed() {
        PluginCall call = getSavedCall();
        if(call != null) {
            call.reject(ERROR_CONNECTION_FAILED);
            freeSavedCall();
        }
    }

    @PluginMethod()
    public void disconnect(PluginCall call) {
        String address = getAddress(call);
        boolean success;
        if (address == null) {
            success = getService().disconnectAllDevices();
        } else {
            success = getService().disconnect(address);
        }

        if(success) {
            call.resolve();
        } else {
            call.reject(ERROR_DISCONNECT_FAILED);
        }
    }

    @PluginMethod()
    public void isConnected(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        boolean connected = getService().isConnected(address);
        JSObject response = new JSObject();
        response.put("connected", connected);

        call.resolve(response);
    }

    @PluginMethod()
    public void write(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        String value = call.getString(KeyConstants.VALUE);
        Log.i(TAG, value);

        boolean success = getService().write(address, BluetoothDeviceHelper.toByteArray(value));

        if(success) {
            call.resolve();
        } else {
            call.reject(ERROR_WRITING);
        }
    }

    @PluginMethod()
    public void read(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        try {
            String value = getService().read(address);

            JSObject response = new JSObject();
            response.put("value", value);

            call.resolve(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception during read", e);
            call.reject("Não foi possível ler dados do dispositivo", e);
        }
    }

    @PluginMethod()
    public void readUntil(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        String delimiter = getDelimiter(call);

        try {
            String value = getService().readUntil(address, delimiter);

            JSObject response = new JSObject();
            response.put("value", value);

            call.resolve(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception during readUntil", e);
            call.reject("Não foi possível ler dados do dispositivo", e);
        }
    }

    @PluginMethod()
    public void enableNotifications(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        String delimiter = getDelimiter(call);

        try {
            String eventName = getService().enableNotifications(address, delimiter);

            JSObject response = new JSObject();
            response.put("eventName", eventName);

            call.resolve(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception during enableNotifications", e);
            call.reject("Não foi possível habilitar as notificações", e);
        }
    }

    @PluginMethod()
    public void disableNotifications(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        try {
            getService().disableNotifications(address);

            call.resolve();
        } catch (IOException e) {
            Log.e(TAG, "Exception during disableNotifications", e);
            call.reject("Não foi possível desabilitar as notificações", e);
        }
    }

    public void notifyClient(String eventName, JSObject response) {
        notifyListeners(eventName, response);
    }

    // TODO - validar se é necessário confirmar todas as permissões
    private boolean hasNotBluetoothPermission() {
        return !hasBluetoothPermission();
    }

    private boolean hasBluetoothPermission() {
        return hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    protected void handleOnStart() {
        super.handleOnStart();
        initializeBluetoothAdapter();
        initializeService();
    }

    @Override
    protected void handleOnStop() {
        super.handleOnStop();
        unregisterReceiver(receiver);
        if(service != null) {
            getService().stopAll();
        }
    }

    private void initializeBluetoothAdapter() {
        bluetoothAdapter = getBluetoothManager().getAdapter();
    }

    private void initializeService() {
        if(service == null) {
            service = new BluetoothSerialService(this, bluetoothAdapter);
        }
    }

    private String getAddress(PluginCall call) {
        return getString(call, KeyConstants.ADDRESS_UUID);
    }

    private String getDelimiter(PluginCall call) {
        return getString(call, KeyConstants.DELIMITER);
    }

    private String getString(PluginCall call, String key) {
        return call.getString(key);
    }

    private BluetoothManager getBluetoothManager() {
        return (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private BluetoothSerialService getService() {
        if(service == null) {
            initializeService();
        }

        return service;
    }
}
