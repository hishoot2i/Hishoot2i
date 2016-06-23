package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.graphics.Point;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;

import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getDimensionPixelSize;
import static org.illegaller.ratabb.hishoot2i.utils.UILHelper.stringDrawables;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.createPoint;

public class DefaultBuilder extends BaseBuilder {

  public DefaultBuilder(Context context) {
    super(context);
    final TrayManager mTrayManager = HishootApplication.get(getContext()).getTrayManager();
    this.id = AppConstants.DEFAULT_TEMPLATE_ID;
    this.type = TemplateType.APK_V1;
    this.author = "DCSMS";
    this.name = "Default";
    final int TL = getDimensionPixelSize(getContext(), R.dimen.def_tl);
    final int TT = getDimensionPixelSize(getContext(), R.dimen.def_tt);
    final int BL = getDimensionPixelSize(getContext(), R.dimen.def_bl);
    final int BT = getDimensionPixelSize(getContext(), R.dimen.def_bt);
    this.previewFile = stringDrawables(R.drawable.default_preview);
    this.frameFile = stringDrawables(R.drawable.frame1);
    final Point screenPoint = createPoint(mTrayManager.getDeviceWidth().getValue(),
        mTrayManager.getDeviceHeight().getValue());
    final Point offset = createPoint(TL, TT);
    this.templatePoint = createPoint(screenPoint.x + (TL + BL), screenPoint.y + (TT + BT));
    this.leftTop = offset;
    this.rightTop = createPoint(templatePoint.x - BL, offset.y);
    this.leftBottom = createPoint(offset.x, templatePoint.y - BT);
    this.rightBottom = createPoint(templatePoint.x - BL, templatePoint.y - BT);
  }

  @Override public Template build() throws Exception {
    return Template.build(this);
  }
}
