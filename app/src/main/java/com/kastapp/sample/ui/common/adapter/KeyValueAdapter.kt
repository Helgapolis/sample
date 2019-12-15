package com.kastapp.sample.ui.common.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kastapp.sample.data.model.KeyValue
import com.kastapp.sample.databinding.ItemKeyValueBinding

/**
 * @param isFilterable true если поле ввода редактируемо
 */
class KeyValueAdapter(
    private val initList: List<KeyValue<*>>,
    private val isFilterable: Boolean = false
) : AbsAdapter<KeyValue<*>, KeyValueHolder>(initList),
    Filterable {

    override fun createHolder(parent: ViewGroup?): KeyValueHolder {
        return KeyValueHolder(
            ItemKeyValueBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
        )
    }

    override fun bind(holder: KeyValueHolder, position: Int) {
        val keyValueTag = getItem(position)
        holder.binding.text.text = keyValueTag.getValue()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any?): CharSequence {
                return resultValue?.let { (it as KeyValue<*>).getValue() } ?: ""
            }

            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val result = FilterResults()
                result.count = initList.size
                result.values = initList

                if (isFilterable) {
                    constraint?.let { str ->
                        val filteredArr = items.filter {
                            str.toString().toLowerCase() == it.getValue().toLowerCase()
                        }
                        result.values = filteredArr
                        result.count = filteredArr.size
                    }
                }

                return result
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                updateData(results.values as List<KeyValue<*>>)
            }
        }
    }
}

class KeyValueHolder(val binding: ItemKeyValueBinding) : DefaultHolder(binding.root)
