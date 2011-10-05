package com.stanfy.arduino.ui.builder;

import java.util.List;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public interface Parser {

  boolean DEBUG = true;

  List<UIDirective> parse(final String text);

}
