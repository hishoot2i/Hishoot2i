package org.illegaller.ratabb.hishoot2i.model.template.builder;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenHeight;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenWidth;
import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.pref.IntPreference;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Context;

import javax.inject.Inject;

public class TemplateBuilderDefault extends AbstractTemplateBuilder {
    @Inject @UserDeviceScreenWidth IntPreference userScreenWidthPref;
    @Inject @UserDeviceScreenHeight IntPreference userScreenHeightPref;
    public TemplateBuilderDefault(Context context) {
        HishootApplication.get(context).inject(this);
        id = AppConstants.DEFAULT_TEMPLATE_ID;
        type = TemplateType.APK_V1;
        author = "DCSMS";
        name = "Default";
        final int TL = Utils.getDimensionPixelSize(context, R.dimen.def_tl);
        final int TT = Utils.getDimensionPixelSize(context, R.dimen.def_tt);
        final int BL = Utils.getDimensionPixelSize(context, R.dimen.def_bl);
        final int BT = Utils.getDimensionPixelSize(context, R.dimen.def_bt);
        previewFile = UILHelper.stringDrawables(R.drawable.default_preview);
        frameFile = UILHelper.stringDrawables(R.drawable.frame1);
        Sizes screenSizes =  Sizes.create(userScreenWidthPref.get(), userScreenHeightPref.get());
        Sizes offset = Sizes.create(TL, TT);
        templateSizes = Sizes.create(screenSizes.width + (TL + BL), screenSizes.height + (TT + BT));
        leftTop = offset;
        rightTop = Sizes.create(templateSizes.width - BL, offset.height);
        leftBottom = Sizes.create(offset.width, templateSizes.height - BT);
        rightBottom = Sizes.create(templateSizes.width - BL, templateSizes.height - BT);
    }
}
