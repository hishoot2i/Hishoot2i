package org.illegaller.ratabb.hishoot2i.events;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public class EventPreview {
  private static final String EMPTY = "";
  public final String message;
  public final String extra;
  @Nullable public final Bitmap bitmap;

  private EventPreview(@Nullable Bitmap bitmap, String message, String extra) {
    this.bitmap = bitmap;
    this.message = message;
    this.extra = extra;
  }

  public static EventPreview messageExtra(String message, String extra) {
    return new EventPreview(null, message, extra);
  }

  public static EventPreview result(Bitmap bitmap) {
    return new EventPreview(bitmap, EMPTY, EMPTY);
  }
}
