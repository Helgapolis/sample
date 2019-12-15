package com.kastapp.sample.ui.common.ext

import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar

fun View.visible() {
    visibility = VISIBLE
}

fun View.invisible() {
    visibility = INVISIBLE
}

fun View.gone() {
    visibility = GONE
}

fun View.visibleOrInvisible(visible: Boolean) {
    visibility = if (visible) VISIBLE else INVISIBLE
}

fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit, interval: Int = 1000) {
    setOnClickListener(object : View.OnClickListener {
        private var lastTimeClicked: Long = 0

        override fun onClick(v: View) {
            val currentTimeClicked = SystemClock.elapsedRealtime()
            if (currentTimeClicked - lastTimeClicked < interval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeClick(v)
        }
    })
}

fun View.hideSoftKeyboard() {
    val imm = context.getSystemService<InputMethodManager>()!!
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.createSnackBar(
    msg: CharSequence,
    isIndefinite: Boolean = false,
    actionBtn: CharSequence? = null,
    action: OnClickListener? = null
) {
    val snackBar = Snackbar.make(
        this,
        msg,
        if (isIndefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG
    )
    snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
        maxLines = 3
        textAlignment = TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER_HORIZONTAL
    }
    snackBar.setAction(actionBtn, action).show()
}
