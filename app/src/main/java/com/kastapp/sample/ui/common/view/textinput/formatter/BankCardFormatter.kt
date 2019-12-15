package com.kastapp.sample.ui.common.view.textinput.formatter

class BankCardFormatter : DigitsFormatter() {

    override fun generateDividersMap(): Map<Int, String> {
        return mapOf(4 to " ", 8 to " ", 12 to " ")
    }

    override fun getUnformattedText(str: String): String {
        return str.trim().replace(Regex("[^\\d]"), "")
    }
}
