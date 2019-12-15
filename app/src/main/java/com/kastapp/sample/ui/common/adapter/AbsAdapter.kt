package com.kastapp.sample.ui.common.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter

abstract class AbsAdapter<Item, H : DefaultHolder?> : BaseAdapter, SpinnerAdapter {
    protected val items = mutableListOf<Item>()

    constructor()

    constructor(items: List<Item>) {
        this.items.addAll(items)
    }

    protected abstract fun createHolder(parent: ViewGroup?): H

    protected abstract fun bind(holder: H, position: Int)

    override fun getCount(): Int {
        return this.items.size
    }

    override fun getItem(position: Int): Item {
        return this.items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, _container: View?, parent: ViewGroup): View {
        var container = _container
        val h: H
        if (container == null) {
            h = createHolder(parent)
            container = h!!.view
            container.tag = h
        } else h = container.tag as H
        bind(h, position)
        return container
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    fun updateData(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}

abstract class DefaultHolder constructor(var view: View)
