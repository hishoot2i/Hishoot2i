package org.illegaller.ratabb.hishoot2i.ui.adapter;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.ui.navigation.BusProvider;
import org.illegaller.ratabb.hishoot2i.ui.navigation.EventHishoot;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TemplateRecyclerAdapter extends RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder>
        implements Filterable {

    private final List<Template> filteredList = new ArrayList<>();
    private final List<Template> sourceList;
    private final String usedTemplateID;

    public TemplateRecyclerAdapter(List<Template> templateList, String usedTemplateID) {
        this.sourceList = templateList;
        this.filteredList.addAll(templateList);
        this.usedTemplateID = usedTemplateID;
    }

    public void setFiltered(List<Template> filtered) {
        this.filteredList.clear();
        this.filteredList.addAll(filtered);
        notifyDataSetChanged();
    }

    public Template getTemplate(int position) {
        return filteredList.get(position);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindTo(getTemplate(position));
    }

    @Override public int getItemCount() {
        return filteredList.size();
    }

    @Override public Filter getFilter() {
        return new TemplateFiltered(this, sourceList);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvTemplateName) TextView templateName;
        @Bind(R.id.tvTemplateAuthor) TextView templateAuthor;
        @Bind(R.id.ivTemplate) ImageView templatePreview;
        @Bind(R.id.ivUsing) ImageView templateUse;

        public ViewHolder(ViewGroup parent) {
            super(Utils.inflateViewHolder(parent, R.layout.item_list_template));
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    final String templateID = getTemplate(getAdapterPosition()).id;
                    BusProvider.getInstance().post(new EventHishoot.EventSetTemplateUse(templateID));
                }
            });
        }

        public void bindTo(final Template template) {
            templateName.setText(template.name);
            templateAuthor.setText(template.author);

            UILHelper.displayPreview(templatePreview, template.previewFile);
            templateUse.setVisibility(template.id.equalsIgnoreCase(usedTemplateID) ?
                    View.VISIBLE : View.GONE);

        }
    }
}
