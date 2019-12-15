package com.kastapp.sample.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kastapp.sample.data.model.Feed
import com.kastapp.sample.data.repository.FeedRepository
import com.kastapp.sample.ui.common.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

private const val PAGE_SIZE = 4

class FeedListViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {
    private var onRetryLoadFeed: (() -> Unit)? = null
    private var onRetryLoadMoreFeed: (() -> Unit)? = null
    private var isPullRefresh: Boolean = false

    private val loadFeedInitEvent = MutableLiveData<Event<Boolean>>()
    fun loadFeedInitEvent(): LiveData<Event<Boolean>> = loadFeedInitEvent

    private val loadFeedMoreEvent = MutableLiveData<Event<Unit>>()
    fun loadFeedMoreEvent(): LiveData<Event<Unit>> = loadFeedMoreEvent

    private val deleteFeedsEvent = MutableLiveData<Event<Unit>>()
    fun deleteFeedsEvent(): LiveData<Event<Unit>> = deleteFeedsEvent

    private val checkNewFeedsEvent = MutableLiveData<Event<Unit>>()
    fun checkNewFeedsEvent(): LiveData<Event<Unit>> = checkNewFeedsEvent

    val feedPagedList: LiveData<PagedList<Feed>>

    init {
        val feedDataFactory = object : DataSource.Factory<Long, Feed>() {
            override fun create(): DataSource<Long, Feed> {

                return object : ItemKeyedDataSource<Long, Feed>() {
                    // при invalidate в requestedInitialKey придет id "возле" которого нужно загружать элементы
                    override fun loadInitial(
                        params: LoadInitialParams<Long>,
                        callback: LoadInitialCallback<Feed>
                    ) {
                        Timber.d("loadInitial=${params.requestedInitialKey} ${params.requestedLoadSize}")
                        onRetryLoadFeed = { loadInitial(params, callback) }
                        val key = if (isPullRefresh) null else params.requestedInitialKey
                        feedRepository.getFeedList(key, params.requestedLoadSize)
                            .doOnSubscribe { loadFeedInitEvent.postValue(Event.Loading) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : DisposableSingleObserver<List<Feed>>() {
                                override fun onSuccess(result: List<Feed>) {
                                    onRetryLoadFeed = null
                                    isPullRefresh = false
                                    callback.onResult(result)
                                    loadFeedInitEvent.value = Event.Success(result.isEmpty())
                                }

                                override fun onError(e: Throwable) {
                                    loadFeedInitEvent.value = Event.Error(e)
                                }
                            })
                    }

                    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Feed>) {
                        Timber.d("loadAfter=${params.key} ${params.requestedLoadSize}")
                        onRetryLoadMoreFeed = { loadAfter(params, callback) }
                        feedRepository.getFeedListAfterKey(params.key, params.requestedLoadSize)
                            .doOnSubscribe { loadFeedMoreEvent.postValue(Event.Loading) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : DisposableSingleObserver<List<Feed>>() {
                                override fun onSuccess(result: List<Feed>) {
                                    callback.onResult(result)
                                    loadFeedMoreEvent.value = Event.Success(Unit)
                                }

                                override fun onError(e: Throwable) {
                                    loadFeedMoreEvent.value = Event.Error(e)
                                }
                            })
                    }

                    override fun loadBefore(
                        params: LoadParams<Long>,
                        callback: LoadCallback<Feed>
                    ) {
                        Timber.d("loadBefore=${params.key} ${params.requestedLoadSize}")
                        feedRepository.getFeedListBeforeKey(params.key, params.requestedLoadSize)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : DisposableSingleObserver<List<Feed>>() {
                                override fun onSuccess(result: List<Feed>) {
                                    callback.onResult(result)
                                }

                                override fun onError(e: Throwable) {
                                }
                            })
                    }

                    override fun getKey(item: Feed): Long {
                        return item.id
                    }
                }
            }
        }

        feedPagedList = LivePagedListBuilder<Long, Feed>(
            feedDataFactory,
            PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .build()
        ).build()
    }

    fun deleteFeedsAsync(ids: List<Long>) {
        feedRepository.deleteFeeds(ids)
            .doOnSubscribe { deleteFeedsEvent.postValue(Event.Loading) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<Unit>() {
                override fun onSuccess(result: Unit) {
                    deleteFeedsEvent.value = Event.SingleSuccess(Unit)
                    invalidateFeedList(false)
                }

                override fun onError(e: Throwable) {
                    deleteFeedsEvent.value = Event.SingleError(e)
                }
            })
    }

    fun checkNewFeedsAsync() {
        onRetryLoadFeed?.invoke() ?: feedRepository.checkNewFeedList()
            .doOnSubscribe { checkNewFeedsEvent.postValue(Event.Loading) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<Feed>>() {
                override fun onSuccess(result: List<Feed>) {
                    checkNewFeedsEvent.value = Event.SingleSuccess(Unit)
                    if (result.isNotEmpty()) invalidateFeedList(true)
                }

                override fun onError(e: Throwable) {
                    checkNewFeedsEvent.value = Event.SingleError(e)
                }
            })
    }

    private fun invalidateFeedList(isPullRefresh: Boolean) {
        this.isPullRefresh = isPullRefresh
        feedPagedList.value!!.dataSource.invalidate()
    }

    fun retryLoadFeed() {
        onRetryLoadFeed?.invoke()
    }

    fun retryLoadMoreFeed() {
        onRetryLoadMoreFeed?.invoke()
    }
}
