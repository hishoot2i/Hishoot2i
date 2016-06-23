package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import java.util.List;
import org.illegaller.ratabb.hishoot2i.model.template.Template;

class TemplateAndFavList {
  final Template mTemplate;
  final List<String> mFavList;

  TemplateAndFavList(Template mTemplate, List<String> mFavList) {
    this.mTemplate = mTemplate;
    this.mFavList = mFavList;
  }
}
