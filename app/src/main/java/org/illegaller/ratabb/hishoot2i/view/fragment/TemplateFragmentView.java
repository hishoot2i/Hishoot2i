package org.illegaller.ratabb.hishoot2i.view.fragment;

import java.util.List;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.view.common.IVew;

public interface TemplateFragmentView extends IVew {
  void setTemplateList(List<Template> templateList);

  void showProgress(boolean isShow);
}
