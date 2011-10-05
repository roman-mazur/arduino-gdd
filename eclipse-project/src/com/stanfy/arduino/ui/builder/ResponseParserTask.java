package com.stanfy.arduino.ui.builder;

import java.util.List;

import android.view.ViewGroup;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class ResponseParserTask {

  /** Builder. */
  private UIBuilder builder;

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

  public void displayResponse(final Command command, final String response) {
    if (builder == null) { return; }
    final List<UIDirective> directions = command.parser.parse(response);
    builder.publishParseResults(directions);
  }

}
