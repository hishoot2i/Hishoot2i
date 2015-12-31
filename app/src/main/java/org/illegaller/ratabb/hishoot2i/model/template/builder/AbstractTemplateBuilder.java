package org.illegaller.ratabb.hishoot2i.model.template.builder;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenHeight;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenWidth;
import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.pref.IntPreference;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;

import android.content.Context;

import javax.inject.Inject;

public abstract class AbstractTemplateBuilder {
    public Sizes templateSizes;
    public Sizes screenSizes;
    public Sizes offset;
    public boolean isCompatible;
    public String name;
    public String author;
    public String id;
    public TemplateType type;
    public String previewFile;
    public String frameFile;
    public String glareFile;
    public String shadowFile;
    public Sizes overlayOffset;
    @Inject @UserDeviceScreenWidth IntPreference userScreenWidthTray;
    @Inject @UserDeviceScreenHeight IntPreference userScreenHeightTray;
    Sizes userDeviceScreenSizes;
    Context mContext;

    AbstractTemplateBuilder(Context context) {
        HishootApplication.get(context).inject(this);
        mContext = context;
        userDeviceScreenSizes = Sizes.create(userScreenWidthTray.get(), userScreenHeightTray.get());
    }

    public Template build() {
        return Template.build(this);
    }


}
