package com.kastapp.sample.ui.common.ext

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kastapp.sample.data.model.KeyValue

fun MaterialAlertDialogBuilder.setSingleChoiceItems(
    items: List<KeyValue<*>>,
    checkedItem: KeyValue<*>,
    listener: ((keyValueTag: KeyValue<*>) -> Unit)
): MaterialAlertDialogBuilder {
    val array = mutableListOf<CharSequence>()
    var checkedPos = 0
    items.forEachIndexed { index, keyValue ->
        if (keyValue == checkedItem)
            checkedPos = index
        array.add(keyValue.getValue())
    }
    return this.setSingleChoiceItems(array.toTypedArray(), checkedPos) { _, which ->
        listener(items[which])
    }
}

fun MaterialAlertDialogBuilder.setMultiChoiceItems(
    items: List<KeyValue<*>>,
    checkedItems: Array<KeyValue<*>>,
    listener: ((keyValueTag: KeyValue<*>, isChecked: Boolean) -> Unit)
): MaterialAlertDialogBuilder {
    val array = mutableListOf<CharSequence>()
    val checkedArray = mutableListOf<Boolean>()
    items.forEach { keyValue ->
        checkedArray.add(checkedItems.contains(keyValue))
        array.add(keyValue.getValue())
    }
    return this.setMultiChoiceItems(array.toTypedArray(), checkedArray.toBooleanArray()) { _, which, isChecked ->
        listener(items[which], isChecked)
    }
}
