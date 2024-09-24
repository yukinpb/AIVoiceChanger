package com.example.voicechanger.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.ItemEffectAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentAudioEffectBinding
import com.example.voicechanger.model.AudioEffectModel
import com.example.voicechanger.utils.EffectType
import com.example.voicechanger.viewModel.AIVoiceMakerViewModel
import com.example.voicechanger.viewModel.AudioEffectViewModel
import com.example.voicechanger.viewModel.ChangeEffectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioEffectFragment : BaseFragment<FragmentAudioEffectBinding, AudioEffectViewModel>(R.layout.fragment_audio_effect) {

    private lateinit var effectType: EffectType
    private var itemEffectAdapter: ItemEffectAdapter? = null

    private val parentViewModel: ChangeEffectViewModel by activityViewModels()
    private val parentAIViewModel: AIVoiceMakerViewModel by activityViewModels()

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val effectModel: AudioEffectModel? = intent?.getParcelableExtra("effect_model")
            if (intent?.action == "select_effect" && effectModel != null) {
                itemEffectAdapter?.selectEffectItem(effectModel)
            }
        }
    }

    override fun getVM(): AudioEffectViewModel {
        val viewModel: AudioEffectViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, IntentFilter("select_effect"))

        arguments?.let {
            effectType = it.getSerializable(ARG_EFFECT_TYPE) as EffectType
        }

        when (effectType) {
            EffectType.ROBOT -> {
                getVM().getRobotEffect()
            }
            EffectType.PEOPLE -> {
                getVM().getPeopleEffect()
            }
            EffectType.SCARY -> {
                getVM().getScaryEffect()
            }
            EffectType.OTHER -> {
                getVM().getOtherEffect()
            }
            EffectType.ALL -> {
                getVM().getAllEffect()
            }
            EffectType.AI -> {
                getVM().getAIEffect()
            }
        }

        itemEffectAdapter = ItemEffectAdapter(requireContext()) {
            if (effectType == EffectType.AI) {
                parentAIViewModel.applyEffect(it.id)
            } else {
                parentViewModel.applyEffect(it.id)
            }
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("select_effect").putExtra("effect_model", it))
        }
        binding.rclEffect.apply {
            adapter = itemEffectAdapter
            setHasFixedSize(true)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().audioEffect.observe(this) {
            itemEffectAdapter?.submitList(it)
        }
    }

    companion object {
        private const val ARG_EFFECT_TYPE = "effect_type"

        fun newInstance(effectType: EffectType): AudioEffectFragment {
            val fragment = AudioEffectFragment()
            val args = Bundle()
            args.putSerializable(ARG_EFFECT_TYPE, effectType)
            fragment.arguments = args
            return fragment
        }
    }

}