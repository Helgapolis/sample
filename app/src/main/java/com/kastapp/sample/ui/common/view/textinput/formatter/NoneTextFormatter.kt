package com.kastapp.sample.ui.common.view.textinput.formatter

import android.text.Editable

class NoneTextFormatter : TextFormatter() {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun fixCursorPos(remCursorPos: Int, editable: Editable) {
    }

    override fun getUnformattedText(str: String): String {
        return str
    }

    override fun formatText(txt: String): String {
        return txt
    }
}
