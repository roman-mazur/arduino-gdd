package com.stanfy.arduino.ui.builder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.view.ViewGroup;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class ResponseParserTask {

  /** Response separator. */
  private static final String SEPARATOR = "Router>";

  /** Errors pattern. */
  private static final Pattern ERRORS = Pattern.compile("%Error.+\\n");

  /** Builder. */
  private UIBuilder builder;

  /** Current buffer. */
  private StringBuilder currentBuffer = new StringBuilder();

  /**
   * Commands.
   */
  public static enum Command {
    /** <code>show hardware</code>. */
    SHOW_HARDWARE(new HardwareParser());

    final Parser parser;

    private Command(final Parser p) { this.parser = p; }

  }

  public void setContainer(final ViewGroup container) {
    this.builder = new UIBuilder(container);
  }

  private String filterString(final String text) {
    final int index = text.indexOf(SEPARATOR);
    if (index == -1) {
      currentBuffer.append(text);
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
    final Matcher m = ERRORS.matcher(response);
    response = m.replaceAll("");
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
