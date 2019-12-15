package com.kastapp.sample.data.local.db.dao

import androidx.room.*
import com.kastapp.sample.data.model.Feed
import io.reactivex.Maybe

import io.reactivex.Single

@Dao
abstract class FeedDao : BaseDao<Feed>() {
    @Query("SELECT id FROM Feed ORDER BY id DESC LIMIT 1")
    abstract fun getLastId(): Maybe<Long>

    @Query("SELECT * FROM Feed ORDER BY id DESC")
    abstract fun getAll(): Single<List<Feed>>

    @Query("SELECT * FROM Feed ORDER BY id DESC LIMIT :limit")
    abstract fun getAll(limit: Int): Single<List<Feed>>

    @Query(
        """
        SELECT * FROM (
            SELECT * FROM Feed WHERE id>=:id ORDER BY id ASC LIMIT 1
        ) 
        UNION ALL 
        SELECT * FROM (
            SELECT * FROM Feed WHERE id <:id ORDER BY id DESC LIMIT :limit
        ) ORDER BY id DESC
        """
    )
    abstract fun getAroundId(id: Long, limit: Int): Single<List<Feed>>

    @Query("SELECT * FROM Feed WHERE id<:id ORDER BY id DESC LIMIT :limit")
    abstract fun getAfterId(id: Long, limit: Int): Single<List<Feed>>

    @Query("SELECT * FROM Feed WHERE id>:id ORDER BY id DESC LIMIT :limit")
    abstract fun getBeforeId(id: Long, limit: Int): Single<List<Feed>>

    @Query("SELECT count(*) FROM Feed")
    abstract fun getTotalCount(): Single<Int>

    @Query("SELECT * FROM Feed WHERE id=:id")
    abstract fun findById(id: Long): Single<Feed>

    @Query("SELECT * FROM Feed WHERE id IN (:ids)")
    abstract fun findByIds(ids: List<Long>): Single<List<Feed>>

    @Query("DELETE FROM Feed")
    abstract fun deleteAll()

    @Query("DELETE FROM Feed WHERE id IN (:ids)")
    abstract fun deleteByIds(ids: List<Long>)
}
