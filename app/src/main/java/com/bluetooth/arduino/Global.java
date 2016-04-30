package com.bluetooth.arduino;

        import android.app.Application;
        import android.bluetooth.BluetoothDevice;
        import android.os.Handler;
        import android.os.Message;
        import android.support.design.BuildConfig;
        import android.util.Log;
        import android.widget.Toast;
        import java.util.ArrayList;

public class Global extends Application {
    public static final boolean D = true;
    public static final String DEVICE_NAME = "device_name";
    public static final int MESSAGE_DISCONNECTED= 6;
    public static final int Message_Written = 3;
    public static final int Message_STATE_CHANGED = 1;
    public static final int Message_allowed = 2;
    public static final int Message_devname = 4;
    public static final int Message_TOAST = 5;
    public static final int NOT_CONNECTED = 8;
    public static final int REQUEST_ENABLE_BT = 7;
    public static final String TAG = "ArduinoBluetooth";
    public static final String TOAST = "toast";
    private BtConnection BtConnection;
    ArrayList aL;
    private BluetoothListener btListener;
    String inString;
    public String incomingValue;
    private String mConnectedDeviceName;
    final Handler mHandler;
    public boolean ratingPopTemDisable;
    int readNo;
    StringBuilder sb;
    Boolean startAppend;
    public boolean toastNotConnected;
    public boolean xmlLoaded;
    static Global global;
    public interface BluetoothListener {
        void bluetoothRead(int i);
    }
    public static Global get(){
        return global;
    }
    public Global() {
        global=this;
        this.mConnectedDeviceName = null;
        this.BtConnection = null;
        this.toastNotConnected = false;
        this.xmlLoaded = false;
        this.ratingPopTemDisable = false;
        this.startAppend = Boolean.valueOf(false);
        this.inString = BuildConfig.FLAVOR;
        this.sb = new StringBuilder();
        this.readNo = Message_STATE_CHANGED;
        this.aL = new ArrayList();
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Global.Message_allowed /*2*/:
                        String strIncom = new String((byte[]) msg.obj, 0, msg.arg1);
                        int endOfLineIndex = strIncom.indexOf("\r\n");
                        Log.e("value", strIncom);
                        Global.this.incomingValue = strIncom;
                        Global.this.btListener.bluetoothRead(Global.Message_allowed);
                        strIncom.replaceAll("\\p{C}", "?");
                        if (strIncom.equals("0")) {
                        }
                        if (strIncom.equals("1")) {
                        }
                        if (endOfLineIndex != -1) {
                        }
                    case Global.Message_Written /*3*/:
                        Log.e(Global.TAG, "Message_write  =w= " + new String((byte[]) msg.obj));
                    case Global.Message_devname /*4*/:
                        Global.this.mConnectedDeviceName = msg.getData().getString(Global.DEVICE_NAME);
                        Global.this.toastNotConnected = false;
                        Toast toast = Toast.makeText(Global.this.getApplicationContext(), "Connected with " + Global.this.mConnectedDeviceName, Toast.LENGTH_SHORT);
                        toast.getView().setAlpha(0.7f);
                        toast.show();
                        if (Global.this.btListener != null) {
                            Global.this.btListener.bluetoothRead(Global.Message_devname);
                        } else {
                            Log.e("bluetoothRead", "btListener null");
                        }
                        Log.d(Global.TAG, "call home page");
                    case Global.Message_TOAST /*5*/:
                        if (Global.this.btListener != null) Global.this.btListener.bluetoothRead(Global.Message_TOAST);
                    case Global.MESSAGE_DISCONNECTED /*6*/:
                        if (Global.this.btListener != null) {
                            Global.this.btListener.bluetoothRead(Global.MESSAGE_DISCONNECTED);
                        } else {
                            Log.e("bluetoothRead", "btListener null");
                        }
                        Log.e("Conexion", "Disconnected");
                        Toast toast1 = Toast.makeText(Global.this.getApplicationContext(), "disconnected", Toast.LENGTH_SHORT);
                        toast1.getView().setAlpha(0.7f);
                        toast1.show();
                    default:
                }
            }
        };
    }

    public void onCreate() {
        super.onCreate();
        if (this.BtConnection == null) {
            this.BtConnection = new BtConnection(getApplicationContext(), this.mHandler);
        }
    }

    public void btConnct(BluetoothDevice device) {
        if (this.BtConnection != null) {
            this.BtConnection.connect(device);
        }
    }

    public void btDisconnect() {
        if (this.BtConnection != null) {
            this.BtConnection.stop();
        }
    }

    public void sendMessage(String message) {
        int state = this.BtConnection.getState();
        if (state != Message_Written) {
            Toast toast = Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT);
            toast.getView().setAlpha(0.7f);
            if (!this.toastNotConnected) {
                this.toastNotConnected = D;
                toast.show();
            }
            this.btListener.bluetoothRead(NOT_CONNECTED);
        } else if (message.length() > 0) {
            byte[] send = message.getBytes();
            Log.e(TAG, "Message enviado:" + message);
            this.BtConnection.write(send);
        }
    }

    public void setBtReader(BluetoothListener btListener) {
        this.btListener = btListener;
    }

}
