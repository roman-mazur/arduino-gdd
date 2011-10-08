package com.stanfy.arduino.ui.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class HardwareParser implements Parser {

  //private static final Pattern TECH_SUPPORT_PATTERN = Pattern.compile(".*Technical\\sSupport:\\s(http://.+)\n");
  private static final Pattern TECH_SUPPORT_PATTERN = Pattern.compile("Technical\\sSupport:\\s(http://[a-zA-Z\\.0-9&\\?=]+)\\s*");
  private static final Pattern COMPILED_PATTERN = Pattern.compile("Compiled\\s([a-zA-Z0-9\\s:-]*)\\sby\\s(\\w+).*", Pattern.DOTALL);
  private static final Pattern PROCESSOR_PATTERN = Pattern.compile("Cisco\\s*([a-zA-Z0-9]*\\s*(\\([a-zA-Z0-9]*\\))?)\\s*processor.*with\\s*([0-9\\.]*[kKmMgGtTpP]*)\\s*([a-zA-Z0-9]*).*");

  private static Spannable url(final String label, final String url) {
    final SpannableString result = new SpannableString(label + ": " + url);
    result.setSpan(new ForegroundColorSpan(Color.GREEN), 0, label.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    result.setSpan(new URLSpan(url), label.length() + 2, result.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    return result;
  }

  private static Spannable compileInfo(final String time, final String author) {
    final String label = "Compiled";
    final SpannableString result = new SpannableString(label + ": " + time + " by " + author);
    result.setSpan(new ForegroundColorSpan(Color.GREEN), 0, label.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    result.setSpan(new ForegroundColorSpan(Color.GRAY), label.length() + 2, label.length() + 2 + time.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    return result;
  }

  private static Spannable processorInfo(final String model, final String memorySize, final String memoryUnits) {
    final String pattern = "<font color=\"green\">Processor:</font> <font color=\"grey\">%s</font><br/>" +
    		"<font color=\"green\">Virtual memory:</font> <font color=\"grey\">%s %s</font><br/>";
    return new SpannableString(Html.fromHtml(String.format(pattern, model, memorySize, memoryUnits)));
  }

  @Override
  public List<UIDirective> parse(final String input) {
    final String texts = input.replaceAll("\r", "");
    final ArrayList<UIDirective> result = new ArrayList<UIDirective>();
    for (final String text : texts.split("\n")) {
      Log.d("View", Arrays.toString(text.getBytes()));
      Matcher m = TECH_SUPPORT_PATTERN.matcher(text);
      if (m.matches()) {
        result.add(new TextViewDirective(url("Tech support", m.group(1))));
      }
      m = COMPILED_PATTERN.matcher(text);
      if (m.matches()) {
        result.add(new TextViewDirective(compileInfo(m.group(1), m.group(2))));
      }
      m = PROCESSOR_PATTERN.matcher(text);
      if (m.matches()) {
        result.add(new TextViewDirective(processorInfo(m.group(1), m.group(3), m.group(4))));
      }
    }

    return result;
  }

}
