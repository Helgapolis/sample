package com.kastapp.sample.ui.permission

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kastapp.sample.R
import com.kastapp.sample.databinding.FragmentPermissionBinding
import com.kastapp.sample.ui.common.AbsFragment
import com.kastapp.sample.ui.common.controller.PermissionRequest
import com.kastapp.sample.ui.common.ext.showSnackBar

class PermissionsFragment : AbsFragment() {

    private lateinit var binding: FragmentPermissionBinding

    private val locationPermissionRequest = PermissionRequest(this,
        setOf(Manifest.permission.ACCESS_FINE_LOCATION),
        R.string.text_popup_permission_location_rationale,
        R.string.text_popup_permission_location_forever_denied, {
            showSnackBar("Получил Разрешение на местоположение!")
        }, {
            showSnackBar("Не получил разрешение на местоположение :(")
        })

    private val cameraPermissionRequest = PermissionRequest(this,
        setOf(Manifest.permission.CAMERA),
        R.string.text_popup_permission_camera_rationale,
        R.string.text_popup_permission_camera_forever_denied, {
            showSnackBar("Получил Разрешение на камеру!")
        }, {
            showSnackBar("Не получил разрешение на камеру :(")
        })

    init {
        permission.registerPermissions(locationPermissionRequest, cameraPermissionRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)

        binding.btnCheckLocation.setOnClickListener {
            permission.check(locationPermissionRequest)
        }

        binding.btnCheckCamera.setOnClickListener {
            permission.check(cameraPermissionRequest)
        }

        return binding.root
    }
}
