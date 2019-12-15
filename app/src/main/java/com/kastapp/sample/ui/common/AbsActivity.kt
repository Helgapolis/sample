package com.kastapp.sample.ui.common

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kastapp.sample.ui.common.controller.PermissionHandler
import com.kastapp.sample.ui.common.controller.PermissionController
import com.kastapp.sample.ui.common.dialog.ScreenProgressDialog
import com.kastapp.sample.ui.common.ext.findRootLayout
import com.kastapp.sample.ui.common.ext.isLockedUi

abstract class AbsActivity : AppCompatActivity(), PermissionHandler {

    protected val permission = PermissionController()
    protected val progressDialog by lazy { ScreenProgressDialog.create(this) }

    final override fun askPermissions(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    final override fun verifyPermission(permission: String): Int {
        return ContextCompat.checkSelfPermission(this, permission)
    }

    final override fun isNeedPermissionRationale(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
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
        return findRootLayout()
    }

    final override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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

    override fun onSupportNavigateUp(): Boolean {
        supportFinishAfterTransition()
        return true
    }

    override fun onBackPressed() {
        if (!isLockedUi()) super.onBackPressed()
    }

    override fun onDestroy() {
        progressDialog.dismiss()
        super.onDestroy()
    }
}
