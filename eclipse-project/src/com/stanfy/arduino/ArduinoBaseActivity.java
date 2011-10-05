package com.stanfy.arduino;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import com.stanfy.arduino.util.Utils;

public abstract class ArduinoBaseActivity extends Activity implements Runnable {

  /** Message codes. */
  public static final int MESSAGE_DISPLAY = 1;

  private static final String TAG = "ArduinoActivity";
  private static final String ACTION_USB_PERMISSION = "com.stanfy.arduino.action.USB_PERMISSION";

  private UsbManager usbManager;
  private PendingIntent pendingIntent;
  private boolean permissionEnabled;

  UsbAccessory usbAccessory;

  FileInputStream inputStream;
  FileOutputStream outputStream;
  ParcelFileDescriptor fileDescriptor;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    usbManager = UsbManager.getInstance(getApplicationContext());
    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
        new Intent(ACTION_USB_PERMISSION), 0);
    final IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
    intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
    registerReceiver(usbReceiver, intentFilter);

    if (getLastNonConfigurationInstance() != null) {
      usbAccessory = (UsbAccessory) getLastNonConfigurationInstance();
      openAccessory(usbAccessory);
      Log.i(TAG, "accessoty opened !!!");
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    if (usbAccessory != null) {
      return usbAccessory;
    } else {
      return super.onRetainNonConfigurationInstance();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (inputStream != null && outputStream != null) { return; }

    final UsbAccessory[] accessories = usbManager.getAccessoryList();
    final UsbAccessory accessory = (accessories == null ? null : accessories[0]);
    if (accessory != null) {
      if (usbManager.hasPermission(accessory)) {
        openAccessory(accessory);
      } else {
        synchronized (usbReceiver) {
          if (!permissionEnabled) {
            usbManager.requestPermission(accessory, pendingIntent);
            permissionEnabled = true;
          }
        }
      }
    } else {
      Log.w(TAG, "Accessory not found");
    }
  }

  private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(final Context context, final Intent intent) {
      final String action = intent.getAction();
      if (ACTION_USB_PERMISSION.equals(action)) {
        synchronized (this) {
          final UsbAccessory accessory = UsbManager.getAccessory(intent);
          if (intent
              .getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
            openAccessory(accessory);
          } else {
            Log.i(TAG, "Permission for accessory " + accessory + "- denied");
          }
          permissionEnabled = false;
        }
      } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
        final UsbAccessory accessory = UsbManager.getAccessory(intent);
        if (accessory != null && accessory.equals(usbAccessory)) {
          closeAccessory();
        }
      }
    }
  };

  private void openAccessory(final UsbAccessory accessory) {
    fileDescriptor = usbManager.openAccessory(accessory);
    if (fileDescriptor != null) {
      usbAccessory = accessory;
      final FileDescriptor descriptor = fileDescriptor.getFileDescriptor();
      inputStream = new FileInputStream(descriptor);
      outputStream = new FileOutputStream(descriptor);
      new Thread(this).start();
      Log.i(TAG, "accessory opened");
    } else {
      Log.i(TAG, "accessory closed");
    }
  }

  protected void onHandleDeviceResponse(final String text) {
    getHandler().handleMessage(Message.obtain(getHandler(), MESSAGE_DISPLAY, text));
  }

  @Override
  public void run() {
    Thread.yield();
    int ret = 0;
    final byte[] buffer = new byte[16384];

    while (ret >= 0) {
      try {
        ret = inputStream.read(buffer);
      } catch (final IOException e) {
        Utils.logThrowable(TAG, e);
        break;
      }

      if (ret > 0) {
        final String str = new String(buffer, 0, ret);
        onHandleDeviceResponse(str);
      }
    }
  }

  protected abstract Handler getHandler();

  private void closeAccessory() {
    try {
      if (fileDescriptor != null) {
        fileDescriptor.close();
      }
    } catch (final IOException ex) {
      Log.i(TAG, ex.getMessage());
    } finally {
      fileDescriptor = null;
      usbAccessory = null;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    closeAccessory();
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(usbReceiver);
    super.onDestroy();
  }
}
