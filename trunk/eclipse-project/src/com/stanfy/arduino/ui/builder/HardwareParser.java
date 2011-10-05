package com.stanfy.arduino.ui.builder;

import java.util.Collections;
import java.util.List;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class HardwareParser implements Parser {

  @Override
  public List<UIDirective> parse(final String text) {
    return Collections.<UIDirective>singletonList(new TextViewDirective(text));
  }

}
