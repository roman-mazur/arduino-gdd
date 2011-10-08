package com.stanfy.arduino.ui.builder;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * @author Olexandr Tereshchuk (Stanfy - http://www.stanfy.com)
 */
public class HelloParser implements Parser {

  @Override
  public List<UIDirective> parse(final String input) {
    final LinkedList<UIDirective> result = new LinkedList<UIDirective>();
    final SpannableString s = new SpannableString(input);
    s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, input.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    result.add(new TextViewDirective(s));
    return result;
  }

}
