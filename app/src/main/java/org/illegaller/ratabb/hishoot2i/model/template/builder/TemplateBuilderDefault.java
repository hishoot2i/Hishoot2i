package org.illegaller.ratabb.hishoot2i.model.template.builder;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Context;

public class TemplateBuilderDefault extends AbstractTemplateBuilder {
    public TemplateBuilderDefault(Context context) {
        super(context);
        id = AppConstants.DEFAULT_TEMPLATE_ID;
        type = TemplateType.APK_V1;
        author = "DCSMS";
        name = "Default";
        isCompatible = true;

        final int TL = Utils.getDimensionPixelSize(context, R.dimen.def_tl);
        final int TT = Utils.getDimensionPixelSize(context, R.dimen.def_tt);
        final int BL = Utils.getDimensionPixelSize(context, R.dimen.def_bl);
        final int BT = Utils.getDimensionPixelSize(context, R.dimen.def_bt);


        previewFile = UILHelper.stringDrawables(R.drawable.default_preview);
        frameFile = UILHelper.stringDrawables(R.drawable.frame1);//no-op
        glareFile = shadowFile = null;

        screenSizes = userDeviceScreenSizes;
        offset = Sizes.create(TL, TT);
        templateSizes = Sizes.create(screenSizes.width + (TL + BL), screenSizes.height + (TT + BT));
    }
}
