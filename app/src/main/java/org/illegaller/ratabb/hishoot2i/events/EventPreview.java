package org.illegaller.ratabb.hishoot2i.events;

import android.graphics.Bitmap;

public class EventPreview {
  public final String message;
  public final String extra;
  public final Bitmap result;

  public EventPreview(Bitmap result, String message, String extra) {
    this.result = result;
    this.message = message;
    this.extra = extra;
  }
}
