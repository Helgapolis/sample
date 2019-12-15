package com.kastapp.sample.ui.common.view.textinput

import android.content.Context
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.kastapp.sample.R
import com.kastapp.sample.ui.common.ext.showToast

class MaterialTextInputLayout : TextInputLayout {

    private var resetErrorTextWatcher: TextWatcher? = null

    var extType: ExtType? = null
        private set

    var isRequiredField: Boolean = false

    val unformattedText: String
        get() {
            val str = requiredEditText.text.toString()
            return extType?.formatter?.getUnformattedText(str) ?: str
        }

    val formattedText: String
        get() = requiredEditText.text.toString()

    val requiredEditText: EditText
        get() = editText!!

    val requiredAutoCompleteTextView: AutoCompleteTextView
        get() = editText!! as AutoCompleteTextView

    constructor(context: Context) : super(context) {
        initAttr()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttr(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttr(attrs)
    }

    private fun initAttr(attrs: AttributeSet? = null) {
        attrs?.let {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MaterialTextInputLayout,
                0,
                0
            )
            val type = a.getInt(R.styleable.MaterialTextInputLayout_extType, -1)
            extType = ExtType.values().getOrNull(type)
            isRequiredField = a.getBoolean(
                R.styleable.MaterialTextInputLayout_required,
                false
            )
            a.recycle()
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)

        if (child is EditText) {
            extType?.inputFilter?.let { filter ->
                filter.onShowError = { context.showToast(it) }
                child.filters = arrayOf<InputFilter>(*child.filters, filter)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        extType?.formatter.apply { requiredEditText.addTextChangedListener(this) }
        resetErrorTextWatcher = requiredEditText.doAfterTextChanged { resetError() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        extType?.formatter.apply {
            requiredEditText.removeTextChangedListener(this)
        }
        requiredEditText.removeTextChangedListener(resetErrorTextWatcher)
    }

    fun isValid(): Boolean {
        extType?.validator?.valid(this, isRequiredField)?.let {
            error = context.getString(it)
            return false
        }
        return true
    }

    private fun resetError() {
        if (error != null) error = null
    }
}
