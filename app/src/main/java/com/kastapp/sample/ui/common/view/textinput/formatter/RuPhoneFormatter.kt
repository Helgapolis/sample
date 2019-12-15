package com.kastapp.sample.ui.common.view.textinput.formatter

class RuPhoneFormatter : DigitsFormatter() {

    override fun generateDividersMap(): Map<Int, String> {
        return mapOf(0 to " (", 3 to ") ", 6 to "-", 8 to "-")
    }

    override fun getUnformattedText(str: String): String {
        return str.trim().replace(Regex("[^\\d]"), "")
    }
}
