package org.illegaller.ratabb.hishoot2i.view.fragment;

import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.view.common.IVew;

import java.util.List;

public interface TemplateFragmentView extends IVew {
    void setTemplateList(List<Template> templateList);

    void showProgress();

    void hideProgress();
}
