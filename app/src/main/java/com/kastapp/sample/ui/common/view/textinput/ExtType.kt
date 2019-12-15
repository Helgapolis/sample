package com.kastapp.sample.ui.common.view.textinput

import android.view.ViewGroup
import com.kastapp.sample.R
import com.kastapp.sample.ui.common.ext.findViewsByClass
import com.kastapp.sample.ui.common.view.textinput.formatter.*
import com.kastapp.sample.ui.common.view.textinput.input.filter.CyrillicNameInputFilter
import com.kastapp.sample.ui.common.view.textinput.input.filter.ExtraInputFilter
import com.kastapp.sample.ui.common.view.textinput.input.filter.NewPassInputFilter

enum class ExtType(
    val validator: TextValidator,
    val inputFilter: ExtraInputFilter? = null,
    val formatter: TextFormatter = NoneTextFormatter()
) {
    TEXT(
        TextValidator()
    ),
    BANK_CARD(
        TextValidator(TextValidator.PATTERN_FOUR_DIGITS_CARD, R.string.error_field_wrong_bank_card),
        null,
        BankCardFormatter()
    ),
    EMAIL(
        TextValidator(TextValidator.PATTERN_EMAIL, R.string.error_field_wrong_email)
    ),
    CODE(
        TextValidator(TextValidator.PATTERN_CODE, R.string.error_field_wrong_code)
    ),
    PASS(
        TextValidator(TextValidator.PATTERN_PASS, R.string.error_field_pass_min_length)
    ),
    NEW_PASS(
        TextValidator { textInput ->
            if (textInput.unformattedText.length < 6)
                return@TextValidator R.string.error_field_pass_min_length
            null
        },
        NewPassInputFilter()
    ),
    REPEATED_NEW_PASS(
        TextValidator { textInput ->
            if (textInput.unformattedText.length < 6)
                return@TextValidator R.string.error_field_pass_min_length
            val parent = textInput.parent as ViewGroup
            parent.findViewsByClass(MaterialTextInputLayout::class).forEach {
                if (it.extType == NEW_PASS && it.unformattedText != textInput.unformattedText)
                    return@TextValidator R.string.error_field_pass_mismatch
            }
            null
        },
        NewPassInputFilter()
    ),
    SIMPLE_DATE(
        TextValidator(TextValidator.PATTERN_SIMPLE_DATE, R.string.error_field_wrong_simple_date),
        null,
        SimpleDateFormatter()
    ),
    NAME(
        TextValidator(TextValidator.PATTERN_NAME, R.string.error_field_wrong_name),
        CyrillicNameInputFilter()
    ),
    PHONE(
        TextValidator(TextValidator.PATTERN_PHONE, R.string.error_field_wrong_phone),
        null,
        RuPhoneFormatter()
    )
}
