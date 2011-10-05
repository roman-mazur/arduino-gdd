package com.stanfy.arduino.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.stanfy.arduino.R;
import com.stanfy.arduino.ui.builder.ResponseParserTask.Command;

/**
 * @author Olexandr Tereshchuk (Stanfy - http://www.stanfy.com)
 */
public class CommandsAdapter extends ArrayAdapter<Command> {

  public CommandsAdapter(final Context context) {
    super(context, R.layout.command_item, Command.values());
  }

}
