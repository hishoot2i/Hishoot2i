package org.illegaller.ratabb.hishoot2i.events;

import android.net.Uri;

public class EventSave {
  public final Uri uri;

  private EventSave(Uri uri) {
    this.uri = uri;
  }

  public static EventSave create(Uri uri) {
    return new EventSave(uri);
  }
}
