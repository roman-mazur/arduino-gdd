package com.stanfy.arduino.ui.builder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class TextViewDirective implements UIDirective {

  /** Text. */
  private CharSequence text;

  public TextViewDirective(final CharSequence text) { this.text = text; }

  @Override
  public View createView(final Context context) {
    final TextView textView = new TextView(context);
    textView.setText(text);
    return textView;
  }

}
