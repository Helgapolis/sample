package com.kastapp.sample.ui.common.view.textinput.formatter

import android.text.Editable
import android.text.Selection

abstract class DigitsFormatter : TextFormatter() {

    abstract fun generateDividersMap(): Map<Int, String>

    override fun fixCursorPos(remCursorPos: Int, editable: Editable) {
        if (!isCopyPast()) {
            var index = remCursorPos

            for (i in remCursorPos until editable.length) {
                val previousChar = editable[i - 1]
                if (Character.isDigit(previousChar)) {
                    break
                } else {
                    index++
                }
            }

            Selection.setSelection(editable, index)
        }
    }

    override fun getUnformattedText(str: String): String {
        return str.trim()
    }

    override fun formatText(txt: String): String {
        val str = txt.trim().replace(Regex("[^\\d]"), "")

        val strBuilder = StringBuilder()
        str.forEachIndexed { index, char ->
            val divider = generateDividersMap().getOrElse(index, { "" })
            strBuilder.apply {
                append(divider).append(char)
            }
        }

        return strBuilder.toString()
    }
}
