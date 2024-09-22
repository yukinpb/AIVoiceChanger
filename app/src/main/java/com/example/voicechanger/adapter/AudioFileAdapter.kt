package com.example.voicechanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ItemAudioBinding
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.utils.setOnSafeClickListener

class AudioFileAdapter(
    private val isShowMenu: Boolean = false,
    private val onMenuClick: (AudioModel) -> Unit = { },
    private val onItemClicked: (AudioModel) -> Unit = {}
) : ListAdapter<AudioModel, AudioFileAdapter.AudioFileViewHolder>(AudioModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioFileViewHolder {
        val binding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioFileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AudioFileViewHolder(private val binding: ItemAudioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(audioFile: AudioModel) {
            binding.tvName.text = audioFile.fileName
            binding.tvDetail.text = binding.root.context.getString(R.string.audio_attr, audioFile.duration, audioFile.size)
            binding.ivMenu.visibility = if (isShowMenu) View.VISIBLE else View.GONE
            binding.ivMenu.setOnSafeClickListener { onMenuClick(audioFile) }
            binding.root.setOnSafeClickListener { onItemClicked(audioFile) }
        }
    }

    class AudioModelDiffCallback : DiffUtil.ItemCallback<AudioModel>() {
        override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem.fileName == newItem.fileName
        }

        override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem == newItem
        }
    }
}