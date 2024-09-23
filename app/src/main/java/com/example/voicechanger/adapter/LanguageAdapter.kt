package com.example.voicechanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicechanger.databinding.ItemLanguage1Binding
import com.example.voicechanger.databinding.ItemLanguage2Binding
import com.example.voicechanger.model.LanguageModel
import com.example.voicechanger.utils.setOnSafeClickListener

class LanguageAdapter(
    private val languages: List<LanguageModel>,
    private val viewType: Int,
    private val onLanguageSelected: (LanguageModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_1 -> {
                val binding = ItemLanguage1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                LanguageViewHolder1(binding)
            }
            VIEW_TYPE_2 -> {
                val binding = ItemLanguage2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                LanguageViewHolder2(binding, onLanguageSelected)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LanguageViewHolder1 -> holder.bind(languages[position])
            is LanguageViewHolder2 -> holder.bind(languages[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getItemCount(): Int = languages.size

    inner class LanguageViewHolder1(private val binding: ItemLanguage1Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(language: LanguageModel) {
            binding.tvLanguage.text = language.languageName
            binding.root.setOnSafeClickListener { onLanguageSelected(language) }
        }
    }

    inner class LanguageViewHolder2(
        private val binding: ItemLanguage2Binding,
        private val onLanguageSelected: (LanguageModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(languageModel: LanguageModel) {
            binding.lanName.text = languageModel.languageName
            binding.txtLang.text = languageModel.originName
            Glide.with(binding.root.context).load(languageModel.flag).into(binding.imgFlag)
            binding.imgSelected.visibility = if (languageModel.isCheck) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                click(languageModel, it)
            }
        }

        private fun click(languageModel: LanguageModel, view: View) {
            onLanguageSelected(languageModel)
            languageModel.isCheck = true
            handleLangDisplay(languageModel)
        }

        private fun handleLangDisplay(languageModel: LanguageModel) {
            for (lang in languages) {
                if (lang.languageName != languageModel.languageName) {
                    lang.isCheck = false
                }
            }
            notifyDataSetChanged()
        }
    }

    companion object {
        const val VIEW_TYPE_1 = 1
        const val VIEW_TYPE_2 = 2
    }
}