package com.stanfy.arduino;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stanfy.arduino.ui.builder.ResponseParserTask;
import com.stanfy.arduino.ui.builder.ResponseParserTask.Command;

public class ArduinoRequestActivity extends ArduinoBaseActivity implements
    OnClickListener {

  private static final String TAG = "RequestA";

  private Button button;
  private EditText input;
  private TextView output;

  /** Response container. */
  private ViewGroup responseContainer;

  private final ResponseParserTask responseParserTask = new ResponseParserTask();

  private final Handler mesHandler = new Handler() {
    @Override
    public void handleMessage(final Message msg) {
      switch (msg.what) {
        case MESSAGE_DISPLAY:
//          runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//              if (msg.obj == null) {
//                output.append("Nothing to display\n");
//              } else {
//                output.append(msg.obj.toString() + "\n");
//              }
//            }
//          });
          break;
        default:
      }
    }
  };

  @Override
  protected Handler getHandler() { return mesHandler; }

  protected void setupResponseContainer(final ViewGroup container) {
    this.responseContainer = container;
    responseParserTask.setContainer(container);
  }

  @Override
  protected void onHandleDeviceResponse(final String text) {
    super.onHandleDeviceResponse(text);
    if (responseContainer != null) {
      responseParserTask.displayResponse(Command.SHOW_HARDWARE, text);
    }
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    button = (Button) findViewById(R.id.send);
    button.setOnClickListener(this);
    input = (EditText) findViewById(R.id.edit);
    output = (TextView) findViewById(R.id.text);

    setupResponseContainer((ViewGroup)findViewById(R.id.response_container));
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
