package com.example.voicechanger.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.adapter.FragmentPagerAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentAiVoiceMakerBinding
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.utils.Constants.ARG_AUDIO_MODEL
import com.example.voicechanger.utils.Constants.DIRECTORY
import com.example.voicechanger.utils.Constants.Fragments.AI_VOICE_MAKER_FRAGMENT
import com.example.voicechanger.utils.EffectType
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.toast
import com.example.voicechanger.viewModel.AIVoiceMakerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AIVoiceMakerFragment : BaseFragment<FragmentAiVoiceMakerBinding, AIVoiceMakerViewModel>(R.layout.fragment_ai_voice_maker) {

    @Inject
    lateinit var appNavigation: AppNavigation
    override fun getVM(): AIVoiceMakerViewModel {
        val viewModel: AIVoiceMakerViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        initToolbar()

        initMainView()

        setupTextWatcher()
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    private fun initToolbar() {
        binding.toolbar.ivBack.setOnClickListener {
            onBack()
        }

        binding.toolbar.tvTitle.text = getString(R.string.ai_voices)
    }

    private fun initMainView() {
        val audioEffectFragmentList = listOf(
            AudioEffectFragment.newInstance(EffectType.AI)
        )
        val viewpagerAdapter = activity?.let { FragmentPagerAdapter(it, audioEffectFragmentList) }
        binding.viewPager.adapter = viewpagerAdapter
    }

    private fun setupTextWatcher() {
        binding.input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                binding.tvCount.text = String.format(getString(R.string._03d_200), textLength)

                if (textLength > 200) {
                    requireContext().toast("Text exceeds 200 characters")
                    binding.input.setText(s?.substring(0, 200))
                    binding.input.setSelection(200)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.tvNext.setOnSafeClickListener {
            if (binding.input.text.length < 5) {
                requireContext().toast("Text must be at least 5 characters")
                return@setOnSafeClickListener
            }
            getVM().fetchVoice(binding.input.text.toString())
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().isSuccess.observe(viewLifecycleOwner){
            if (it) {
                appNavigation.openAIVoiceMakerToPlayerScreen(Bundle().apply {
                    putParcelable(ARG_AUDIO_MODEL, getVM().getAudioSaved())
                    putString(DIRECTORY, AI_VOICE_MAKER_FRAGMENT)
                })
            }
        }
    }

}