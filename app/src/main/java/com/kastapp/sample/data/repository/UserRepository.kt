package com.kastapp.sample.data.repository

import com.kastapp.sample.data.model.UserInfo
import com.kastapp.sample.data.local.prefs.Preferences
import com.kastapp.sample.data.remote.Endpoints
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UserRepository(
    private val api: Endpoints,
    private val preferences: Preferences
) {

    private var cachedUserInfo: UserInfo? = null

    fun getUserInfo(): Single<UserInfo> {
        return Maybe.fromCallable<UserInfo> {
            cachedUserInfo?.copy()
        }.subscribeOn(Schedulers.io())
            .switchIfEmpty(api.getUserInfo().map {
                cachedUserInfo = it.getResultOrThrow()
                cachedUserInfo?.copy()
            })
    }

    fun sendUserInfo(userInfo: UserInfo): Single<Any> {
        return api.sendUserInfo(userInfo)
            .subscribeOn(Schedulers.io())
            .map {
                it.getResultOrThrow()
                cachedUserInfo = userInfo
            }
    }
}
