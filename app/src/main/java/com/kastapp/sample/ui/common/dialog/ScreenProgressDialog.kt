package com.kastapp.sample.ui.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.kastapp.sample.R

object ScreenProgressDialog {

    fun create(context: Context): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setView(R.layout.layout_progress)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
