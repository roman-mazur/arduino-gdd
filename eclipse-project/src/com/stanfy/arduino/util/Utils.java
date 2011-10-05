package com.stanfy.arduino.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public final class Utils {

  private Utils() { /* hidden */ }
  
  public static String logThrowable(final String tag, final Throwable th) {
    Log.e(tag, "Error occured", th);
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, false);
    th.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }
}
