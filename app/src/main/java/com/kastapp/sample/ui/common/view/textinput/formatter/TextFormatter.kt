package com.kastapp.sample.ui.common.view.textinput.formatter

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher

abstract class TextFormatter : TextWatcher {

    private var isLocked: Boolean = false
    private var isBackspace: Boolean = false
    private var isCopyPast: Boolean = false

    abstract fun getUnformattedText(str: String): String
    abstract fun formatText(txt: String): String
    protected abstract fun fixCursorPos(remCursorPos: Int, editable: Editable)

    override fun afterTextChanged(s: Editable?) {
        if (isLocked) return
        isLocked = true
        if (!isBackspace) {
            s?.apply {
                val remCursorPos = Selection.getSelectionStart(this)
                val remStr = this.toString()
                clear()
                append(formatText(remStr))
                fixCursorPos(remCursorPos, this)
            }
        }
        isLocked = false
    }

    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        removedCount: Int,
        insertedCount: Int
    ) {
        if (!isLocked) {
            isBackspace = removedCount > 0 && insertedCount == 0
            isCopyPast = insertedCount > 1
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    protected fun isCopyPast() = isCopyPast
}
