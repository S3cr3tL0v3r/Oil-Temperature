//package de.henrikkaltenbach.oiltemperature.old;
//
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothManager;
//import android.content.*;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.provider.Settings;
//import android.util.Log;
//import androidx.annotation.Nullable;
//import android.os.Bundle;
//import androidx.core.app.ActivityCompat;
//
//public class MainActivity extends Activity {
//
//    private static final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
//    private static final int REQUEST_CODE_FOR_OVERLAY_SCREEN = 106;
//    private static final String OUTER_TAG = MainActivity.class.getSimpleName();
//
//    private GetFloatingIconClick receiver;
//    private final IntentFilter filter = new IntentFilter();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.v(OUTER_TAG, "1. Stufe: onCreate() betreten");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.v(OUTER_TAG, "2. Stufe: onStart() betreten");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.v(OUTER_TAG, "3. Stufe: onResume() betreten");
//        receiver = new GetFloatingIconClick();
//        filter.addAction(FloatingWidgetService.BROADCAST_ACTION);
//        registerReceiver(receiver, filter);
//        if (!getBluetoothAdapter().isEnabled()) {
//            promptEnableBluetooth();
//        } else {
//            startService();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.v(OUTER_TAG, "4. Stufe: onPause() betreten");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.v(OUTER_TAG, "5. Stufe: onStop() betreten");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.v(OUTER_TAG, "Wiederaufnahme: onRestart() betreten [nach Stufe 5, vor Stufe 2]");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.v(OUTER_TAG, "6. Stufe: onDestroy() betreten");
//        unregisterReceiver(receiver);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ENABLE_BLUETOOTH_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
//            promptEnableBluetooth();
//        }
//    }
//
//    private void startService() {
//        if (!hasRequiredPermissions()) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{
//                            Manifest.permission.BLUETOOTH,
//                            Manifest.permission.BLUETOOTH_ADMIN,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
//                    },
//                    PackageManager.PERMISSION_GRANTED);
//        }
//        if (Settings.canDrawOverlays(MainActivity.this)) {
//            Intent startIntent = new Intent(MainActivity.this, FloatingWidgetService.class);
//            startService(startIntent);
//        } else {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, REQUEST_CODE_FOR_OVERLAY_SCREEN);
//        }
//    }
//
//    private void promptEnableBluetooth() {
//        if (!getBluetoothAdapter().isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
//        }
//    }
//
//    private BluetoothAdapter getBluetoothAdapter() {
//        return ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
//    }
//
//    private void test() {
//
//    }
//
//    private boolean hasRequiredPermissions() {
//        return hasPermission(Manifest.permission.BLUETOOTH)
//                && hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
//                && hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
//    }
//
//    private boolean hasPermission(String permission) {
//        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private class GetFloatingIconClick extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Intent selfIntent = new Intent(MainActivity.this, MainActivity.class);
//            selfIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(selfIntent);
//        }
//    }
//}