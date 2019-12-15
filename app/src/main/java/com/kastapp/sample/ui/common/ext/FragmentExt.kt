package com.kastapp.sample.ui.common.ext

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kastapp.sample.R
import com.kastapp.sample.data.model.KeyValue
import com.kastapp.sample.ui.common.Event

fun Fragment.showSnackBar(msg: CharSequence) = view!!.createSnackBar(msg)

fun Fragment.showSnackBar(@StringRes msg: Int) = view!!.createSnackBar(getString(msg))

fun Fragment.showSnackBarWithRepeat(error: Event.Error, action: View.OnClickListener) =
    view!!.createSnackBar(
        getMsgFromError(error),
        true,
        getString(R.string.btn_repeat),
        action
    )

fun Fragment.showSnackBar(error: Event.Error) {
    showSnackBar(getMsgFromError(error))
}

fun Fragment.getMsgFromError(error: Event.Error): String {
    return requireActivity().getMsgFromError(error)
}

inline fun <reified T : Any> Fragment.open(bundle: Bundle? = null, options: Bundle? = null) {
    val intent = Intent(context, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
    }
    requireContext().checkIntentAndDo(intent) { startActivity(intent, options) }
}

inline fun <reified T : Any> Fragment.open(requestCode: Int, bundle: Bundle? = null) {
    val intent = Intent(context, T::class.java)
    requireContext().checkIntentAndDo(intent) {
        startActivityForResult(
            intent,
            requestCode,
            bundle
        )
    }
}

fun Fragment.open(vararg intents: Intent) {
    requireContext().open(*intents)
}

fun Fragment.open(intent: Intent, requestCode: Int) {
    requireContext().checkIntentAndDo(intent) { startActivityForResult(intent, requestCode) }
}

fun Fragment.hideSoftKeyboard() = activity?.apply { hideSoftKeyboard() }

fun Fragment.createKeyValueListFromRes(@ArrayRes keyRes: Int, @ArrayRes valueRes: Int): List<KeyValue<Int>> {
    return requireContext().createKeyValueListFromRes(keyRes, valueRes)
}
