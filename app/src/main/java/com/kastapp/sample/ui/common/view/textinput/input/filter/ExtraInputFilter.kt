package com.kastapp.sample.ui.common.view.textinput.input.filter

import android.text.InputFilter
import android.text.Spanned
import androidx.annotation.StringRes

abstract class ExtraInputFilter : InputFilter {

    var onShowError: ((resId: Int) -> Unit) = {
    }

    protected abstract fun isValidChar(c: Char): Boolean

    @StringRes
    protected abstract fun getErrorId(): Int

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var needKeepOriginal = true
        val sb = StringBuilder(end - start)
        for (i in start until end) {
            val c: Char = source[i]
            if (isValidChar(c))
                sb.append(c)
            else {
                needKeepOriginal = false
                onShowError(getErrorId())
            }
        }
        return if (needKeepOriginal) null else sb
    }
}
