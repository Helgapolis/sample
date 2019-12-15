package com.kastapp.sample.ui.common.controller

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker

class ActionModeController : ActionMode.Callback {

    @MenuRes
    private var menuRes: Int? = null
    private var actionMode: ActionMode? = null
    private var tracker: SelectionTracker<*>? = null
    private var onActionItemClicked: ((id: Int) -> Unit)? = null

    fun show(
        activity: AppCompatActivity,
        @MenuRes menuRes: Int,
        onActionItemClicked: (id: Int) -> Unit = {},
        tracker: SelectionTracker<*>? = null
    ) {
        this.tracker = tracker
        this.menuRes = menuRes
        this.onActionItemClicked = onActionItemClicked

        tracker?.let {
            if (it.selection.size() > 0) {
                actionMode = actionMode ?: activity.startSupportActionMode(this)
                actionMode!!.title = it.selection.size().toString()
            } else {
                actionMode?.finish()
            }
        }
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        onActionItemClicked!!(item.itemId)
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(menuRes!!, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = true

    override fun onDestroyActionMode(mode: ActionMode) {
        tracker?.clearSelection()
        tracker = null
        actionMode = null
        menuRes = null
        onActionItemClicked = null
    }

    fun finish() {
        actionMode?.finish()
    }
}
