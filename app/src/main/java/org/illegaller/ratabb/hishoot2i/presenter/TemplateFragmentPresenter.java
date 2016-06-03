package org.illegaller.ratabb.hishoot2i.presenter;

import android.app.Activity;
import android.view.MenuItem;
import butterknife.ButterKnife;
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
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateFragmentView;
import rx.Subscription;

public class TemplateFragmentPresenter extends BasePresenter<TemplateFragmentView> {
  private final List<Template> mTemplateList = new ArrayList<>();
  @Inject TemplateManager mTemplateManager;
  private Subscription mSubscription;

  @Inject public TemplateFragmentPresenter() {
  }

  public Template getTemplateById(String templateId) {
    return mTemplateManager.getTemplateById(templateId);
  }

  @Override public void detachView() {
    if (mSubscription != null) mSubscription.unsubscribe();
    super.detachView();
  }

  public void performList(String favListId) {
    checkViewAttached();
    getView().showProgress(true);
    mSubscription = mTemplateManager.getTemplateList(favListId)
        .compose(SimpleSchedule.schedule())
        .subscribe(this::setTemplates,
            throwable -> CrashLog.logError("perform populate list template", throwable),
            () -> getView().showProgress(false));
  }

  public void setupSearchView(Activity activity, MenuItem item) {
    MaterialSearchView searchView = ButterKnife.findById(activity, R.id.search_view);
    searchView.setMenuItem(item);
    searchView.setVoiceSearch(false);
    searchView.setOnQueryTextListener(new SearchOnQueryTextListener());
    searchView.setTextColor(R.color.colorAccent);
    searchView.setHintTextColor(R.color.colorAccent);
  }

  void setTemplates(List<Template> list) {
    mTemplateList.clear();
    mTemplateList.addAll(list);
    getView().setTemplateList(mTemplateList);
  }

  boolean contains(Template template, String query) {
    return Utils.containsLowerCase(template.name, query) || Utils.containsLowerCase(template.author,
        query);
  }

  //////////////////////////////////////////////////////////////
  class SearchOnQueryTextListener implements MaterialSearchView.OnQueryTextListener {

    @Override public boolean onQueryTextSubmit(String query) {
      return false;
    }

    @Override public boolean onQueryTextChange(String newText) {
      final List<Template> filterList = new ArrayList<>();
      if (mTemplateList.size() != 0) {
        if (Utils.isEmpty(newText)) {
          filterList.addAll(mTemplateList);
        } else {
          filterList.addAll(Stream.of(mTemplateList)
              .filter(template -> contains(template, newText))
              .collect(Collectors.toList()));
        }
      }
      getView().setTemplateList(filterList);
      return false;
    }
  }
}
