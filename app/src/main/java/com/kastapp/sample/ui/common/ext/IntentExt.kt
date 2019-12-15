package com.kastapp.sample.ui.common.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri

fun Intent.settings(context: Context): Intent {
    val appPackageName = context.packageName
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.parse("package:$appPackageName")
    return this
}

fun Intent.email(address: String, subject: String = "", text: String = ""): Intent {
    action = Intent.ACTION_SENDTO
    data = Uri.parse(String.format("mailto:%s", address))
    if (subject.isNotBlank()) putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotBlank()) putExtra(Intent.EXTRA_TEXT, text)
    return this
}

fun Intent.browse(_url: String): Intent {
    var url = _url
    if (!url.startsWith("https://") && !url.startsWith("http://")) {
        url = "http://$url"
    }
    action = Intent.ACTION_VIEW
    data = url.toUri()
    return this
}

fun Intent.sms(number: String, message: String = ""): Intent {
    action = Intent.ACTION_SENDTO
    data = "smsto:$number".toUri()
    putExtra("sms_body", message)
    return this
}

fun Intent.dial(number: String): Intent {
    action = Intent.ACTION_DIAL
    data = "tel:$number".toUri()
    return this
}

fun Intent.call(number: String): Intent {
    action = Intent.ACTION_CALL
    data = "tel:$number".toUri()
    return this
}

fun Intent.share(subject: String, message: String): Intent {
    action = Intent.ACTION_SEND
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, message)
    putExtra(Intent.EXTRA_SUBJECT, subject)
    return this
}

fun Intent.map(lat: Double, lng: Double): Intent {
    action = Intent.ACTION_VIEW
    data = "geo:$lat,$lng".toUri()
    return this
}

fun Intent.map(lat: Double, lng: Double, zoom: Int): Intent {
    action = Intent.ACTION_VIEW
    data = "geo:$lat,$lng?z=$zoom".toUri()
    return this
}

fun Intent.playMarket(context: Context): Array<Intent> {
    val appPackageName = context.packageName
    return arrayOf(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$appPackageName")
        ), Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
        )
    )
}

fun Intent.singleTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
fun Intent.noHistory(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
fun Intent.noAnimation(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) }
fun Intent.newTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
fun Intent.multipleTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK) }
fun Intent.newDocument(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT) }
fun Intent.clearTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }
fun Intent.excludeFromRecents(): Intent = apply {
    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
}
