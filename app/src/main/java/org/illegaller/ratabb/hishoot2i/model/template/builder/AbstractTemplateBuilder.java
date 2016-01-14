package org.illegaller.ratabb.hishoot2i.model.template.builder;

import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;

public abstract class AbstractTemplateBuilder {
    public Sizes templateSizes;
    public String name;
    public String author;
    public String id;
    public TemplateType type;
    public String previewFile;
    public String frameFile;
    public String glareFile;
    public String shadowFile;
    public Sizes overlayOffset;
    public Sizes leftTop;
    public Sizes rightTop;
    public Sizes leftBottom;
    public Sizes rightBottom;

    public Template build() {
        return Template.build(this);
    }
}
