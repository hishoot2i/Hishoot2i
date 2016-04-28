package org.illegaller.ratabb.hishoot2i.events;

public class EventTemplateFav {
  public final String templateId;
  public final boolean isAdd;

  public EventTemplateFav(String templateId, boolean isAdd) {
    this.templateId = templateId;
    this.isAdd = isAdd;
  }
}
