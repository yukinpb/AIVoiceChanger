package com.example.voicechanger.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentTextToAudioBinding
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.popup.LanguagePopup
import com.example.voicechanger.utils.Constants.ARG_AUDIO_PATH
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.toast
import com.example.voicechanger.viewModel.TextToAudioViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TextToAudioFragment :
    BaseFragment<FragmentTextToAudioBinding, TextToAudioViewModel>(R.layout.fragment_text_to_audio) {

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun getVM(): TextToAudioViewModel {
        val viewModel: TextToAudioViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.tvLocate.visibility = View.VISIBLE
        binding.toolbar.tvTitle.setText(R.string.text_to_audio)
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.toolbar.ivBack.setOnSafeClickListener {
            onBack()
        }

        binding.tvLocate.setOnSafeClickListener {
            showPopupMenu(binding.tvLocate)
        }

        binding.tvNext.setOnSafeClickListener {
            val text = binding.input.text.trim()

            if (text.length < 5) {
                requireContext().toast(getString(R.string.enter_minimum_words))
            } else {
                getVM().saveAudio(text.toString()) { path ->
                    appNavigation.openTextToAudioToChangeEffectScreen(Bundle().apply {
                        putString(ARG_AUDIO_PATH, path)
                    })
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().language.observe(this) { language ->
            language?.let {
                binding.tvCountry.text = language.languageName
                binding.imgFlag.setImageResource(language.flag)
            }
        }
    }

    private fun showPopupMenu(anchor: View) {
        val languagePopup = LanguagePopup(requireContext()) { language ->
            getVM().setLanguage(language)
        }
        languagePopup.show(anchor)
    }

}