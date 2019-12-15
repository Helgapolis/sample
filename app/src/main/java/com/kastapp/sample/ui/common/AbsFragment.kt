package com.kastapp.sample.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kastapp.sample.ui.common.controller.PermissionHandler
import com.kastapp.sample.ui.common.controller.PermissionController
import com.kastapp.sample.ui.common.dialog.ScreenProgressDialog

abstract class AbsFragment : Fragment(), PermissionHandler {

    protected val permission = PermissionController()
    protected val progressDialog by lazy { ScreenProgressDialog.create(requireContext()) }

    final override fun askPermissions(permissions: Array<String>, requestCode: Int) {
        requestPermissions(permissions, requestCode)
    }

    final override fun verifyPermission(permission: String): Int {
        return ContextCompat.checkSelfPermission(context!!, permission)
    }

    final override fun isNeedPermissionRationale(permission: String): Boolean {
        return shouldShowRequestPermissionRationale(permission)
    }

    final override fun isPermissionDeniedForever(permission: String): Boolean {
        return !isNeedPermissionRationale(permission)
    }

    final override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permission.onRequestPermissionsResult(requestCode)
    }

    final override fun getRootView(): View {
        return view!!
    }

    final override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(permission)
        permission.onRestoreState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permission.onActivityResult(requestCode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        permission.onSaveState(outState)
    }

    override fun onDestroyView() {
        progressDialog.dismiss()
        super.onDestroyView()
    }
}
