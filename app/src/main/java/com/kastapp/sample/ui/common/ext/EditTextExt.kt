package com.kastapp.sample.ui.common.ext

import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService

fun EditText.showSoftKeyboard() {
    val imm = context.getSystemService<InputMethodManager>()!!
    postDelayed({ imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT) }, 50)
}
