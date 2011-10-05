package com.stanfy.arduino.ui.builder;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.stanfy.arduino.Animations;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class UIBuilder {

  /** Container. */
  private final ViewGroup container;


  public UIBuilder(final ViewGroup container) {
    this.container = container;
    container.setLayoutAnimation(Animations.goDownAnimationController());
  }

  public void publishParseResults(final List<UIDirective> directions) {
    final Runnable task = new Runnable() {
      @Override
      public void run() {
        container.removeAllViews();
        for (final UIDirective d : directions) { build(d); }
      }
    };
    final Context ctx = container.getContext();
    if (ctx instanceof Activity) {
      ((Activity)ctx).runOnUiThread(task);
    } else { // why ???
      container.post(task);
    }
  }

  private void build(final UIDirective d) {
    final View v = d.createView(container.getContext());
    container.addView(v);
  }

}
