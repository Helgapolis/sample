package com.kastapp.sample.data.repository

import com.kastapp.sample.data.model.Feed
import com.kastapp.sample.data.local.db.dao.FeedDao
import com.kastapp.sample.data.local.prefs.Preferences
import com.kastapp.sample.data.remote.Endpoints
import com.kastapp.sample.data.remote.response.ApiResponse
import com.kastapp.sample.data.remote.response.FeedListResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FeedRepository(
    private val api: Endpoints,
    private val feedDao: FeedDao,
    private val preferences: Preferences
) {

    fun getFeedList(key: Long?, size: Int): Single<List<Feed>> {
        return (key?.let { feedDao.getAroundId(key, size) } ?: feedDao.getAll(size))
            .subscribeOn(Schedulers.io())
            .flatMap { feedList ->
                if (feedList.isEmpty()) {
                    api.getFeeds(size).map { insertFeedResultIntoDb(it) }
                } else {
                    Single.just(feedList)
                }
            }
    }

    fun getFeedListAfterKey(key: Long, size: Int): Single<List<Feed>> {
        return feedDao.getAfterId(key, size)
            .subscribeOn(Schedulers.io())
            .flatMap { feedList ->
                if (feedList.isEmpty()) {
                    api.getMoreFeeds(key, size).map { insertFeedResultIntoDb(it) }
                } else {
                    Single.just(feedList)
                }
            }
    }

    private fun insertFeedResultIntoDb(response: ApiResponse<FeedListResponse>): List<Feed> {
        val feeds = response.getResultOrThrow().feeds
        feedDao.insert(feeds)
        return feeds
    }

    fun checkNewFeedList(): Single<List<Feed>> {
        return feedDao.getLastId()
            .subscribeOn(Schedulers.io())
            .flatMapSingleElement {
                api.getNewFeeds(it)
            }
            .switchIfEmpty(api.getNewFeeds())
            .map { response ->
                val feeds = response.getResultOrThrow().feeds
                feedDao.upsert(feeds)
                feeds
            }
    }

    fun deleteFeeds(keys: List<Long>): Single<Unit> {
        return api.deleteFeeds(keys)
            .subscribeOn(Schedulers.io())
            .map {
                it.getResultOrThrow()
                feedDao.deleteByIds(keys)
            }
    }

    fun getFeedListBeforeKey(key: Long, size: Int): Single<List<Feed>> {
        return feedDao.getBeforeId(key, size)
            .subscribeOn(Schedulers.io())
    }
}
