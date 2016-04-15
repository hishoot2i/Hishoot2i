package org.illegaller.ratabb.hishoot2i.view.adapter;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.widget.ItemListTemplate;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {

    private final List<Template> mTemplateList;
    private final List<String> favList;
    private final StringTray usedTemplateIdTray;

    public TemplateAdapter(List<Template> mTemplateList, List<String> favList, StringTray usedTemplateIdTray) {
        this.mTemplateList = new ArrayList<>(mTemplateList);
        this.favList = new ArrayList<>(favList);
        this.usedTemplateIdTray = usedTemplateIdTray;
    }

    public void animateTo(List<Template> templates) {
        applyAndAnimateRemovals(templates);
        applyAndAnimateAdditions(templates);
        applyAndAnimateMovedItems(templates);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_template, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItemListTemplate.bindTo(getItem(position), favList, usedTemplateIdTray.get());
    }

    @Override public int getItemCount() {
        return mTemplateList.size();
    }

    Template getItem(int position) {
        return mTemplateList.get(position);
    }

    Template removeItem(int position) {
        final Template template = mTemplateList.remove(position);
        notifyItemRemoved(position);
        return template;
    }

    void applyAndAnimateRemovals(List<Template> newList) {
        for (int i = mTemplateList.size() - 1; i >= 0; i--) {
            final Template template = mTemplateList.get(i);
            if (!newList.contains(template)) removeItem(i);
        }
    }

    void applyAndAnimateAdditions(List<Template> newList) {
        for (int i = 0; i < newList.size(); i++) {
            final Template template = newList.get(i);
            if (!mTemplateList.contains(template)) addItem(i, template);
        }
    }

    void applyAndAnimateMovedItems(List<Template> newList) {
        for (int to = newList.size() - 1; to >= 0; to--) {
            final Template model = newList.get(to);
            final int from = mTemplateList.indexOf(model);
            if (from >= 0 && from != to) moveItem(from, to);
        }
    }


    void addItem(int position, Template template) {
        mTemplateList.add(position, template);
        notifyItemInserted(position);
    }

    void moveItem(int from, int to) {
        final Template template = mTemplateList.remove(from);
        mTemplateList.add(to, template);
        notifyItemMoved(from, to);
    }


    ///////////////////////*ViewHolder*//////////////////////////////
    public class ViewHolder extends AnimateViewHolder {
        @Bind(R.id.item_template) ItemListTemplate mItemListTemplate;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    Template template = getItem(getAdapterPosition());
                    usedTemplateIdTray.set(template.id);
                    MainActivity.start(itemView.getContext(), template);
                }
            });
           /* itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View view) {
                    mItemListTemplate.doExpand();
                    return true;
                }
            });*/
        }

        @Override public void preAnimateRemoveImpl() {
            ViewCompat.setTranslationY(itemView, 1);
            ViewCompat.setAlpha(itemView, 1);
        }

        @Override
        public void preAnimateAddImpl() {
            ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
            ViewCompat.setAlpha(itemView, 0);
        }

        @Override
        public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView)
                    .translationY(-itemView.getHeight() * 0.3f)
                    .alpha(0)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }

        @Override
        public void animateAddImpl(ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView)
                    .translationY(0)
                    .alpha(1)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }
    }
}
