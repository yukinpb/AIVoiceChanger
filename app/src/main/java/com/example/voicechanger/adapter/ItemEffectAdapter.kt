package com.example.voicechanger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ItemEffectBinding
import com.example.voicechanger.model.AudioEffectModel
import com.example.voicechanger.utils.setOnSafeClickListener

class ItemEffectAdapter(
    private val context: Context,
    private val onItemClicked: (AudioEffectModel) -> Unit
) : ListAdapter<AudioEffectModel, ItemEffectAdapter.EffectViewHolder>(AudioEffectModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EffectViewHolder {
        val binding = ItemEffectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EffectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EffectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EffectViewHolder(
        private val binding: ItemEffectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(effectModel: AudioEffectModel) {
            binding.tvEffect.isSelected = true
            binding.tvEffect.text = effectModel.name

            if (effectModel.isActive) {
                binding.llyMain.setBackgroundResource(R.drawable.bg_effect_selected)
                binding.tvEffect.setTextColor(context.getColor(R.color.black))
                Glide.with(context).load(effectModel.iconUnSelected).into(binding.ivAvt)
            } else {
                binding.llyMain.setBackgroundResource(R.drawable.bg_item_unselect)
                binding.tvEffect.setTextColor(context.getColor(R.color.grayText))
                Glide.with(context).load(effectModel.iconSelected).into(binding.ivAvt)
            }

            itemView.setOnSafeClickListener {
                selectEffectItem(effectModel)
                onItemClicked(effectModel)
            }
        }
    }

    fun selectEffectItem(effectModel: AudioEffectModel) {
        val updatedList = currentList.map {
            it.copy(isActive = it.name == effectModel.name)
        }
        submitList(updatedList)
    }
}

class AudioEffectModelDiffCallback : DiffUtil.ItemCallback<AudioEffectModel>() {
    override fun areItemsTheSame(oldItem: AudioEffectModel, newItem: AudioEffectModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AudioEffectModel, newItem: AudioEffectModel): Boolean {
        return oldItem == newItem
    }
}