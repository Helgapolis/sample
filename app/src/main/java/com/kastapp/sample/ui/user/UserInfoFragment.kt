package com.kastapp.sample.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.kastapp.sample.R
import com.kastapp.sample.data.model.UserInfo
import com.kastapp.sample.databinding.FragmentUserInfoBinding
import com.kastapp.sample.ui.common.AbsFragment
import com.kastapp.sample.ui.common.EventObserver
import com.kastapp.sample.ui.common.adapter.KeyValueAdapter
import com.kastapp.sample.ui.common.dialog.BirthdayDatePickerDialog
import com.kastapp.sample.ui.common.ext.*
import com.kastapp.sample.util.Dates
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserInfoFragment : AbsFragment() {

    private lateinit var binding: FragmentUserInfoBinding
    private val userInfoViewModel: UserInfoViewModel by viewModel()
    private val sexKeyValArr by lazy {
        createKeyValueListFromRes(
            R.array.key_sex,
            R.array.value_sex
        )
    }
    private val hobbyKeyValArr by lazy {
        createKeyValueListFromRes(
            R.array.key_hobby,
            R.array.value_hobby
        )
    }

    private val onBirthdaySelected =
        MaterialPickerOnPositiveButtonClickListener<Long> { selection ->
            binding.info?.birthday = selection
            binding.birthday.requiredEditText.setText(Dates.toSimpleDate(selection))
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false).apply {
            hobbyArr = hobbyKeyValArr
            sexArr = sexKeyValArr
        }

        binding.sex.requiredAutoCompleteTextView.apply {
            setAdapter(KeyValueAdapter(sexKeyValArr))
            setOnItemClickListener { _, _, position, _ ->
                binding.info!!.sex = sexKeyValArr[position].getKey()
            }
        }

        binding.hobby.requiredAutoCompleteTextView.apply {
            setAdapter(KeyValueAdapter(hobbyKeyValArr))
            setOnItemClickListener { _, _, position, _ ->
                binding.info!!.hobby = hobbyKeyValArr[position].getKey()
            }
        }

        binding.birthday.setSafeEndIconOnClickListener({
            BirthdayDatePickerDialog.create(
                childFragmentManager,
                onBirthdaySelected,
                binding.info!!.birthday
            )
        }, 2000)

        binding.btnSave.setOnClickListener {
            binding.apply {
                if (phone.isValid()
                        .and(firstName.isValid())
                        .and(lastName.isValid())
                        .and(middleName.isValid())
                        .and(birthday.isValid())
                        .and(sex.isValid())
                ) {
                    userInfoViewModel.updateUserInfoAsync(reBuildUserInfo()!!)
                }
            }
        }

        binding.error.btnRepeat.setOnClickListener {
            userInfoViewModel.getUserInfoAsync()
        }

        savedInstanceState?.let {
            BirthdayDatePickerDialog.onRestoreState(childFragmentManager, onBirthdaySelected)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userInfoViewModel.getUserInfoEvent().observe(
            viewLifecycleOwner, EventObserver({
                binding.info = it
                binding.executePendingBindings()
                binding.content.visible()
                binding.progress.root.gone()
                binding.error.root.gone()
            }, {
                binding.content.gone()
                binding.progress.root.gone()
                binding.error.root.visible()
                binding.error.msg.text = getMsgFromError(it)
            }, {
                binding.content.gone()
                binding.progress.root.visible()
                binding.error.root.gone()
            })
        )

        userInfoViewModel.updateUserInfoEvent().observe(
            viewLifecycleOwner, EventObserver({
                binding.btnSave.hideProgress()
                showSnackBar(R.string.text_popup_data_saved)
            }, {
                binding.btnSave.hideProgress()
                showSnackBar(it)
            }, {
                binding.btnSave.showProgress()
            })
        )
    }

    override fun onPause() {
        hideSoftKeyboard()
        super.onPause()
    }

    override fun onStop() {
        reBuildUserInfo()
        super.onStop()
    }

    private fun reBuildUserInfo(): UserInfo? {
        return binding.info?.apply {
            phone = binding.phone.unformattedText
            firstName = binding.firstName.unformattedText
            lastName = binding.lastName.unformattedText
            middleName = binding.middleName.unformattedText
            birthday = Dates.simpleDateToLong(binding.birthday.unformattedText)
        }
    }
}
