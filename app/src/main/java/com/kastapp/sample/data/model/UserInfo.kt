package com.kastapp.sample.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    var phone: String,
    var sex: Int?,
    var hobby: Int?,
    var birthday: Long?,

    @SerializedName("first_name")
    var firstName: String,

    @SerializedName("last_name")
    var lastName: String,

    @SerializedName("middle_name")
    var middleName: String
) : Parcelable
