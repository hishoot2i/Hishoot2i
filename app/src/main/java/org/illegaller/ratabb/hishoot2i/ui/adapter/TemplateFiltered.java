package org.illegaller.ratabb.hishoot2i.ui.adapter;

import org.illegaller.ratabb.hishoot2i.model.template.Template;

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class TemplateFiltered extends Filter {
    private final TemplateRecyclerAdapter mAdapter;
    private final List<Template> templateList;
    private final List<Template> filteredList;

    public TemplateFiltered(TemplateRecyclerAdapter adapter, List<Template> templateList) {
        this.mAdapter = adapter;
        this.templateList = new LinkedList<>(templateList);
        this.filteredList = new ArrayList<>();
    }

    @Override protected FilterResults performFiltering(CharSequence charSequence) {
        filteredList.clear();
        final FilterResults results = new FilterResults();
        if (charSequence.length() == 0 || TextUtils.isEmpty(charSequence))
            filteredList.addAll(templateList);
        else {
            final String filterPattern = charSequence.toString().toLowerCase(Locale.US).trim();
            for (final Template template : templateList) {
                if (template.name.toLowerCase(Locale.US).contains(filterPattern)) filteredList.add(template);
                else if (template.author.toLowerCase(Locale.US).contains(filterPattern))
                    filteredList.add(template);
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        mAdapter.setFiltered((List<Template>) filterResults.values);
    }
}
