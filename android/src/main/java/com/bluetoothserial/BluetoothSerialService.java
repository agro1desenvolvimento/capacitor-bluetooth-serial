package com.bluetoothserial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bluetoothserial.plugin.BluetoothSerial;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BluetoothSerialService {
    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothSerialService";
    private static final String SUBSCRIBE_EVENT = "subscribe";

    private BluetoothAdapter adapter;
    private BluetoothSerial plugin;
    private Map<String, BluetoothConnection> connections = new HashMap<>();

    public BluetoothSerialService(BluetoothSerial plugin, BluetoothAdapter adapter) {
        this.plugin = plugin;
        this.adapter = adapter;
    }

    public boolean connect(BluetoothDevice device) {
        return connect(device, true);
    }

    // TODO
    public boolean connectInsecure(BluetoothDevice device) {
        return connect(device, false);
    }

    private boolean connect(BluetoothDevice device, boolean secure) {
        BluetoothConnection connectedThread = new BluetoothConnection(device, secure);
        connectedThread.start();

        connections.put(device.getAddress(), connectedThread);

        return true;
    }

    public boolean disconnectAllDevices() {
        boolean success = true;
        for(String address : connections.keySet()) {
            success = success & disconnect(address);
        }

        return success;
    }

    public boolean disconnect(BluetoothDevice device) {
        String address = device.getAddress();
        return disconnect(address);
    }

    public boolean disconnect(String address) {
        Log.d(TAG, "BEGIN disconnect device " + address);

        BluetoothConnection socket = getConnection(address);

        if(socket == null) {
            Log.e(TAG, "No connection found");
            return true;
        }

        if(!socket.isConnected()) {
            Log.i(TAG, "Device is already disconnected");
        } else {
            return socket.disconnect();
        }

        connections.remove(address);
        Log.d(TAG, "END disconnect device " + address);

        return true;
    }

    public boolean isConnected(String address) {
        Log.d(TAG, "BEGIN isConnected device " + address);

        BluetoothConnection socket = getConnection(address);

        if(socket == null) {
            Log.e(TAG, "No connection found");
            return false;
        }

        return socket.isConnected();
    }

    /**
     * Write to the connected Device via socket.
     *
     * @param address The device address to send
     * @param out  The bytes to write
     */
    public boolean write(String address, byte[] out) {

        BluetoothConnection r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            r = getConnection(address);
        }

        if(r == null || !r.isConnected()) {
            return false;
        }

        // Perform the write unsynchronized
        r.write(out);

        return true;
    }

    public String read(String address) throws IOException {
        BluetoothConnection connection = getConnection(address);

        // TODO - criar exception customizada
        if(connection == null) {
            Log.e(TAG, "No connection found");
            throw new IOException("No connection found");
        }

        if(!connection.isConnected()) {
            Log.e(TAG, "Not connected");

            throw new IOException("Not connected");
        }

        return connection.read();
    }

    public String readUntil(String address, String delimiter) throws IOException {
        BluetoothConnection connection = getConnection(address);

        if(connection == null) {
            Log.e(TAG, "No connection found");
            throw new IOException("No connection found");
        }

        if(!connection.isConnected()) {
            Log.e(TAG, "Not connected");

            throw new IOException("Not connected");
        }

        return connection.readUntil(delimiter);
    }

    public String enableNotifications(String address, String delimiter) throws IOException {
        BluetoothConnection connection = getConnection(address);

        if(connection == null) {
            Log.e(TAG, "No connection found");
            throw new IOException("No connection found");
        }

        if(!connection.isConnected()) {
            Log.e(TAG, "Not connected");

            throw new IOException("Not connected");
        }

        return connection.enableNotifications(delimiter);
    }

    public void disableNotifications(String address) throws IOException {
        BluetoothConnection connection = getConnection(address);

        if(connection == null) {
            Log.e(TAG, "No connection found");
            throw new IOException("No connection found");
        }

        if(!connection.isConnected()) {
            Log.e(TAG, "Not connected");

            throw new IOException("Not connected");
        }

        connection.disableNotifications();
    }

    private BluetoothConnection getConnection(String address) {
        return connections.get(address);
    }

    private class BluetoothConnection extends Thread {
        private BluetoothSocket socket = null;
        private final InputStream inStream;
        private final OutputStream outStream;
        private StringBuffer buffer;
        private boolean enabledNotifications;
        private boolean enabledRawNotifications;
        private String subscribeDelimiter;

        public BluetoothConnection(BluetoothDevice device, boolean secure) {
            adapter.cancelDiscovery();

            createRfcomm(device, secure);

            inStream = getInputStream(socket);
            outStream = getOutputStream(socket);
            buffer = new StringBuffer();
            this.enabledNotifications = false;
            this.enabledRawNotifications = false;
        }

        private void createRfcomm(BluetoothDevice device, boolean secure) {
            String socketType = secure ? "Secure" : "Insecure";
            Log.d(TAG, "BEGIN create socket SocketType:" + socketType);

            try {
                if(secure) {
                    socket = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
                } else {
                    socket = device.createInsecureRfcommSocketToServiceRecord(DEFAULT_UUID);
                }

                Log.d(TAG, "END create socket SocketType:" + socketType);
                Log.d(TAG, "BEGIN connect SocketType:" + socketType);

                socket.connect();

                Log.i(TAG, "Connection success - SocketType:" + socketType);

                Log.d(TAG, "END connect SocketType:" + socketType);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "create() failed", e);
            }
        }

        private InputStream getInputStream(BluetoothSocket socket) {
            try {
                return socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Erro ao obter inputStream", e);
            }

            return null;
        }

        private OutputStream getOutputStream(BluetoothSocket socket) {
            try {
                return socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Erro ao obter outputStream", e);
            }

            return null;
        }

        public void run() {
            Log.i(TAG, "BEGIN connectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {

                try {
                    // Read from the InputStream
                    bytes = inStream.read(buffer);
                    String data = new String(buffer, 0, bytes);

                    this.buffer.append(data);

                    if (areNotificationsEnabled()) {
                        notifySubscribers(buffer);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);

                    // TODO - se perder conexao, tentar reconectar
     //               connectionLost();
                    // Start the service over to restart listening mode
      //              BluetoothSerialService.this.start();
                    //System.out.println(BluetoothSerialService.this);
                    break;
                }

            }
        }

        public boolean areNotificationsEnabled() {
            return this.enabledNotifications || this.enabledRawNotifications;
        }

        public void notifySubscribers(byte[] bytes) {
            if (this.enabledNotifications) {
                while (buffer.indexOf(this.subscribeDelimiter) >= 0) {
                    String data = readUntil(this.subscribeDelimiter);
                    JSObject ret = new JSObject();
                    ret.put("data", data);

                    plugin.notifyClient(SUBSCRIBE_EVENT, ret);
                }
            }
            if (this.enabledRawNotifications) {
                // TODO - implementar
                // enviar bytes
            }
        }

        public synchronized String read() {
            String data;
            synchronized (buffer) {
                int index = buffer.length();

                data = buffer.substring(0, index);
                buffer.delete(0, index);
            }

            return data;
        }

        public synchronized String readUntil(String delimiter) {
            String data = "";
            synchronized (buffer) {
                int index = buffer.indexOf(delimiter);

                if (index >= 0) {
                    index += delimiter.length();
                    data = buffer.substring(0, index);
                    buffer.delete(0, index+1);
                }
            }

            return data;
        }

        public synchronized String enableNotifications(String delimiter) {
            enabledNotifications = true;
            this.subscribeDelimiter = delimiter;

            return SUBSCRIBE_EVENT;
        }

        public synchronized void disableNotifications() {
            enabledNotifications = false;
        }


        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                outStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public boolean disconnect() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
                return false;
            }

            return true;
        }

        public boolean isConnected() {
            return socket.isConnected();
        }
    }
}
