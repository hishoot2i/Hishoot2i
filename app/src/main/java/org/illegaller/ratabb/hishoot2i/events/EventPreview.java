package org.illegaller.ratabb.hishoot2i.events;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public class EventPreview {
  public final String message;
  public final String extra;
  @Nullable public final Bitmap result;

  public EventPreview(@Nullable Bitmap result, String message, String extra) {
    this.result = result;
    this.message = message;
    this.extra = extra;
  }
}
