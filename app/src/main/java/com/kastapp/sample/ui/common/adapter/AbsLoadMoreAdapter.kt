package com.kastapp.sample.ui.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kastapp.sample.R
import com.kastapp.sample.databinding.ItemLoadMoreBinding
import com.kastapp.sample.ui.common.ext.invisible
import com.kastapp.sample.ui.common.ext.visible

abstract class AbsLoadMoreAdapter<T, VH : RecyclerView.ViewHolder>(
    private val context: Context,
    private val onRetryLoadMore: () -> Unit,
    config: DiffUtil.ItemCallback<T>
) :
    PagedListAdapter<T, VH>(config) {

    private var loadMoreState: LoadMoreState = LoadMoreState.Idle
    protected val inflater: LayoutInflater = LayoutInflater.from(context)

    abstract fun onCreateHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindHolder(holder: VH, position: Int)
    abstract fun getItemType(position: Int): Int
    abstract fun getItemUniqueId(position: Int): Long

    @Suppress("UNCHECKED_CAST")
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when (viewType) {
            R.layout.item_load_more -> LoadMoreHolder(
                ItemLoadMoreBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onRetryLoadMore
            ) as VH
            else -> onCreateHolder(parent, viewType)
        }
    }

    final override fun onBindViewHolder(holder: VH, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_load_more -> (holder as LoadMoreHolder).bind(loadMoreState)
            else -> onBindHolder(holder, position)
        }
    }

    final override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_load_more
        } else {
            getItemType(position)
        }
    }

    final override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    final override fun getItemId(position: Int): Long {
        if (getItemViewType(position) == R.layout.item_load_more) {
            return R.layout.item_load_more.toLong()
        }
        return getItemUniqueId(position)
    }

    fun setLoadMoreState(state: LoadMoreState) {
        if (currentList?.size != 0) {
            val previousState = loadMoreState
            val hadExtraRow = hasExtraRow()
            loadMoreState = state
            val hasExtraRow = hasExtraRow()
            if (hadExtraRow != hasExtraRow) {
                if (hadExtraRow) {
                    notifyItemRemoved(super.getItemCount())
                } else {
                    notifyItemInserted(super.getItemCount())
                }
            } else if (hasExtraRow && previousState !== state) {
                notifyItemChanged(itemCount - 1)
            }
        }
    }

    private fun hasExtraRow(): Boolean {
        return loadMoreState !is LoadMoreState.Idle
    }
}

sealed class LoadMoreState {
    object Idle : LoadMoreState()
    object Loading : LoadMoreState()
    class Error(val msg: String) : LoadMoreState()
}

class LoadMoreHolder(
    private val binding: ItemLoadMoreBinding,
    private val onRetryLoadMore: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRepeat.setOnClickListener { onRetryLoadMore() }
    }

    fun bind(state: LoadMoreState) {

        when (state) {
            LoadMoreState.Loading -> {
                binding.apply {
                    error.invisible()
                    btnRepeat.invisible()
                    progress.visible()
                }
            }
            is LoadMoreState.Error -> {
                binding.apply {
                    error.visible()
                    btnRepeat.visible()
                    progress.invisible()
                    error.text = state.msg
                }
            }
        }
    }
}
