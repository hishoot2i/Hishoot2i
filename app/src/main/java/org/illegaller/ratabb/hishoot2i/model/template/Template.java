package org.illegaller.ratabb.hishoot2i.model.template;

import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.builder.AbstractTemplateBuilder;

import android.support.annotation.Nullable;

public class Template  {

    /* Unique ID = application template packageName */
    public String id;
    public String name;
    public String author;
    public Sizes screenSizes;
    public Sizes templateSizes;
    public Sizes offset;
    public boolean isCompatible;
    public TemplateType type;
    // file image
    public String frameFile;
    public String previewFile; //same as frameFile if we don't have
    @Nullable public String glareFile; // template apk v1 is null
    @Nullable public String shadowFile; // new on template apk v2
    @Nullable public Sizes overlayOffset; //htz

    public Template() {
    }

    public static Template build(AbstractTemplateBuilder builder) {
        Template template = new Template();
        template.id = builder.id;
        template.name = builder.name;
        template.author = builder.author;
        template.screenSizes = builder.screenSizes;
        template.templateSizes = builder.templateSizes;
        template.offset = builder.offset;
        template.isCompatible = builder.isCompatible;
        template.type = builder.type;
        template.previewFile = builder.previewFile;
        template.frameFile = builder.frameFile;
        template.glareFile = builder.glareFile;
        template.shadowFile = builder.shadowFile;
        template.overlayOffset = builder.overlayOffset;
        return template;
    }

    @Override public String toString() {
        return "Template{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", screenSizes=" + screenSizes +
                ", templateSizes=" + templateSizes +
                ", offset=" + offset +
                ", isCompatible=" + isCompatible +
                ", type=" + type +
                ", frameFile='" + frameFile + '\'' +
                ", previewFile='" + previewFile + '\'' +
                ", glareFile='" + glareFile + '\'' +
                ", shadowFile='" + shadowFile + '\'' +
                ", overlayOffset=" + overlayOffset +
                '}';
    }
}
