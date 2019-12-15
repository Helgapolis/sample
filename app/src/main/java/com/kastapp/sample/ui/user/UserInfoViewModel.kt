package com.kastapp.sample.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kastapp.sample.data.model.UserInfo
import com.kastapp.sample.data.repository.UserRepository
import com.kastapp.sample.ui.common.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver

class UserInfoViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val updateUserInfoEvent = MutableLiveData<Event<Unit>>()
    fun updateUserInfoEvent(): LiveData<Event<Unit>> = updateUserInfoEvent

    private val getUserInfoEvent = MutableLiveData<Event<UserInfo>>()
    fun getUserInfoEvent(): LiveData<Event<UserInfo>> = getUserInfoEvent

    init {
        getUserInfoAsync()
    }

    fun updateUserInfoAsync(userInfo: UserInfo) {
        userRepository.sendUserInfo(userInfo)
            .doOnSubscribe { updateUserInfoEvent.postValue(Event.Loading) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<Any>() {
                override fun onSuccess(result: Any) {
                    updateUserInfoEvent.value = Event.SingleSuccess(Unit)
                }

                override fun onError(e: Throwable) {
                    updateUserInfoEvent.value = Event.SingleError(e)
                }
            })
    }

    fun getUserInfoAsync() {
        userRepository.getUserInfo()
            .doOnSubscribe { getUserInfoEvent.postValue(Event.Loading) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<UserInfo>() {
                override fun onSuccess(result: UserInfo) {
                    getUserInfoEvent.value = Event.Success(result)
                }

                override fun onError(e: Throwable) {
                    getUserInfoEvent.value = Event.Error(e)
                }
            })
    }
}
