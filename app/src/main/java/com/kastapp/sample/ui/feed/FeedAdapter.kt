package com.kastapp.sample.ui.feed

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.paging.PagedList
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kastapp.sample.R
import com.kastapp.sample.data.model.Feed
import com.kastapp.sample.databinding.ItemFeedBinding
import com.kastapp.sample.ui.common.adapter.AbsLoadMoreAdapter
import com.kastapp.sample.ui.common.adapter.LoadMoreState
import com.kastapp.sample.ui.common.ext.setSafeOnClickListener

class FeedAdapter(
    val context: Context,
    recyclerView: RecyclerView,
    onRetryLoadMore: () -> Unit,
    onOpenActionMode: (tracker: SelectionTracker<Feed>) -> Unit
) : AbsLoadMoreAdapter<Feed, FeedAdapter.FeedHolder>(
    context, onRetryLoadMore, diffCallback
) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem == newItem
            }
        }
    }

    val selectionTracker: SelectionTracker<Feed>
    var onItemClick: (feed: Feed, shared: Array<Pair<View, String>>) -> Unit = { _, _ -> }

    init {
        setHasStableIds(true)
        recyclerView.adapter = this
        selectionTracker = SelectionTracker.Builder<Feed>(
            ::FeedAdapter.name, recyclerView,
            createKeyProviderForSelector(), createDetailsLookupForSelector(recyclerView),
            StorageStrategy.createParcelableStorage(Feed::class.java)
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
            .also { tracker ->
                tracker.addObserver(object : SelectionTracker.SelectionObserver<Feed>() {
                    override fun onSelectionChanged() {
                        onOpenActionMode(tracker)
                    }

                    override fun onSelectionRestored() {
                        onOpenActionMode(tracker)
                    }
                })
            }
    }

    override fun submitList(pagedList: PagedList<Feed>?) {
        setLoadMoreState(LoadMoreState.Idle)
        super.submitList(pagedList)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        return FeedHolder(ItemFeedBinding.inflate(inflater, parent, false))
    }

    override fun onBindHolder(holder: FeedHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.apply {
                binding.title.transitionName = FeedItemActivity.TRANSITION_TITLE + "-${item.id}"
                binding.image.transitionName = FeedItemActivity.TRANSITION_IMAGE + "-${item.id}"
                binding.card.transitionName = FeedItemActivity.TRANSITION_CARD + "-${item.id}"
                binding.title.text = item.title
                binding.card.isChecked = selectionTracker.isSelected(getItemDetails().selectionKey)
                Glide.with(binding.image)
                    .load(item.image)
                    .dontTransform()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            }
        }
    }

    override fun getItemType(position: Int): Int {
        return R.layout.item_feed
    }

    override fun getItemUniqueId(position: Int): Long {
        return getItem(position)?.id ?: RecyclerView.NO_ID
    }

    private fun createKeyProviderForSelector() = object : ItemKeyProvider<Feed?>(SCOPE_MAPPED) {
        override fun getKey(position: Int): Feed? = this@FeedAdapter.getItem(position)
        override fun getPosition(key: Feed): Int =
            this@FeedAdapter.currentList?.indexOf(key) ?: RecyclerView.NO_POSITION
    }

    private fun createDetailsLookupForSelector(recyclerView: RecyclerView) =
        object : ItemDetailsLookup<Feed>() {
            override fun getItemDetails(e: MotionEvent) = recyclerView.findChildViewUnder(e.x, e.y)
                ?.let { (recyclerView.getChildViewHolder(it) as? FeedHolder)?.getItemDetails() }
        }

    inner class FeedHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {

        /*
        *  setOnLongClickListener нужен чтобы не вызывался setSafeOnClickListener при долгом нажатии.
        *  Т.к. у нас при долгом нажатии включается селект мод
        * */
        init {
            binding.card.setSafeOnClickListener({
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val item = getItem(pos)!!
                    val shared = arrayOf(
                        Pair.create<View, String>(
                            binding.image,
                            FeedItemActivity.TRANSITION_IMAGE + "-${item.id}"
                        ),
                        Pair.create<View, String>(
                            binding.title,
                            FeedItemActivity.TRANSITION_TITLE + "-${item.id}"
                        ),
                        Pair.create<View, String>(
                            binding.card,
                            FeedItemActivity.TRANSITION_CARD + "-${item.id}"
                        )
                    )
                    onItemClick(item, shared)
                }
            })
            binding.card.setOnLongClickListener {
                true
            }
        }

        fun getItemDetails() = object : ItemDetailsLookup.ItemDetails<Feed>() {
            override fun getSelectionKey() = getItem(adapterPosition)
            override fun getPosition() = adapterPosition
        }
    }
}
