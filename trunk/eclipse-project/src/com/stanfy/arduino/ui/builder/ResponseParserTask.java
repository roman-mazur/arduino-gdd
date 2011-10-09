package com.stanfy.arduino.ui.builder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.ViewGroup;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class ResponseParserTask {

  /** Response separator. */
  private static final String SEPARATOR = "Router>";

  /** Errors pattern. */
  private static final Pattern ERRORS = Pattern.compile("%.+\\n");

  /** Builder. */
  private UIBuilder builder;

  /** Current buffer. */
  private StringBuilder currentBuffer = new StringBuilder();

  /**
   * Commands.
   */
  public static enum Command {
    /** <code>show hardware</code>. */
    SHOW_HARDWARE(new HardwareParser(), "show hardware", "Hardware info"),
    SHOW_IP(new ShowIpOSPFParser(), "show ip ospf interface ethernet 0", "Show IP OSPF for 0 int");
    ///** <code>HELLO</code>. */
    //HELLO(new HelloParser(), "hello");

    final Parser parser;
    final String command;
    final String text;

    private Command(final Parser p, final String command, final String text) {
      this.parser = p;
      this.command = command;
      this.text = text;
    }

    /** @return the command */
    public String getCommand() { return command; }
    public String getText() { return text; }

    public static String[] titles() {
      final String[] result = new String[values().length];
      for (final Command c : values()) {
        result[c.ordinal()] = c.text;
      }
      return result;
    }

  }

  public void setContainer(final ViewGroup container) {
    this.builder = new UIBuilder(container);
  }

  private String filterString(final String text) {
    final int index = text.indexOf(SEPARATOR);
    if (index == -1) {
      currentBuffer.append(text);
      Log.d("123123", "Continue");
      return null;
    }
    String response = text.substring(0, index);
    if (currentBuffer.length() > 0) {
      response = currentBuffer.toString() + response;
      currentBuffer.delete(0, currentBuffer.length());
    }
    if (index < text.length() - SEPARATOR.length() - 1) {
      currentBuffer.append(text.substring(index + SEPARATOR.length()));
    }
    Log.d("123123", "B: " + response);
    final Matcher m = ERRORS.matcher(response);
    response = m.replaceAll("");
    Log.d("123123", "A: " + response);
    return response;
  }

  public void displayResponse(final Command command, final String response) {
    if (builder == null) { return; }
    final String text = filterString(response);
    if (text != null) {
      final List<UIDirective> directions = command.parser.parse(text);
      builder.publishParseResults(directions);
    }
  }

}
