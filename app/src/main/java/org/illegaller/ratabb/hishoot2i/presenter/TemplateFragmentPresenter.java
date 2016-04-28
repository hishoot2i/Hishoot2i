package org.illegaller.ratabb.hishoot2i.presenter;

import android.app.Activity;
import android.view.MenuItem;
import butterknife.ButterKnife;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.List;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.SimpleObserver;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.fragment.TemplateFragmentView;

public class TemplateFragmentPresenter
    implements IPresenter<TemplateFragmentView>, MaterialSearchView.OnQueryTextListener {
  private TemplateManager tManager;
  private List<Template> mTemplateList = new ArrayList<>();
  private TemplateFragmentView mView;

  public TemplateFragmentPresenter(TemplateManager tManager) {
    this.tManager = tManager;
  }

  public Template getTemplateById(String templateId) {
    return tManager.getTemplateById(templateId);
  }

  @Override public void attachView(TemplateFragmentView view) {
    this.mView = view;
  }

  @Override public void detachView() {
    this.mView = null;
  }

  public void performList(String favListId) {
    mView.showProgress(true);
    tManager.getTemplateList(favListId).subscribe(new SimpleObserver<List<Template>>() {
      @Override public void onCompleted() {
        mView.showProgress(false);
      }

      @Override public void onError(Throwable e) {
        mView.showProgress(false);
        CrashLog.logError("getSubscriber", e);
      }

      @Override public void onNext(List<Template> list) {
        mView.showProgress(false);
        mTemplateList.clear();
        mTemplateList.addAll(list);
        mView.setTemplateList(mTemplateList);
      }
    });
  }

  public void setupSearchView(Activity activity, MenuItem item) {
    MaterialSearchView searchView = ButterKnife.findById(activity, R.id.search_view);
    searchView.setMenuItem(item);
    searchView.setVoiceSearch(false);
    searchView.setOnQueryTextListener(this);
    searchView.setTextColor(R.color.colorAccent);
    searchView.setHintTextColor(R.color.colorAccent);
  }

  @Override public boolean onQueryTextSubmit(String query) { /*no-op*/
    return false;
  }

  @Override public boolean onQueryTextChange(String query) {
    this.mView.setTemplateList(filter(query));
    return true;
  }

  //////////////////////////////////////////////////////////////
  List<Template> filter(String query) {
    final List<Template> filter = new ArrayList<>();
    if (mTemplateList == null || mTemplateList.size() == 0) return filter;
    if (Utils.isEmpty(query)) {
      filter.addAll(mTemplateList);
    } else {
      for (Template template : mTemplateList)
        if (contains(template, query)) filter.add(template);
    }
    return filter;
  }

  boolean contains(Template template, String query) {
    return Utils.containsLowerCase(template.name, query) || Utils.containsLowerCase(template.author,
        query);
  }
}
