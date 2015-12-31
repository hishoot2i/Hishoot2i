package org.illegaller.ratabb.hishoot2i.model.template;

public enum TemplateType {
    APK_V1("apk_v1"), APK_V2("apk_v2"), HTZ("htz");

    String id;

    TemplateType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
