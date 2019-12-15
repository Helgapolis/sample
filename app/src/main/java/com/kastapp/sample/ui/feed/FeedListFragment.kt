package com.kastapp.sample.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.MutableSelection
import androidx.recyclerview.selection.SelectionTracker
import com.kastapp.sample.R
import com.kastapp.sample.data.model.Feed
import com.kastapp.sample.databinding.FragmentFeedListBinding
import com.kastapp.sample.ui.common.AbsFragment
import com.kastapp.sample.ui.common.EventObserver
import com.kastapp.sample.ui.common.adapter.LoadMoreState
import com.kastapp.sample.ui.common.controller.ActionModeController
import com.kastapp.sample.ui.common.ext.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedListFragment : AbsFragment() {

    private lateinit var binding: FragmentFeedListBinding
    private lateinit var feedAdapter: FeedAdapter
    private val feedViewModel: FeedListViewModel by viewModel()
    private val actionModeController = ActionModeController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)

        feedAdapter = FeedAdapter(
            requireContext(), binding.list, onRetryLoadMore = {
                feedViewModel.retryLoadMoreFeed()
            }, onOpenActionMode = ::showActionMode
        )

        feedAdapter.onItemClick = { feed, shared ->
            val activity = requireActivity()
            val pairs = activity.createSafeTransitionParticipants(true, *shared)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs).toBundle()!!
            open<FeedItemActivity>(
                bundleOf(FeedItemActivity.EXTRA_FEED to feed),
                options
            )
        }

        binding.refresh.setOnRefreshListener { feedViewModel.checkNewFeedsAsync() }
        binding.error.btnRepeat.setOnClickListener { feedViewModel.retryLoadFeed() }

        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        feedAdapter.selectionTracker.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        feedAdapter.selectionTracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        feedViewModel.loadFeedMoreEvent().observe(
            viewLifecycleOwner, EventObserver({
                feedAdapter.setLoadMoreState(LoadMoreState.Idle)
            }, {
                feedAdapter.setLoadMoreState(LoadMoreState.Error(getMsgFromError(it)))
            }, {
                feedAdapter.setLoadMoreState(LoadMoreState.Loading)
            })
        )

        feedViewModel.loadFeedInitEvent().observe(
            viewLifecycleOwner, EventObserver({
                binding.empty.root.visibleOrGone(it)
                binding.error.root.gone()
                binding.refresh.hide()
            }, {
                binding.empty.root.gone()
                binding.error.root.visible()
                binding.error.msg.text = getMsgFromError(it)
                binding.refresh.hide()
            }, {
                binding.empty.root.gone()
                binding.error.root.gone()
                binding.refresh.show()
            })
        )

        feedViewModel.deleteFeedsEvent().observe(
            viewLifecycleOwner, EventObserver({
                progressDialog.dismiss()
                actionModeController.finish()
                showSnackBar(R.string.text_popup_removed)
            }, {
                progressDialog.dismiss()
                showSnackBar(it)
            }, {
                progressDialog.show()
            })
        )

        feedViewModel.checkNewFeedsEvent().observe(
            viewLifecycleOwner, EventObserver({
                binding.refresh.hide()
            }, {
                binding.refresh.hide()
                showSnackBar(it)
            }, {
                binding.refresh.show()
            })
        )

        feedViewModel.feedPagedList.observe(viewLifecycleOwner, Observer {
            feedAdapter.submitList(it)
        })
    }

    private fun showActionMode(tracker: SelectionTracker<Feed>) {
        actionModeController.show(activity as AppCompatActivity, R.menu.news_action, { menuId ->
            when (menuId) {
                R.id.action_delete -> {
                    val snapshot = MutableSelection<Feed>()
                    tracker.copySelection(snapshot)
                    val ids = snapshot.map { it.id }
                    feedViewModel.deleteFeedsAsync(ids)
                }
            }
        }, tracker)
    }
}
