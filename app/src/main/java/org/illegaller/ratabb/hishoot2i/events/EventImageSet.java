package org.illegaller.ratabb.hishoot2i.events;

public class EventImageSet {
  public final Type type;
  public final String path;

  public EventImageSet(Type type, String path) {
    this.type = type;
    this.path = path;
  }

  public enum Type {
    SS1, SS2, BG, NONE
  }
}
