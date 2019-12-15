package com.kastapp.sample.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Feed(
    @PrimaryKey
    val id: Long,
    val title: String,
    val text: String,
    val image: String
) : Parcelable
