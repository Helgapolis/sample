package com.kastapp.sample.ui.common.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import com.kastapp.sample.R
import com.kastapp.sample.data.model.KeyValue

fun Context.showLongToast(@StringRes msg: Int) =
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.showToast(@StringRes msg: Int) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(msg: CharSequence) =
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.showToast(msg: CharSequence) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

@Suppress("DEPRECATION")
fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService<ConnectivityManager>()!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = cm.activeNetwork ?: return false
        val actNw = cm.getNetworkCapabilities(nw) ?: return false

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val networkInfo = cm.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}

fun Context.findActivity(): Activity? {
    if (this is Activity) return this
    return if (this is ContextWrapper) this.baseContext.findActivity() else null
}

fun Context.lockUi() {
    findActivity()!!.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Context.isLockedUi(): Boolean {
    val flags = findActivity()!!.window.attributes.flags
    return (flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) != 0
}

fun Context.unlockUi() {
    findActivity()!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Context.isSafeIntent(intent: Intent): Boolean {
    return this.packageManager.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    ).isNotEmpty()
}

fun Context.checkIntentAndDo(intent: Intent, action: () -> Unit) {
    if (isSafeIntent(intent))
        action()
    else
        showLongToast(R.string.error_open_intent)
}

fun Context.open(vararg intents: Intent) {
    if (intents.isNotEmpty()) {
        intents.forEach {
            if (isSafeIntent(it)) {
                startActivity(it)
                return
            }
        }
        showLongToast(R.string.error_open_intent)
    }
    throw IllegalArgumentException("intents are empty")
}

inline fun <reified T : Any> Context.open(bundle: Bundle? = null, options: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
    }
    checkIntentAndDo(intent) { startActivity(intent, options) }
}

fun Context.createKeyValueListFromRes(@ArrayRes keyRes: Int, @ArrayRes valueRes: Int): List<KeyValue<Int>> {
    val keys = resources.getIntArray(keyRes)
    val values = resources.getStringArray(valueRes)
    val list = mutableListOf<KeyValue<Int>>()
    for (i in keys.indices) {
        list.add(KeyValue.create(keys[i], values[i]))
    }
    return list.toList()
}
