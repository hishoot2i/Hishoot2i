package org.illegaller.ratabb.hishoot2i.events;

public class EventMainSetImage {
    public final WhatImage whatImage;
    public final String path;

    public EventMainSetImage(WhatImage whatImage, String path) {
        this.whatImage = whatImage;
        this.path = path;
    }

    public enum WhatImage {
        SS1,SS2,BG
    }
}
