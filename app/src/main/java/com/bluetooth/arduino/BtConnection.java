package com.bluetooth.arduino;

/**
 * Created by arpitkh996 on 12-03-2016.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BtConnection {
    private static final boolean D = true;
    private static final UUID MY_UUID;
    private static final String NAME = "BluetoothDEB";
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;
    private static final String TAG = "Servicio_Bluetooth";
    private final BluetoothAdapter AdaptadorBT;
    private int EstadoActual;
    private AcceptThread acceptThread;
    private ConnectedThread connectedThread;
    private ConnectThread connectThread;
    Context context1;
    Global global;
    private final Handler mHandler;

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp =  BtConnection.this.AdaptadorBT.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {

                    BtConnection.this.connected(socket, socket.getRemoteDevice());
                    // Do work to manage the connection (in a separate thread)
                    //            manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(BtConnection.MY_UUID);
            } catch (IOException e) {
                Log.e(BtConnection.TAG, "create() Fallo", e);
            }
            this.mmSocket = tmp;
        }

        public void run() {
            Log.e(BtConnection.TAG, "Comenzando HebraConectada");
            setName("HiloConectado");
            BtConnection.this.AdaptadorBT.cancelDiscovery();
            try {
                Log.e(BtConnection.TAG, "mmSocket.connect()");
                this.mmSocket.connect();
                synchronized (BtConnection.this) {
                    BtConnection.this.connectThread = null;
                }
                BtConnection.this.connected(this.mmSocket, this.mmDevice);
            } catch (IOException e) {
                BtConnection.this.connectionFailed();
                try {
                    this.mmSocket.close();
                } catch (IOException e2) {
                    Log.e(BtConnection.TAG, "Imposible cerrar el socket durante la falla de conexion", e2);
                }
                BtConnection.this.start();
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(BtConnection.TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket BTSocket;
        private final InputStream INPUT_Stream;
        private final OutputStream OUTPUT_Stream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(BtConnection.TAG, "Creacion de HiloConectado");
            this.BTSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(BtConnection.TAG, "Sockets temporales No creados", e);
            }
            this.INPUT_Stream = tmpIn;
            this.OUTPUT_Stream = tmpOut;
        }

        public void write(byte[] buffer) {
            try {
                this.OUTPUT_Stream.write(buffer);
                Handler access$500 = BtConnection.this.mHandler;
                access$500.obtainMessage(BtConnection.STATE_CONNECTED, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(BtConnection.TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                this.BTSocket.close();
            } catch (IOException e) {
                Log.e(BtConnection.TAG, "close() del socket conectado Fallo", e);
            }
        }

        public void run() {
            Log.e(BtConnection.TAG, "Comenzar Hebraconectada");
            byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            while (true) {
                try {
                    int bytes = this.INPUT_Stream.read(buffer);
                    Handler access$500 = BtConnection.this.mHandler;
                    access$500.obtainMessage(BtConnection.STATE_CONNECTING, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(BtConnection.TAG, "disconnected", e);
                    BtConnection.this.connectionLost();
                    return;
                }
            }
        }
    }

    static {
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }

    public BtConnection(Context context, Handler handler) {
        this.global = (Global) context;
        this.AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
        this.EstadoActual = STATE_NONE;
        this.mHandler = handler;
        this.context1 = context;
    }

    private synchronized void setState(int estado) {
        this.EstadoActual = estado;
        Handler handler = this.mHandler;
        handler.obtainMessage(STATE_LISTEN, estado, -1).sendToTarget();
    }

    public synchronized int getState() {
        return this.EstadoActual;
    }

    public synchronized void start() {
        Log.e(TAG, "start");
        if (this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }
        if (this.connectedThread != null) {
            this.connectedThread.cancel();
            this.connectedThread = null;
        }
        if (this.acceptThread == null) {
            this.acceptThread = new AcceptThread();
            this.acceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.e(TAG, "Conectado con: " + device);
        if (this.EstadoActual == STATE_CONNECTING && this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }
        if (this.connectedThread != null) {
            this.connectedThread.cancel();
            this.connectedThread = null;
        }
        this.connectThread = new ConnectThread(device);
        this.connectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.e(TAG, "connected");
        if (this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }
        if (this.connectedThread != null) {
            this.connectedThread.cancel();
            this.connectedThread = null;
        }
        if (this.acceptThread != null) {
            this.acceptThread.cancel();
            this.acceptThread = null;
        }
        this.connectedThread = new ConnectedThread(socket);
        this.connectedThread.start();
        Handler handler = this.mHandler;
        Message msg = handler.obtainMessage(4);
        Bundle bundle = new Bundle();
        bundle.putString(Global.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.e(TAG, "stop");
        if (this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }
        if (this.connectedThread != null) {
            this.connectedThread.cancel();
            this.connectedThread = null;
        }
        if (this.acceptThread != null) {
            this.acceptThread.cancel();
            this.acceptThread = null;
        }
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        synchronized (this) {
            if (this.EstadoActual != STATE_CONNECTED) {
                return;
            }
            ConnectedThread r = this.connectedThread;
            r.write(out);
        }
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);
        Handler handler = this.mHandler;
        Message msg = handler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString(Global.TOAST, "Connection error");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        setState(STATE_LISTEN);
        Handler handler = this.mHandler;
        Message msg = handler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString(Global.TOAST, "Lost connection");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
        handler = this.mHandler;
        this.mHandler.sendMessage(handler.obtainMessage(6));
    }
}

