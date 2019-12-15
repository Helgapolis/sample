package com.kastapp.sample.data.remote.response

import com.google.gson.annotations.SerializedName
import com.kastapp.sample.data.ServerException

data class ApiResponse<T>(
    val data: T,
    @SerializedName("error_code")
    val errorCode: Int = 0,
    @SerializedName("error_name")
    val errorName: String = ""
) {

    fun getResultOrThrow(): T {
        return if (errorCode == 0) {
            data
        } else {
            throw ServerException(errorCode, errorName)
        }
    }
}
