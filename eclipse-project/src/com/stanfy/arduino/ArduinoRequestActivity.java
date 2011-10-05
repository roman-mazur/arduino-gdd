package com.stanfy.arduino;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ArduinoRequestActivity extends ArduinoBaseActivity implements
    OnClickListener {

  private static final String TAG = "RequestA";

  private Button button;
  private EditText input;
  private TextView output;

  private final Handler mesHandler = new Handler() {
    public void handleMessage(final Message msg) {
      switch (msg.what) {
        case MESSAGE_DISPLAY:
          runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
              if (msg.obj == null) {
                output.append("Nothing to display\n");
              } else {
                output.append(msg.obj.toString() + "\n");
              }
            }
          });
          break;
        default:
      }
    }
  };
  
  protected Handler getHandler() { return mesHandler; }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    button = (Button) findViewById(R.id.send);
    button.setOnClickListener(this);
    input = (EditText) findViewById(R.id.edit);
    output = (TextView) findViewById(R.id.text);
  }

  @Override
  public void onClick(final View v) {
    if (v.getId() == R.id.send) {
      try {
        if (inputStream != null && !TextUtils.isEmpty(input.getText())) {
          outputStream.write(input.getText().toString().getBytes());
          outputStream.flush();
          input.setText("");
        }
      } catch (final IOException e) {
        output.setText(Utils.logThrowable(TAG, e) + "\n");
      }
    }
  }

}
