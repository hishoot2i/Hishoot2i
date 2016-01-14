package org.illegaller.ratabb.hishoot2i.ui.fragment;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.di.ir.TemplateUsedID;
import org.illegaller.ratabb.hishoot2i.model.pref.StringPreference;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.ui.adapter.TemplateRecyclerAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;


public class ListTemplateFragment extends BaseFragment {

    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Inject @TemplateUsedID StringPreference templateUsedIdTray;
    TemplateRecyclerAdapter mAdapter;

    public ListTemplateFragment() {
    }

    public static ListTemplateFragment newInstance() {
        Bundle args = new Bundle();
        ListTemplateFragment fragment = new ListTemplateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setupSearchView(menu.findItem(R.id.action_search));
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupSearchView(MenuItem item) {
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("template name or author");

    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                                 @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_template, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setLayoutManager(new GridLayoutManager(weakActivity.get(), 2));
        mRecyclerView.setHasFixedSize(true);
        new LoadAdapter().execute();
    }

    class LoadAdapter extends AsyncTask<Void, Void, List<Template>> {

        @Override protected List<Template> doInBackground(Void... voids) {
            TemplateProvider templateProvider = new TemplateProvider(weakActivity.get());
            return templateProvider.asList();
        }

        @Override protected void onPostExecute(List<Template> templates) {
            super.onPostExecute(templates);
            mAdapter = new TemplateRecyclerAdapter(templates, templateUsedIdTray.get());
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
