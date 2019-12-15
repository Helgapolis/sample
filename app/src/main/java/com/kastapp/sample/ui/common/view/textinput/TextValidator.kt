package com.kastapp.sample.ui.common.view.textinput

import androidx.annotation.StringRes
import com.kastapp.sample.R

class TextValidator(
    private val pattern: Regex? = null,
    @StringRes private val error: Int = 0,
    private val action: ((inputLayout: MaterialTextInputLayout) -> Int?)? = null
) {
    companion object {
        val PATTERN_FOUR_DIGITS_CARD = Regex("^[0-9]{4} [0-9\\*]{4} [0-9\\*]{4} [0-9\\*]{4,}$")
        val PATTERN_CODE = Regex("^[0-9]{4}$")
        val PATTERN_PASS = Regex("^.{6,}$")
        val PATTERN_NAME = Regex("^.{2,}$")
        val PATTERN_PHONE = Regex("^[0-9]{10}$")
        val PATTERN_EMAIL = Regex(
            "^[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+$"
        )
        val PATTERN_SIMPLE_DATE = Regex(
            "^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*$"
        )
    }

    fun valid(inputLayout: MaterialTextInputLayout, required: Boolean): Int? {
        if (inputLayout.formattedText.isBlank() && required) {
            return R.string.error_field_no_blank
        } else {
            action?.let { action ->
                return action(inputLayout)
            } ?: if (pattern?.matches(inputLayout.unformattedText) == false) {
                return error
            }
        }

        return null
    }
}
