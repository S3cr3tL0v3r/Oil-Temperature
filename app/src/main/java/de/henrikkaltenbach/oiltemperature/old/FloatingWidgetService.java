//package de.henrikkaltenbach.oiltemperature.old;
//
//import android.app.Service;
//import android.bluetooth.*;
//import android.bluetooth.le.*;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.PixelFormat;
//import android.os.*;
//import android.util.Log;
//import android.view.*;
//import android.widget.TextView;
//import android.widget.Toast;
//import de.henrikkaltenbach.oiltemperature.R;
//
//import java.util.Collections;
//import java.util.UUID;
//
//public class FloatingWidgetService extends Service {
//
//    public static final String BROADCAST_ACTION = "floating widget";
//
//    private static final String OUTER_TAG = FloatingWidgetService.class.getSimpleName();
//    private static final int MAX_CLICK_DURATION = 200;
//    private static final long SCAN_PERIOD = 10000;
//    private static final String ENVIRONMENTAL_SENSING_SERVICE_UUID = "0000181A-0000-1000-8000-00805F9B34FB";
//    private static final String TEMPERATURE_CHARACTERISTIC_UUID = "00002A6E-0000-1000-8000-00805F9B34FB";
//    private static final String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805F9B34FB";
//    private static final String DEVICE_NAME = "Henrik's ESP32";
//
//    private boolean isScanning;
//    private long startClickTime;
//    private Context context;
//    private WindowManager windowManager;
//    private View floatingWidget;
//    private TextView tvOilTemperature;
//    private BluetoothLeScanner bleScanner;
//    private ScanFilter scanFilter;
//    private ScanSettings scanSettings;
//    private Handler handler;
//    private BluetoothGatt bleGatt;
//
//    public FloatingWidgetService() {
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.v(OUTER_TAG, "1. Stufe: onCreate() betreten");
//        floatingWidget = LayoutInflater.from(this).inflate(R.layout.service_float_widget, null);
//        context = this;
//        scanFilter = buildScanFilter();
//        scanSettings = buildScanSettings();
//        handler = new Handler(Looper.getMainLooper());
//        final WindowManager.LayoutParams params = createLayoutParams();
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        windowManager.addView(floatingWidget, params);
//        floatingWidget.findViewById(R.id.root_container).setOnTouchListener(getFloatingWidgetOnTouchListener(params));
//        tvOilTemperature = floatingWidget.findViewById(R.id.tvOilTemperature);
//        onResume();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.v(OUTER_TAG, "onStartCommand() betreten");
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
//        // If we get killed, after returning from here, restart
//        return START_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // We don't provide binding, so return null
//        Log.v(OUTER_TAG, "onBind() betreten");
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.v(OUTER_TAG, "6. Stufe: onDestroy() betreten");
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
//        if (floatingWidget != null) windowManager.removeView(floatingWidget);
//    }
//
//    private void onResume() {
//        if (getBluetoothAdapter().isEnabled()) {
//            bleScanner = getBluetoothAdapter().getBluetoothLeScanner();
//            startBleScan();
//        }
//    }
//
//    private final ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            bleScanner.stopScan(scanCallback);
//            result.getDevice().connectGatt(context, false, gattCallback);
//            BluetoothDevice bleDevice = result.getDevice();
//            Log.i( "ScanCallback", String.format("Connect to %s (%s)", bleDevice.getName(), bleDevice.getAddress()));
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Log.e("ScanCallback", "onScanFailed: code " + errorCode);
//        }
//    };
//
//    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            BluetoothDevice bleDevice = gatt.getDevice();
//            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
//                bleGatt = gatt;
//                Log.i("BluetoothGattCallback", String.format("Successfully connected to %s (%s)", bleDevice.getName(), bleDevice.getAddress()));
//                handler.post(() -> bleGatt.discoverServices());
//                Log.i("BluetoothGattCallback", String.format("Discover services on %s (%s)", bleDevice.getName(), bleDevice.getAddress()));
//            } else  {
//                gatt.close();
//                handler.post(() -> tvOilTemperature.setText(R.string.n_a));
//                startBleScan();
//                Log.e("BluetoothGattCallback",  String.format("Successfully disconnected from or connection error with %s (%s)", bleDevice.getName(), bleDevice.getAddress()));
//            }
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            bleGatt = gatt;
//            BluetoothGattCharacteristic characteristic = bleGatt
//                    .getService(UUID.fromString(ENVIRONMENTAL_SENSING_SERVICE_UUID))
//                    .getCharacteristic(UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID));
//            UUID cccdUUID = UUID.fromString(CCC_DESCRIPTOR_UUID);
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(cccdUUID);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            if (bleGatt.setCharacteristicNotification(characteristic, true)) {
//                bleGatt.writeDescriptor(descriptor);
//                Log.i("BluetoothGattCallback", "Successfully subscribed to characteristic notifications");
//            } else {
//                Log.e("BluetoothGattCallback", "Could not subscribe to characteristic notifications");
//            }
//        }
//
//        @Override
//        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            byte value = characteristic.getValue()[0];
//            final String oilTemperature = String.valueOf(value);
//            handler.post(() -> tvOilTemperature.setText(oilTemperature));
//            Log.i("BluetoothGattCallback", "Characteristic value changed. Oil temperature: " + oilTemperature);
//        }
//    };
//
//    private BluetoothAdapter getBluetoothAdapter() {
//        return ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
//    }
//
//    private ScanFilter buildScanFilter() {
//        return new ScanFilter.Builder()
//                .setDeviceName(DEVICE_NAME)
//                .setServiceUuid(ParcelUuid.fromString(ENVIRONMENTAL_SENSING_SERVICE_UUID))
//                .build();
//    }
//
//    private ScanSettings buildScanSettings() {
//        return new ScanSettings.Builder()
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
//                .build();
//    }
//
//    private void startBleScan() {
//        if (!isScanning) {
//            bleScanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
//            isScanning = true;
//            handler.postDelayed(this::stopBleScan, SCAN_PERIOD);
//            Log.i("BluetoothLeScanner", "Scan started");
//        }
//    }
//
//    private void stopBleScan() {
//        if (isScanning) {
//            bleScanner.stopScan(scanCallback);
//            isScanning = false;
//            Log.i("BluetoothLeScanner", "Scan stopped");
//        }
//    }
//
//    private WindowManager.LayoutParams createLayoutParams() {
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
//                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//                        : WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.TOP | Gravity.START;
//        params.x = 0;
//        params.y = 100;
//        return params;
//    }
//
//    private View.OnTouchListener getFloatingWidgetOnTouchListener(WindowManager.LayoutParams params) {
//        return new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        startClickTime = System.currentTimeMillis();
//                        return false;
//                    case MotionEvent.ACTION_UP:
//                        int dX = (int) (event.getRawX() - initialTouchX);
//                        int dY = (int) (event.getRawY() - initialTouchY);
//                        long clickDuration = System.currentTimeMillis() - startClickTime;
//                        if (clickDuration < MAX_CLICK_DURATION) {
//                            Intent intent = new Intent(BROADCAST_ACTION);
//                            sendBroadcast(intent);
//                            stopSelf();
//                        }
//                        return false;
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        windowManager.updateViewLayout(floatingWidget, params);
//                        return false;
//                }
//                return false;
//            }
//        };
//    }
//}