package com.stanfy.arduino;

import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ArduinoRequestActivity extends ArduinoTestingActivity {

	private Button button;
	private final byte port = 0x1;
	private final byte mode = 0x0;
	private final byte value = 0x1;
	private final byte portNumber = 0x2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		button = (Button) findViewById(R.id.send);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendByte(port, portNumber, mode, value);
			}
		});
	}
	
	public void sendByte(byte portType, byte portNumber, byte mode, byte value) {
		byte[] bufferBytes = new byte[4];
		bufferBytes[0] = portType;
		bufferBytes[1] = portNumber;
		bufferBytes[2] = mode;
		bufferBytes[3] = value;
		
		if (inputStream != null && bufferBytes[1] != -1) {
			try {
				outputStream.write(bufferBytes);
				outputStream.flush();
			} catch (IOException ex) {
				Log.e("ERROR", ex.getMessage());
			}
		}
	}
	
}
