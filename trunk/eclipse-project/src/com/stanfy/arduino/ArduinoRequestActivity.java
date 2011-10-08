package com.stanfy.arduino;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.stanfy.arduino.ui.builder.ResponseParserTask;
import com.stanfy.arduino.ui.builder.ResponseParserTask.Command;
import com.stanfy.arduino.util.CommandsAdapter;
import com.stanfy.arduino.util.Utils;

public class ArduinoRequestActivity extends ArduinoBaseActivity implements
    OnClickListener {

  private static final String TAG = "RequestA";

  private Button button, sendIp;
  private EditText ipText;

  private Spinner spinner;

  /** Response container. */
  private ViewGroup responseContainer;

  private final ResponseParserTask responseParserTask = new ResponseParserTask();

  private final Handler mesHandler = new Handler() {
    @Override
    public void handleMessage(final Message msg) {
      switch (msg.what) {
        case MESSAGE_DISPLAY:
          /*
          runOnUiThread(new Runnable() {

            @Override
            public void run() {
              if (msg.obj == null) {
                Toast.makeText(ArduinoRequestActivity.this, "", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(ArduinoRequestActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
              }
            }
          });
          */
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
    sendIp = (Button) findViewById(R.id.send_ip);
    sendIp.setOnClickListener(this);
    ipText = (EditText) findViewById(R.id.ip_text);

    setupResponseContainer((ViewGroup)findViewById(R.id.response_container));

    spinner = (Spinner)findViewById(R.id.command_spinner);
    spinner.setAdapter(new CommandsAdapter(this));
  }

  public byte[] getServersIp() {

    final String ip = ipText.getText().toString();
    final String[] parsed = ip.split("\\.");
    final byte[] parsedByte = new byte[4];

    for(int i = 0; i < 4; i++) {
      final int parsedNumber = Integer.parseInt(parsed[i]);
      parsedByte[i] = (byte)parsedNumber;
    }

    return parsedByte;
  }

  @Override
  public void onClick(final View v) {
    try {
      if (v.getId() == R.id.send) {
        final Command c = Command.values()[spinner.getSelectedItemPosition()];
        if (inputStream != null) {
          outputStream.write((c.getCommand() + "\n").getBytes());
          outputStream.flush();
        }
      }
      if(v.getId() == R.id.send_ip) {

        if(inputStream != null) {
          outputStream.write(getServersIp());
          outputStream.flush();
        }
      }
    } catch (final Exception e) {
      final TextView view = new TextView(this);
      view.setText(Utils.logThrowable(TAG, e) + "\n");
      responseContainer.removeAllViews();
      responseContainer.addView(view);
    }
  }

}
