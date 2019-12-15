package com.kastapp.sample.ui.common.adapter

import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import com.kastapp.sample.data.model.KeyValue
import com.kastapp.sample.data.model.findByKey
import com.kastapp.sample.ui.common.view.textinput.MaterialTextInputLayout
import com.kastapp.sample.util.Dates

@BindingAdapter("simpleDate")
fun bindSimpleDate(textInput: MaterialTextInputLayout, text: Long?) =
    textInput.requiredEditText.setText(Dates.toSimpleDate(text))

@BindingAdapter("keyValArr", "key", requireAll = true)
fun bindKeyValArr(textView: AutoCompleteTextView, keyValArr: List<KeyValue<Int>>, id: Int?) {
    id ?: return
    textView.setText(keyValArr.findByKey(id)?.getValue(), false)
}

@BindingAdapter("formattedText")
fun bindFormattedText(textInput: MaterialTextInputLayout, str: String?) {
    val formattedText = textInput.extType?.formatter?.formatText(str ?: "")
    textInput.requiredEditText.setText(formattedText)
}
