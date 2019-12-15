package com.kastapp.sample.ui.common.ext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.annotation.TransitionRes
import androidx.core.util.Pair
import com.kastapp.sample.R
import com.kastapp.sample.data.NoNetworkException
import com.kastapp.sample.data.ServerException
import com.kastapp.sample.ui.common.Event

fun Activity.hideSoftKeyboard() = currentFocus?.apply { hideSoftKeyboard() }

fun Activity.open(intent: Intent, requestCode: Int) {
    checkIntentAndDo(intent) { startActivityForResult(intent, requestCode) }
}

inline fun <reified T : Any> Activity.open(requestCode: Int, bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    checkIntentAndDo(intent) { startActivityForResult(intent, requestCode, bundle) }
}

fun Activity.showSnackBar(error: Event.Error) {
    showSnackBar(getMsgFromError(error))
}

fun Activity.showSnackBarWithRepeat(error: Event.Error, action: View.OnClickListener) {
    val root = findRootLayout()
    root.createSnackBar(
        getMsgFromError(error),
        true,
        getString(R.string.btn_repeat),
        action
    )
}

fun Activity.getMsgFromError(error: Event.Error): String {
    return error.msg ?: error.exception!!.let {
        when (it) {
            is ServerException -> it.message!!
            is NoNetworkException -> getString(R.string.error_check_network)
            else -> getString(R.string.error_try_later)
        }
    }
}

fun Activity.showSnackBar(@StringRes msg: Int) = findRootLayout().createSnackBar(getString(msg))
fun Activity.showSnackBar(msg: CharSequence) = findRootLayout().createSnackBar(msg)
fun Activity.findRootLayout(): View = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)

fun Activity.createTransition(@TransitionRes resource: Int): Transition {
    return TransitionInflater.from(this).inflateTransition(resource)
}

fun Activity.createSafeTransitionParticipants(
    includeStatusBar: Boolean,
    vararg others: Pair<View, String>
): Array<Pair<View, String>> {

    fun addViewById(
        @IdRes viewId: Int,
        participants: ArrayList<Pair<View, String>>
    ) {
        val view = window.decorView.findViewById<View>(viewId)
        view?.transitionName?.let { participants.add(Pair(view, it)) }
    }

    return ArrayList<Pair<View, String>>(3).apply {
        if (includeStatusBar) {
            addViewById(android.R.id.statusBarBackground, this)
        }
        addViewById(android.R.id.navigationBarBackground, this)
        addAll(others.toList())
    }.toTypedArray()
}
