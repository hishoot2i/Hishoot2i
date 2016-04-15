package org.illegaller.ratabb.hishoot2i.view;

import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.view.common.IVew;
import org.illegaller.ratabb.hishoot2i.view.fragment.MainFragment;

public interface MainActivityView extends IVew {
    void fabSow(boolean isShow);
    void setMainFragment(ImageReceive imageReceive, Template template);
}
