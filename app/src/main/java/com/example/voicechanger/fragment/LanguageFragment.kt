package com.example.voicechanger.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.voicechanger.R
import com.example.voicechanger.adapter.LanguageAdapter
import com.example.voicechanger.adapter.LanguageAdapter.Companion.VIEW_TYPE_2
import com.example.voicechanger.base.fragment.BaseFragmentNotRequireViewModel
import com.example.voicechanger.databinding.FragmentLanguageBinding
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.pref.AppPreferences
import com.example.voicechanger.utils.LanguageProvider
import com.example.voicechanger.utils.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : BaseFragmentNotRequireViewModel<FragmentLanguageBinding>(R.layout.fragment_language) {

    private lateinit var adapter: LanguageAdapter
    private val languages = LanguageProvider.getLanguages()

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var appNavigation: AppNavigation
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        lifecycleScope.launch {
            val languageName = appPreferences.getLanguage().firstOrNull()
            languageName?.let { name ->
                languages.forEach { language ->
                    if (language.languageName == name) {
                        language.isCheck = true
                    }
                }
            }
        }

        adapter = LanguageAdapter(languages, VIEW_TYPE_2) {
            lifecycleScope.launch {
                appPreferences.setLanguage(it)
                LocaleHelper.setLocale(requireContext(), it.locale)
                requireActivity().recreate()
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.txtDone.setOnSafeClickListener {
            lifecycleScope.launch {
                appPreferences.setFirstTime(false)
            }
            appNavigation.openLanguageToHomeScreen()
        }
    }
}

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}