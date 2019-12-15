package com.kastapp.sample.data.remote

import com.kastapp.sample.data.model.UserInfo
import com.kastapp.sample.data.remote.response.ApiResponse
import com.kastapp.sample.data.remote.response.FeedListResponse
import io.reactivex.Single
import retrofit2.http.*

interface Endpoints {
    @GET("feed/list/init")
    fun getFeeds(@Query("size") size: Int): Single<ApiResponse<FeedListResponse>>

    @GET("feed/list/after")
    fun getMoreFeeds(@Query("key") key: Long?, @Query("size") size: Int): Single<ApiResponse<FeedListResponse>>

    @GET("feed/list/new")
    fun getNewFeeds(@Query("key") key: Long? = null): Single<ApiResponse<FeedListResponse>>

    @FormUrlEncoded
    @POST("feed/delete")
    fun deleteFeeds(@Field("keys[]") keys: List<Long>): Single<ApiResponse<Unit>>

    @GET("user/info")
    fun getUserInfo(): Single<ApiResponse<UserInfo>>

    @POST("user/info")
    fun sendUserInfo(@Body userInfo: UserInfo): Single<ApiResponse<Unit>>
}
