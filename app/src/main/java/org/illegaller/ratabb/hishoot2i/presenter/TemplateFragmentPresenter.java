package org.illegaller.ratabb.hishoot2i.presenter;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.fragment.TemplateFragmentView;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class TemplateFragmentPresenter
        implements IPresenter<TemplateFragmentView>, MaterialSearchView.OnQueryTextListener {
    private List<Template> mTemplateList;
    private TemplateFragmentView mView;
    private PerformListTask mTask;
    private TemplateProvider mProvider;

    public TemplateProvider getProvider() {
        return mProvider;
    }

    @Override public void attachView(TemplateFragmentView view) {
        this.mView = view;
        this.mTemplateList = new ArrayList<>();
    }

    @Override public void detachView() {
        if (this.mTask != null) {
            this.mTask.cancel(true);
            this.mTask = null;
        }
        this.mView = null;
    }


    public void performList() {
        final Context context = this.mView.context();
        if (context != null) {
            this.mTask = new PerformListTask(context);
            this.mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    public void setSearchView(Activity activity, MenuItem item) {
        MaterialSearchView searchView = ButterKnife.findById(activity, R.id.search_view);
        searchView.setMenuItem(item);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(this);
        searchView.setTextColor(R.color.colorAccent);
        searchView.setHintTextColor(R.color.colorAccent);
    }

    @Override public boolean onQueryTextSubmit(String query) {       /*no-op*/
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
        if (Utils.isEmpty(query)) filter.addAll(mTemplateList);
        else {
            for (Template template : mTemplateList)
                if (contains(template, query)) filter.add(template);
        }
        return filter;
    }

    boolean contains(Template template, String query) {
        return Utils.containsLowerCase(template.name, query) ||
                Utils.containsLowerCase(template.author, query);
    }

    class PerformListTask extends AsyncTask<Void, Void, List<Template>> {

        private final Context context;

        PerformListTask(Context context) {
            this.context = context;
        }

        @Override protected List<Template> doInBackground(Void... voids) {
            mProvider = new TemplateProvider(context);
            return mProvider.asList();
        }

        @Override protected void onPreExecute() {
            mView.showProgress();
        }

        @Override protected void onPostExecute(List<Template> templates) {
            mView.hideProgress();
            mTemplateList.clear();
            mTemplateList.addAll(templates);
            mView.setTemplateList(mTemplateList);
        }
    }
}
