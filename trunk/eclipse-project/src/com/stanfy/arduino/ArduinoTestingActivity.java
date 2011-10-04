package com.stanfy.arduino;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

public class ArduinoTestingActivity extends Activity {
	
	private static final String TAG = "ArduinoActivity";
	private static final String ACTION_USB_PERMISSION = "com.stanfy.arduino.action.USB_PERMISSION";
	private UsbManager usbManager;
	private PendingIntent pendingIntent;
	private boolean permissionEnabled;
	public UsbAccessory usbAccessory;
	public FileInputStream inputStream;
	public FileOutputStream outputStream;
	public ParcelFileDescriptor fileDescriptor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        usbManager = UsbManager.getInstance(getApplicationContext());
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(usbReceiver, intentFilter);
        
        if(getLastNonConfigurationInstance() != null) {
        	usbAccessory = (UsbAccessory) getLastNonConfigurationInstance();
        	openAccessory(usbAccessory);
        	Log.i(TAG, "accessoty opened !!!");
        }
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	if(usbAccessory != null) {
    		return usbAccessory;
    	} else {
    		return super.onRetainNonConfigurationInstance();
    	}	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(inputStream != null && outputStream != null) { return; }
    	
    	UsbAccessory[] accessories = usbManager.getAccessoryList();
    	UsbAccessory accessory = (accessories == null ? null : accessories[0]);
    	if(accessory != null) {
    		if(usbManager.hasPermission(accessory)) {
    			openAccessory(accessory);
    		} else {
    			synchronized(usbReceiver) {
    				if(!permissionEnabled) {
    					usbManager.requestPermission(accessory, pendingIntent);
    					permissionEnabled = true;
    				}
    			}
    		}
    	} else {
    		Log.i(TAG, "Accessory not found");
    	}
    }
    
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					}else {
						Log.i(TAG,"Permission for accessory " + accessory +"- denied");
					}
					permissionEnabled = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if(accessory != null && accessory.equals(usbAccessory)) {
					closeAccessory();
				}
			}
		}
	};
	
	private void openAccessory(UsbAccessory accessory) {
		fileDescriptor = usbManager.openAccessory(accessory);
		if(fileDescriptor != null) {
			usbAccessory = accessory;
			FileDescriptor descriptor = fileDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(descriptor);
			outputStream = new FileOutputStream(descriptor);
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					try {
//					while (true) {
//						Thread.yield();
//						if (inputStream != null) {
//					final StringBuffer buf = new StringBuffer();
//					final byte[] bbuf = new byte[256];
//						while (inputStream.read(bbuf) > 0) {
//							buf.append(new String(bbuf));
//						}
//					Toast.makeText(ArduinoTestingActivity.this, buf, Toast.LENGTH_LONG).show();
//						}
//					}
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}).start();
			Log.i(TAG, "accessory opened");
		} else {
			Log.i(TAG, "accessory closed");
		}
	}
	
	private void closeAccessory() {
		try{
			if(fileDescriptor != null) {
				fileDescriptor.close();
			}
		} catch (IOException ex) {
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