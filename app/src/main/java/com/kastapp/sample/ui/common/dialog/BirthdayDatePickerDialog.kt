package com.kastapp.sample.ui.common.dialog

import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.kastapp.sample.R
import com.kastapp.sample.ui.common.ext.invisible
import kotlinx.android.parcel.Parcelize

object BirthdayDatePickerDialog {

    fun create(
        fragmentManager: FragmentManager,
        onSelected: MaterialPickerOnPositiveButtonClickListener<Long>,
        selection: Long? = null
    ) {
        val constraintsBuilder = CalendarConstraints.Builder().apply {
            setOpenAt(selection?.let { it } ?: MaterialDatePicker.todayInUtcMilliseconds())
            setValidator(BirthdayDateValidator())
        }

        val pickerBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setSelection(selection)
            setTitleText(R.string.label_popup_choose_birthday)
            setCalendarConstraints(constraintsBuilder.build())
        }

        pickerBuilder.build().apply {
            showNow(fragmentManager, BirthdayDatePickerDialog.javaClass.simpleName)
            setListener(this, onSelected)
        }
    }

    private fun setListener(
        datePicker: MaterialDatePicker<Long>,
        onSelected: MaterialPickerOnPositiveButtonClickListener<Long>
    ) {
        datePicker.view?.findViewById<View>(R.id.mtrl_picker_header_toggle)?.invisible()
        datePicker.addOnPositiveButtonClickListener(onSelected)
    }

    @Suppress("UNCHECKED_CAST")
    fun onRestoreState(
        fragmentManager: FragmentManager,
        onSelected: MaterialPickerOnPositiveButtonClickListener<Long>
    ) {
        fragmentManager.findFragmentByTag(BirthdayDatePickerDialog.javaClass.simpleName)?.let {
            setListener(it as MaterialDatePicker<Long>, onSelected)
        }
    }
}

@Parcelize
class BirthdayDateValidator : DateValidator {
    override fun isValid(date: Long): Boolean {
        return date < System.currentTimeMillis()
    }
}
