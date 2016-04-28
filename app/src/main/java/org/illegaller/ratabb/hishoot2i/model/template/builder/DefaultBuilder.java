package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.graphics.Point;
import javax.inject.Inject;
import javax.inject.Named;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;

public class DefaultBuilder extends BaseBuilder {
  @Inject @Named(IKeyNameTray.DEVICE_HEIGHT) IntTray deviceHeightTray;

  @Inject @Named(IKeyNameTray.DEVICE_WIDTH) IntTray deviceWidthTray;

  public DefaultBuilder(Context context) {
    HishootApplication.get(context).getApplicationComponent().inject(this);
    id = AppConstants.DEFAULT_TEMPLATE_ID;
    type = TemplateType.APK_V1;
    author = "DCSMS";
    name = "Default";
    final int TL = ResUtils.getDimensionPixelSize(context, R.dimen.def_tl);
    final int TT = ResUtils.getDimensionPixelSize(context, R.dimen.def_tt);
    final int BL = ResUtils.getDimensionPixelSize(context, R.dimen.def_bl);
    final int BT = ResUtils.getDimensionPixelSize(context, R.dimen.def_bt);
    previewFile = UILHelper.stringDrawables(R.drawable.default_preview);
    frameFile = UILHelper.stringDrawables(R.drawable.frame1);
    Point screenPoint = new Point(deviceWidthTray.get(), deviceHeightTray.get());
    Point offset = new Point(TL, TT);
    templatePoint = new Point(screenPoint.x + (TL + BL), screenPoint.y + (TT + BT));
    leftTop = offset;
    rightTop = new Point(templatePoint.x - BL, offset.y);
    leftBottom = new Point(offset.x, templatePoint.y - BT);
    rightBottom = new Point(templatePoint.x - BL, templatePoint.y - BT);
  }

  @Override public Template build() {
    return Template.build(this);
  }
}
