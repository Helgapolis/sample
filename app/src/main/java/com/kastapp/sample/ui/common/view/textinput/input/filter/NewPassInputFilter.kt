package com.kastapp.sample.ui.common.view.textinput.input.filter

import com.kastapp.sample.R

class NewPassInputFilter : ExtraInputFilter() {

    companion object {
        private const val WRONG_DIGITS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя "
    }

    override fun getErrorId(): Int = R.string.hint_field_pass_symbols

    override fun isValidChar(c: Char): Boolean {
        return !WRONG_DIGITS.contains(c.toString())
    }
}
