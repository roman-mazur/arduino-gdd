package com.stanfy.arduino.ui.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class HardwareParser implements Parser {

  private static final Pattern TECH_SUPPORT_PATTERN = Pattern.compile("Technical\\sSupport:\\s(http://.+)" + (DEBUG ? "" : "\\n"));

  private static Spannable url(final String label, final String url) {
    final SpannableString result = new SpannableString(label + ": " + url);
    result.setSpan(new ForegroundColorSpan(Color.GREEN), 0, label.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    result.setSpan(new URLSpan(url), label.length() + 2, result.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    return result;
  }

  @Override
  public List<UIDirective> parse(final String text) {
    final ArrayList<UIDirective> result = new ArrayList<UIDirective>();
    final Matcher m = TECH_SUPPORT_PATTERN.matcher(text);
    if (m.matches()) {
      result.add(new TextViewDirective(url("Tech support", m.group(1))));
    }
    return result;
  }

}
