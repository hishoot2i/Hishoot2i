package org.illegaller.ratabb.hishoot2i.presenter;

import android.content.Context;
import android.view.MenuItem;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateFragmentView;
import org.illegaller.ratabb.hishoot2i.view.rx.RxMaterialSearchView;

import static org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule.schedule;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.containsLowerCase;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.isEmpty;

public class TemplateFragmentPresenter extends BasePresenter<TemplateFragmentView> {
  @Inject TemplateManager mTemplateManager;
  private List<Template> mTemporaryList;

  @Inject TemplateFragmentPresenter() {
  }

  public Template getTemplateById(String templateId) {
    return mTemplateManager.getTemplateById(templateId);
  }

  public void performList(String favListId) {
    checkViewAttached();
    showProgress();
    addAutoUnSubscribe(mTemplateManager.getTemplateList(favListId)
        .compose(schedule())
        .subscribe(this::setTemporaryList, CrashLog::logError, this::hideProgress));
  }

  public void setupSearchView(Context context, MenuItem item) {
    final MaterialSearchView view = ((LauncherActivity) context).getSearchView();
    view.setMenuItem(item);
    view.setVoiceSearch(false);
    view.setTextColor(R.color.colorAccent);
    view.setHintTextColor(R.color.colorAccent);
    addAutoUnSubscribe(RxMaterialSearchView.queryTextChanges(view)
        .subscribe(this::queryTextChanges, CrashLog::logError));
  }

  private void showProgress() {
    getMvpView().showProgress(true);
  }

  private void hideProgress() {
    getMvpView().showProgress(false);
  }

  private void queryTextChanges(String query) {
    final List<Template> filterList = new ArrayList<>();
    if (getTemporaryList().size() != 0) {
      if (isEmpty(query)) {
        filterList.addAll(getTemporaryList());
      } else {
        filterList.addAll(Stream.of(getTemporaryList())
            .filter(template -> contains(template, query))
            .collect(Collectors.toList()));
      }
    }
    getMvpView().setTemplateList(filterList);
  }

  private boolean contains(Template template, String query) {
    return containsLowerCase(template.name, query) || containsLowerCase(template.author, query);
  }

  private List<Template> getTemporaryList() {
    if (mTemporaryList == null) mTemporaryList = new ArrayList<>();
    return mTemporaryList;
  }

  private void setTemporaryList(List<Template> list) {
    final List<Template> temp = getTemporaryList();
    temp.clear();
    temp.addAll(list);
    getMvpView().setTemplateList(list);
  }
}
