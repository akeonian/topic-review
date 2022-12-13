package com.example.topicreview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.topicreview.R
import com.example.topicreview.database.Category
import com.example.topicreview.databinding.ListItemCatSelectBinding

private const val NONE_ID = -1
private const val VIEW_TYPE_NONE = 0
private const val VIEW_TYPE_CATEGORY = 1

class SelectCategoryAdapter(private var selectedId: Int = NONE_ID): ListAdapter<Category, SelectCategoryAdapter.ViewHolder>(DiffCallback) {

    private var selectedPosition: Int = -1


    open class ViewHolder(private val binding: ListItemCatSelectBinding): RecyclerView.ViewHolder(binding.root) {
        open fun bind(category: Category, selectedId: Int) {
            binding.category.text = category.title
            binding.category.isChecked = category.id == selectedId
        }

        open fun setListener(listener: OnCheckedChangeListener) {
            binding.category.setOnCheckedChangeListener(listener)
        }
    }

    class NoneViewHolder(private val binding: ListItemCatSelectBinding): ViewHolder(binding) {
        fun bind(selectedId: Int) {
            binding.category.setText(R.string.none)
            binding.category.isChecked = selectedId <= 0
        }
    }

    init {
        if (selectedId == NONE_ID) selectedPosition = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemCatSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            VIEW_TYPE_NONE -> NoneViewHolder(binding)
            else -> ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_NONE -> bindNone(holder as NoneViewHolder)
            else -> bindCategory(holder, getCategory(position))
        }
    }

    private fun bindNone(holder: NoneViewHolder) {
        holder.bind(selectedId)
        holder.setListener { _, isChecked ->
            if (isChecked) {
                selectedId = NONE_ID
                if (selectedPosition != -1) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = holder.adapterPosition
            }
        }
    }

    private fun bindCategory(holder: ViewHolder, category: Category) {
        holder.bind(category, selectedId)
        holder.setListener { _, isChecked ->
            if (isChecked && category.id != selectedId) {
                selectedId = category.id
                if (selectedPosition != -1) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = holder.adapterPosition
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_NONE
            else -> VIEW_TYPE_CATEGORY
        }
    }

    private fun getCategory(adapterPosition: Int): Category {
        return getItem(adapterPosition - 1)
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    fun getSelectedItemId(): Int {
        return selectedId
    }

    object DiffCallback: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}