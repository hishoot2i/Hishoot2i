package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import java.util.List;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;

public interface TemplateFragmentView extends Mvp.View {
  void setTemplateList(List<Template> templateList);

  void showProgress(boolean isShow);
}
