package com.bluetoothserial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BluetoothSerialService {
    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothSerialService";

    private BluetoothAdapter adapter;
    private Map<String, CommunicationThread> connections = new HashMap<>();
    private CommunicationThread connectedThread;


    public BluetoothSerialService(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean connect(BluetoothDevice device) {
        return connect(device, true);
    }

    public boolean connectInsecure(BluetoothDevice device) {
        return connect(device, false);
    }

    private boolean connect(BluetoothDevice device, boolean secure) {
        connectedThread = new CommunicationThread(device, secure);
        connectedThread.start();

        connections.put(device.getAddress(), connectedThread);

        return true;
    }
/*
    private class ConnectThread extends Thread {
        private  BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                if (secure) {
                    // tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                    tmp = device.createRfcommSocketToServiceRecord(UUID_SPP);
                } else {
                    //tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                    tmp = device.createInsecureRfcommSocketToServiceRecord(UUID_SPP);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                Log.i(TAG,"Connecting to socket...");
                mmSocket.connect();
                Log.i(TAG,"Connected");
            } catch (IOException e) {
                Log.e(TAG, e.toString());

                // Some 4.1 devices have problems, try an alternative way to connect
                // See https://github.com/don/BluetoothSerial/issues/89
                try {
                    Log.i(TAG,"Trying fallback...");
                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                    mmSocket.connect();
                    Log.i(TAG,"Connected");
                } catch (Exception e2) {
                    Log.e(TAG, "Couldn't establish a Bluetooth connection.");
                    try {
                        mmSocket.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e3);
                    }
                    connectionFailed();
                    return;
                }
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSerialService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }
*/
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

        CommunicationThread socket = connections.get(address);

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

        CommunicationThread socket = getCommunication(address);

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
       // try {
           // Log.d(TAG, out.toString());

        /*    BluetoothSocket socket = getSocket(address);

            if(socket == null) {
                Log.e(TAG, "No connection found");
                return false;
            }

            Log.d(TAG, "" + socket.isConnected());
*/

            CommunicationThread r;
            // Synchronize a copy of the ConnectedThread
            synchronized (this) {
                r = connectedThread;
            }
            // Perform the write unsynchronized
            r.write(out);
            //socket.getOutputStream().write(buffer);
      //  } catch (IOException e) {
      //      Log.e(TAG, "Exception during write", e);
       //     return false;
        //}

        return true;
    }

    public byte[] read(String address) throws IOException {
        CommunicationThread socket = getCommunication(address);

        if(socket == null) {
            Log.e(TAG, "No connection found");
            return new byte[0];
        }

        if(!socket.isConnected()) {
            // TODO - throw exception
        }

        byte[] buffer = new byte[1024];

        //int bytes = socket.getInputStream().read(buffer);
        //byte[] rawdata = Arrays.copyOf(buffer, bytes);

        //return rawdata;

        return new byte[0];
    }

    private CommunicationThread getCommunication(String address) {
        return connections.get(address);
    }

    private class CommunicationThread extends Thread {
        private BluetoothSocket socket = null;
        private final InputStream inStream;
        private final OutputStream outStream;

        public CommunicationThread(BluetoothDevice device, boolean secure) {
           // this.socket = socket;

            String socketType = secure ? "Secure" : "Insecure";

            adapter.cancelDiscovery();
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
                socket.getOutputStream().write("STATUS\n\n".getBytes());
                System.out.println("enviando comando");

                byte[] buffer = new byte[1024];
                int bytes = socket.getInputStream().read(buffer);
                String data = new String(buffer, 0, bytes);
                System.out.println("data");
                System.out.println(data);

               // connections.put(device.getAddress(), socket);
                Log.i(TAG, "Connection success - SocketType:" + socketType);

                Log.d(TAG, "END connect SocketType:" + socketType);

                //TODO - usar map de threads
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "create() failed", e);
            }


            inStream = getInputStream(socket);
            outStream = getOutputStream(socket);
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
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
/*
                try {
                    // Read from the InputStream
                    bytes = inStream.read(buffer);
                    String data = new String(buffer, 0, bytes);
                    System.out.println(data);


                    // Send the new data String to the UI Activity
 //                   mHandler.obtainMessage(BluetoothSerial.MESSAGE_READ, data).sendToTarget();

                    // Send the raw bytestream to the UI Activity.
                    // We make a copy because the full array can have extra data at the end
                    // when / if we read less than its size.
                   /* if (bytes > 0) {
                        byte[] rawdata = Arrays.copyOf(buffer, bytes);
    //                    mHandler.obtainMessage(BluetoothSerial.MESSAGE_READ_RAW, rawdata).sendToTarget();
                    }


                    */
               /* } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
     //               connectionLost();
                    // Start the service over to restart listening mode
      //              BluetoothSerialService.this.start();
                    System.out.println(BluetoothSerialService.this);
                    break;
                }
*/

            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                System.out.println("teste");
              //  outStream.flush();
                //outStream.write("STATUS\n\n".getBytes(Charset.forName("UTF-8")));
                outStream.write(buffer);
                System.out.println("aaaaa");

                // Share the sent message back to the UI Activity
     //           mHandler.obtainMessage(BluetoothSerial.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

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
