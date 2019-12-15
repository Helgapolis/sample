package com.kastapp.sample.ui.common.controller

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kastapp.sample.R
import com.kastapp.sample.ui.common.ext.createSnackBar
import com.kastapp.sample.ui.common.ext.settings

const val REQUEST_PERMISSION = 65535
const val REQUEST_SETTINGS = 65534

interface PermissionHandler {
    fun askPermissions(permissions: Array<String>, requestCode: Int)
    fun verifyPermission(permission: String): Int
    fun isNeedPermissionRationale(permission: String): Boolean
    fun isPermissionDeniedForever(permission: String): Boolean
    fun startActivityForResult(intent: Intent?, requestCode: Int)
    fun getRootView(): View
}

class PermissionRequest(
    val handler: PermissionHandler,
    val permissions: Set<String>,
    @StringRes val rationaleMsg: Int? = null,
    @StringRes val deniedForeverMsg: Int,
    val onGranted: () -> Unit,
    val onDenied: () -> Unit = {}
) {
    var id = 0
}

class PermissionController : LifecycleObserver {

    private lateinit var request: PermissionRequest
    private lateinit var permissions: List<PermissionRequest>
    private var rationaleDialog: AlertDialog? = null

    fun registerPermissions(vararg permissions: PermissionRequest) {
        if (permissions.isNotEmpty()) {
            var incrementId = 0
            permissions.forEach { permission ->
                permission.id = ++incrementId
            }
        }
        this.permissions = permissions.toList()
    }

    fun check(request: PermissionRequest) {
        this.request = request

        if (!hasPermissions()) {
            if (hasPermissionRationale()) {
                request.rationaleMsg?.let { msg ->
                    val context = request.handler.getRootView().context
                    rationaleDialog = MaterialAlertDialogBuilder(context)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.btn_understand)) { dialog, _ ->
                            dialog.dismiss()
                            askPermissions()
                        }.show()
                } ?: askPermissions()
            } else {
                askPermissions()
            }
        } else {
            request.onGranted()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int) {
        if (requestCode == REQUEST_PERMISSION) {
            if (hasPermissions()) {
                request.onGranted()
            } else {
                if (hasForeverDenied()) {
                    val root = request.handler.getRootView()
                    val context = root.context
                    root.createSnackBar(
                        context.getString(request.deniedForeverMsg),
                        false,
                        context.getString(R.string.btn_settings),
                        View.OnClickListener {
                            request.handler.startActivityForResult(
                                Intent().settings(context),
                                REQUEST_SETTINGS
                            )
                        })
                } else request.onDenied()
            }
        }
    }

    fun onActivityResult(requestCode: Int) {
        if (requestCode == REQUEST_SETTINGS) {
            if (hasPermissions()) {
                request.onGranted()
            }
        }
    }

    fun onRestoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { bundle ->
            val id = bundle.getInt(::PermissionRequest.name)
            if (id > 0) {
                request = permissions.first { it.id == id }
            }
        }
    }

    fun onSaveState(outState: Bundle) {
        if (::request.isInitialized) {
            outState.putInt(::PermissionRequest.name, request.id)
        }
    }

    private fun askPermissions() {
        request.handler.askPermissions(
            request.permissions.toTypedArray(),
            REQUEST_PERMISSION
        )
    }

    private fun hasPermissions(): Boolean = request.permissions.all {
        request.handler.verifyPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasPermissionRationale(): Boolean = request.permissions.any {
        request.handler.isNeedPermissionRationale(it)
    }

    private fun hasForeverDenied(): Boolean = request.permissions.any {
        request.handler.isPermissionDeniedForever(it)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        rationaleDialog?.dismiss()
    }
}
