package com.kastapp.sample.ui.common.ext

import android.os.SystemClock
import android.view.View
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.setSafeEndIconOnClickListener(
    onSafeClick: (View) -> Unit,
    interval: Int = 1000
) {
    setEndIconOnClickListener(object : View.OnClickListener {
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
