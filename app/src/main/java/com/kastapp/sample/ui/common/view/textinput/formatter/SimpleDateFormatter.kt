package com.kastapp.sample.ui.common.view.textinput.formatter

class SimpleDateFormatter : DigitsFormatter() {

    override fun generateDividersMap(): Map<Int, String> {
        return mapOf(2 to ".", 4 to ".")
    }
}
