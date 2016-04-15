package org.illegaller.ratabb.hishoot2i.events;

public class EventTemplateFav {
    public final String templateId;
    public final Boolean isAdd;

    public EventTemplateFav(String templateId, Boolean isAdd) {
        this.templateId = templateId;
        this.isAdd = isAdd;
    }
}
