package com.stanfy.arduino.ui.builder;

import android.content.Context;
import android.view.View;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public interface UIDirective {

  View createView(final Context context);

}
