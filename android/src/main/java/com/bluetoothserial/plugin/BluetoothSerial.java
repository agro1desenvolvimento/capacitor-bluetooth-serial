package com.bluetoothserial.plugin;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
        },
        requestCodes = {
          BluetoothSerial.REQUEST_ENABLE_BT
        }
)
public class BluetoothSerial extends Plugin {

    static final int REQUEST_ENABLE_BT = 1245;

    private static final String ERROR_ADDRESS_MISSING = "Propriedade endereço do dispositivo é obrigatória.";
    private static final String ERROR_DEVICE_NOT_FOUND = "Dispositivo não encontrado.";
    private static final String ERROR_CONNECTION_FAILED = "Falha ao conectar ao dispositivo.";
    private static final String ERROR_DISCONNECT_FAILED = "Falha ao desconectar do dispositivo.";
    private static final String ERROR_WRITING= "Falha ao enviar dados ao dispositivo.";

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

        resolveCall(call, response);

        freeSavedCall();
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
        boolean enabled = isEnabled();

        resolveEnableBluetooth(call, enabled);
    }

    @PluginMethod()
    public void enable(PluginCall call) {
      if (!hasRequiredPermissions()) {
        saveCall(call);
        pluginRequestAllPermissions();
      } else {
        enableBluetooth(call);
      }
    }

    @PluginMethod()
    public void scan(PluginCall call) {
        if (rejectIfDisabled(call)) {
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
            Log.e(getLogTag(), "Error searching devices", e);
            call.reject("Não foi possível buscar os dispositivos", e);
            freeSavedCall();
        }
    }

    private void stopScan() {
        bluetoothAdapter.cancelDiscovery();
    }

    @PluginMethod()
    public void connect(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        if (rejectIfDisabled(call)) {
          return;
        }

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
        getService().connect(device, this);
    }

    public void connected() {
        PluginCall call = getSavedCall();
        if(call != null) {
            resolveCall(call);
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
            resolveCall(call);
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

        resolveCall(call, response);
    }

    @PluginMethod()
    public void write(PluginCall call) {
        String address = getAddress(call);

        if (address == null) {
            call.reject(ERROR_ADDRESS_MISSING);
            return;
        }

        String value = call.getString(KeyConstants.VALUE);
        Log.i(getLogTag(), value);

        boolean success = getService().write(address, BluetoothDeviceHelper.toByteArray(value));

        if(success) {
            resolveCall(call);
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

            resolveCall(call, response);
        } catch (IOException e) {
            Log.e(getLogTag(), "Exception during read", e);
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

            resolveCall(call, response);
        } catch (IOException e) {
            Log.e(getLogTag(), "Exception during readUntil", e);
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

            resolveCall(call, response);
        } catch (IOException e) {
            Log.e(getLogTag(), "Exception during enableNotifications", e);
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

            resolveCall(call);
        } catch (IOException e) {
            Log.e(getLogTag(), "Exception during disableNotifications", e);
            call.reject("Não foi possível desabilitar as notificações", e);
        }
    }

    public void notifyClient(String eventName, JSObject response) {
        notifyListeners(eventName, response);
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

        if(service != null) {
            getService().stopAll();
        }
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(getLogTag(), "handling request perms result");
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.d(getLogTag(), "No stored plugin call for permissions request result");
            return;
        }

        for(int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Log.e(getLogTag(), "User denied permission");
                savedCall.error("Permissão negada");

                freeSavedCall();
                return;
            }
        }

        Log.d(getLogTag(), "Permissions acquired");

        enableBluetooth(savedCall);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
      super.handleOnActivityResult(requestCode, resultCode, data);

      Log.i(getLogTag(), "Handler called with " + resultCode);

      if (requestCode == REQUEST_ENABLE_BT) {
        PluginCall call = getSavedCall();

        if (call == null) {
          return;
        }

        resolveEnableBluetooth(call, resultCode == Activity.RESULT_OK);
        freeSavedCall();
      }
    }

    private void enableBluetooth(PluginCall call) {
      if(!hasRequiredPermissions()) {
        resolveEnableBluetooth(call, false);
        return;
      }
      if (isEnabled()) {
        resolveEnableBluetooth(call, true);
        return;
      }

      Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(call, enableIntent, REQUEST_ENABLE_BT);
    }

  private void resolveEnableBluetooth(PluginCall call, boolean enabled) {
    JSObject ret = new JSObject();
    ret.put(KeyConstants.ENABLED, enabled);

    resolveCall(call, ret);
  }

    private void resolveCall(PluginCall call, JSObject ret) {
      call.resolve(ret);
      call.release(getBridge());
    }

    private void resolveCall(PluginCall call) {
      call.resolve();
      releaseBridge(call);
    }

    private void releaseBridge(PluginCall call) {
      if (call != null && !call.isReleased()) {
        call.release(getBridge());
      }
    }

    private boolean rejectIfDisabled(PluginCall call) {
      if (!hasRequiredPermissions()) {
        Log.e(getLogTag(), "App does not have permission to access bluetooth");

        call.reject("Permissão negada para acesso ao bluetooth");
        return true;
      }

      if (isDisabled()) {
        Log.e(getLogTag(), "Bluetooth is disabled");

        call.reject("Bluetooth está desabilitado");
        return true;
      }

      return false;
    }

    private boolean isDisabled() {
      return !isEnabled();
    }

    private boolean isEnabled() {
      return hasRequiredPermissions() && bluetoothAdapter.isEnabled();
    }

    private void initializeBluetoothAdapter() {
        bluetoothAdapter = getBluetoothManager().getAdapter();
    }

    private void initializeService
            () {
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
