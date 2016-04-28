package org.illegaller.ratabb.hishoot2i.events;

public class EventBadgeBB {
  public final int count;
  public final Type type;

  public EventBadgeBB(Type type, int count) {
    this.count = count;
    this.type = type;
  }

  public enum Type {
    INSTALLED, FAV, SAVED
  }
}
