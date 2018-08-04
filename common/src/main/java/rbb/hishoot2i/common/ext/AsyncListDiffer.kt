package rbb.hishoot2i.common.ext

import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

fun <T> RecyclerView.Adapter<*>.asyncListDiffer(
    diffCallback: DiffUtil.ItemCallback<T>
): AsyncListDiffer<T> = AsyncListDiffer<T>(this, diffCallback)