package com.kastapp.sample.data.local.prefs

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import java.lang.reflect.Type

abstract class AbsPreferences(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()

    protected fun putString(key: String, value: String) =
        preferences.edit().putString(key, value).apply()

    protected fun getString(key: String): String? = preferences.getString(key, "")

    protected fun putBoolean(key: String, value: Boolean) =
        preferences.edit().putBoolean(key, value).apply()

    protected fun getBoolean(key: String, defValue: Boolean): Boolean =
        preferences.getBoolean(key, defValue)

    protected fun putInt(key: String, value: Int) =
        preferences.edit().putInt(key, value).apply()

    protected fun getInt(key: String, defValue: Int = -1): Int =
        preferences.getInt(key, defValue)

    protected fun putLong(key: String, value: Long) =
        preferences.edit().putLong(key, value).apply()

    protected fun getLong(key: String, defValue: Long = -1): Long =
        preferences.getLong(key, defValue)

    protected fun removeValue(key: String) = preferences.edit().remove(key).apply()

    protected fun clear() = preferences.edit().clear().apply()

    protected fun <T> putObject(key: String, value: T) =
        preferences.edit().putString(key, gson.toJson(value)).apply()

    protected fun <T> getObjectValue(key: String, classType: Class<T>): T =
        gson.fromJson(preferences.getString(key, null), classType)

    protected fun <T> getObjectValue(key: String, type: Type): T =
        gson.fromJson(preferences.getString(key, null), type)
}
