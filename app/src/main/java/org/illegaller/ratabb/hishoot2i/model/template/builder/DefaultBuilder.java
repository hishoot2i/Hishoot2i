package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.graphics.Point;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;

public class DefaultBuilder extends BaseBuilder {
  @Inject TrayManager mTrayManager;

  public DefaultBuilder(Context mContext) {
    HishootApplication.get(mContext).getAppComponent().inject(this);
    id = AppConstants.DEFAULT_TEMPLATE_ID;
    type = TemplateType.APK_V1;
    author = "DCSMS";
    name = "Default";
    final int TL = ResUtils.getDimensionPixelSize(mContext, R.dimen.def_tl);
    final int TT = ResUtils.getDimensionPixelSize(mContext, R.dimen.def_tt);
    final int BL = ResUtils.getDimensionPixelSize(mContext, R.dimen.def_bl);
    final int BT = ResUtils.getDimensionPixelSize(mContext, R.dimen.def_bt);
    previewFile = UILHelper.stringDrawables(R.drawable.default_preview);
    frameFile = UILHelper.stringDrawables(R.drawable.frame1);
    Point screenPoint = new Point(mTrayManager.getDeviceWidth().getValue(),
        mTrayManager.getDeviceHeight().getValue());
    Point offset = new Point(TL, TT);
    templatePoint = new Point(screenPoint.x + (TL + BL), screenPoint.y + (TT + BT));
    leftTop = offset;
    rightTop = new Point(templatePoint.x - BL, offset.y);
    leftBottom = new Point(offset.x, templatePoint.y - BT);
    rightBottom = new Point(templatePoint.x - BL, templatePoint.y - BT);
  }

  @Override public Template build() throws Exception {
    return Template.build(this);
  }
}
