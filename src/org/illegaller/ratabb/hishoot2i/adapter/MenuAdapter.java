/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.illegaller.ratabb.hishoot2i.adapter;

import org.illegaller.ratabb.hishoot2i.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
	private String[] mDataset;
	private OnItemClickListener mListener;

	public MenuAdapter(String[] dataSet, OnItemClickListener listener) {
		mDataset = dataSet;
		mListener = listener;
	}

	public interface OnItemClickListener {
		public void onClick(View view, int position);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public final TextView mTextView;

		public ViewHolder(TextView v) {
			super(v);
			mTextView = v;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater li = LayoutInflater.from(parent.getContext());
		View v = li.inflate(R.layout.drawer_list_item, parent, false);
		TextView tv = (TextView) v.findViewById(android.R.id.text1);
		return new ViewHolder(tv);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.mTextView.setText(mDataset[position]);
		holder.mTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onClick(v, position);
			}
		});

	}

	@Override
	public int getItemCount() {
		return mDataset.length;
	}

}
