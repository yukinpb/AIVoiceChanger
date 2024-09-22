package com.example.voicechanger.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.voicechanger.model.TypeEffectModel
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ItemTypeEffectBinding
import com.example.voicechanger.utils.setOnSafeClickListener
import kotlin.Unit

class EffectAdapter(
    private val context: Context,
    private val onItemClicked: (Int) -> Unit,
) : ListAdapter<TypeEffectModel, EffectAdapter.TypeEffectViewHolder>(TypeEffectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeEffectViewHolder {
        val binding = ItemTypeEffectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TypeEffectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TypeEffectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TypeEffectViewHolder(
        private val binding: ItemTypeEffectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TypeEffectModel) {
            binding.txtEffectType.text = data.type
            if (data.isActive) {
                binding.txtEffectType.setBackgroundResource(R.drawable.bg_selected_tab)
                binding.txtEffectType.setTextColor(context.getColor(R.color.white))
            } else {
                binding.txtEffectType.setBackgroundResource(R.drawable.bg_unselected_tab)
                binding.txtEffectType.setTextColor(context.getColor(R.color.white))
            }

            itemView.setOnSafeClickListener {
                selectItem(data)
                onItemClicked(bindingAdapterPosition)
            }
        }
    }

    fun selectItem(typeEffectModel: TypeEffectModel) {
        val updatedList = currentList.map {
            it.copy(isActive = it == typeEffectModel)
        }
        submitList(updatedList)
    }

    fun selectItem(i: Int) {
        val typeEffectModel = getItem(i)
        val updatedList = currentList.map {
            it.copy(isActive = it == typeEffectModel)
        }
        submitList(updatedList)
    }
}

class TypeEffectDiffCallback : DiffUtil.ItemCallback<TypeEffectModel>() {
    override fun areItemsTheSame(oldItem: TypeEffectModel, newItem: TypeEffectModel): Boolean {
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: TypeEffectModel, newItem: TypeEffectModel): Boolean {
        return oldItem == newItem
    }
}

