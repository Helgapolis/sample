package com.kastapp.sample.ui.common.view.textinput.input.filter

import com.kastapp.sample.R

class CyrillicNameInputFilter : ExtraInputFilter() {

    companion object {
        private const val DIGITS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя -"
    }

    override fun getErrorId(): Int = R.string.hint_field_name_symbols

    override fun isValidChar(c: Char): Boolean {
        return DIGITS.contains(c.toString())
    }
}
