package com.example.voicechanger.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentAudioListBinding
import com.example.voicechanger.dialog.ConfirmDialog
import com.example.voicechanger.dialog.RingtoneDialog
import com.example.voicechanger.dialog.SaveDialog
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.popup.AudioSortPopup
import com.example.voicechanger.utils.Constants.ARG_AUDIO_MODEL
import com.example.voicechanger.utils.Constants.ARG_AUDIO_PATH
import com.example.voicechanger.utils.Constants.DIRECTORY
import com.example.voicechanger.utils.Constants.Fragments.AUDIO_LIST_FRAGMENT
import com.example.voicechanger.utils.Constants.Fragments.MERGE_AUDIO_FRAGMENT
import com.example.voicechanger.utils.Constants.Fragments.MY_VOICE_FRAGMENT
import com.example.voicechanger.utils.Constants.Fragments.OPEN_FILE_FRAGMENT
import com.example.voicechanger.utils.Constants.Fragments.TRIM_AUDIO_FRAGMENT
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.shareFile
import com.example.voicechanger.utils.toast
import com.example.voicechanger.viewModel.AudioListViewModel
import com.example.voicechanger.viewModel.SortType
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AudioListFragment :
    BaseFragment<FragmentAudioListBinding, AudioListViewModel>(R.layout.fragment_audio_list) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private var currentPopup: PopupWindow? = null

    private var directory: String = ""

    private var audioFiles: List<AudioModel> = listOf()

    private lateinit var audioAdapter: AudioFileAdapter

    override fun getVM(): AudioListViewModel {
        val viewModel: AudioListViewModel by viewModels()
        return viewModel
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        getData()

        initToolbar()

        initMainView()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().audioFiles.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noData.visibility = View.VISIBLE
                binding.rclAudio.visibility = View.GONE
            } else {
                binding.noData.visibility = View.GONE
                binding.rclAudio.visibility = View.VISIBLE
                audioFiles = it
                audioAdapter.submitList(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getVM().loadAudioFiles()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.toolbar.ivSort.setOnSafeClickListener {
            showSortPopup(binding.toolbar.ivSort)
        }

        binding.fabMergeAudio.setOnSafeClickListener {
            val selectedItems = audioAdapter.getSelectedItems()

            if (selectedItems.size < 2) {
                requireContext().toast("Please select at least 2 audio files to merge")
                return@setOnSafeClickListener
            }

            getVM().mergeSelectedAudioFiles(selectedItems,
                onSuccess = {
                    appNavigation.openAudioListToAudioPlayerScreen(Bundle().apply {
                        putParcelable(ARG_AUDIO_MODEL, it)
                        putString(DIRECTORY, MERGE_AUDIO_FRAGMENT)
                    })
                },
                onError = {
                    requireContext().toast("Merge audio failed!!!")
                }
            )
        }
    }

    private fun getData() {
        arguments?.let {
            directory = it.getString(DIRECTORY) ?: ""
        }
    }

    private fun initToolbar() {
        binding.toolbar.ivBack.setOnSafeClickListener {
            onBack()
        }

        binding.toolbar.tvTitle.text = getString(R.string.change_voice)

        binding.toolbar.ivSort.visibility = View.VISIBLE
    }

    private fun initMainView() {
        audioAdapter = AudioFileAdapter(
            isShowCheckbox = directory == MERGE_AUDIO_FRAGMENT,
            isShowMenu = directory == MY_VOICE_FRAGMENT,
            onMenuClick = { audioFile ->
                showBottomSheetDialog(audioFile)
            },
            onItemClicked = {
                when (directory) {
                    MY_VOICE_FRAGMENT -> {
                        appNavigation.openAudioListToAudioPlayerScreen(Bundle().apply {
                            putParcelable(ARG_AUDIO_MODEL, it)
                            putString(DIRECTORY, AUDIO_LIST_FRAGMENT)
                        })
                    }
                    OPEN_FILE_FRAGMENT -> {
                        appNavigation.openAudioListToChangeEffectScreen(Bundle().apply {
                            putString(ARG_AUDIO_PATH, it.path)
                        })
                    }
                    TRIM_AUDIO_FRAGMENT -> {
                        appNavigation.openAudioListToEditAudioScreen(Bundle().apply {
                            putString(ARG_AUDIO_PATH, it.path)
                            putString(DIRECTORY, TRIM_AUDIO_FRAGMENT)
                        })
                    }
                }
            }
        )

        binding.rclAudio.layoutManager = LinearLayoutManager(requireContext())
        binding.rclAudio.adapter = audioAdapter

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText ?: "")
                return true
            }
        })

        binding.fabMergeAudio.isVisible = directory == MERGE_AUDIO_FRAGMENT
        binding.toolbar.tvTitle.text = when (directory) {
            MY_VOICE_FRAGMENT -> getString(R.string.my_voice)
            OPEN_FILE_FRAGMENT -> getString(R.string.txt_import)
            TRIM_AUDIO_FRAGMENT -> getString(R.string.trim_audio)
            else -> getString(R.string.merge_audio)
        }
    }

    private fun filter(query: String) {
        val filteredList = audioFiles.filter { it.fileName.contains(query, ignoreCase = true) }
        if (filteredList.isEmpty()) {
            binding.noData.visibility = View.VISIBLE
            binding.rclAudio.visibility = View.GONE
        } else {
            binding.noData.visibility = View.GONE
            binding.rclAudio.visibility = View.VISIBLE
            audioAdapter.submitList(filteredList)
        }
    }

    private fun showSortPopup(anchorView: View) {
        currentPopup?.dismiss()
        if (currentPopup?.isShowing == true) {
            currentPopup = null
            return
        }

        val audioFileSortPopup = AudioSortPopup(
            context = requireContext(),
            onSortNewest = {
                if (getVM().getSortType() == SortType.DATE_NEWEST) {
                    getVM().setSortType(SortType.DATE_OLDEST)
                } else {
                    getVM().setSortType(SortType.DATE_NEWEST)
                }
            },
            onSortFileName = {
                if (getVM().getSortType() == SortType.NAME_ASC) {
                    getVM().setSortType(SortType.NAME_DESC)
                } else {
                    getVM().setSortType(SortType.NAME_ASC)
                }
            }
        )

        audioFileSortPopup.show(anchorView)
        currentPopup = audioFileSortPopup
    }

    private fun showBottomSheetDialog(audioModel: AudioModel) {
        val bottomSheetDialog = BottomSheetDialog(
            requireContext(),
            R.style.BottomSheetDialogTheme
        ).apply {
            setContentView(R.layout.layout_bottom_sheet_menu)
            setCanceledOnTouchOutside(false)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        bottomSheetDialog.findViewById<ImageView>(R.id.imgClose)?.setOnSafeClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<LinearLayout>(R.id.setAsRing)?.setOnSafeClickListener {
            showSetAsRingtoneDialog(audioModel)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<LinearLayout>(R.id.share)?.setOnSafeClickListener {
            onShareAudioFile(audioModel)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<LinearLayout>(R.id.delete)?.setOnSafeClickListener {
            onDeleteAudioFile(audioModel)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<LinearLayout>(R.id.llyRename)?.setOnSafeClickListener {
            showEnterFileNameDialog(audioModel)
            bottomSheetDialog.dismiss()
        }

        if (!bottomSheetDialog.isShowing) {
            bottomSheetDialog.show()
        }
    }

    private fun onShareAudioFile(audioFile: AudioModel) {
        audioFile.path.let { filePath ->
            requireContext().shareFile(filePath)
        }
    }

    private fun onDeleteAudioFile(audioFile: AudioModel) {
        ConfirmDialog(
            title = getString(R.string.delete),
            content = getString(R.string.are_you_sure_to_delete_this_audio),
            negative = getString(R.string.cancel),
            positive = getString(R.string.ok),
            onNegative = {},
            onPositive = {
                val file = File(audioFile.path)
                if (file.delete()) {
                    requireContext().toast(
                        getString(
                            R.string.deleted_file_successfully,
                            audioFile.fileName
                        )
                    )
                    getVM().loadAudioFiles()
                }
            }
        ).show(parentFragmentManager, ConfirmDialog::class.java.simpleName)
    }

    private fun showSetAsRingtoneDialog(audioFile: AudioModel) {
        val dialog = RingtoneDialog(audioFile.path)
        dialog.show(parentFragmentManager, "SetAsRingtoneDialog")
    }

    private fun showEnterFileNameDialog(audioFile: AudioModel) {
        SaveDialog(
            onNegative = {},
            onPositive = { fileName ->
                val file = File(audioFile.path)
                val extension = file.extension
                val newFile = File(file.parent, "$fileName.$extension")

                if (newFile.exists()) {
                    ConfirmDialog(
                        title = getString(R.string.rename),
                        content = getString(R.string.file_already_exists, fileName),
                        negative = getString(R.string.cancel),
                        positive = getString(R.string.ok),
                        onNegative = {},
                        onPositive = {
                            newFile.delete()

                            if (file.renameTo(newFile)) {
                                requireContext().toast(
                                    getString(
                                        R.string.rename_file_successfully,
                                        audioFile.fileName,
                                        fileName
                                    )
                                )
                                getVM().loadAudioFiles()
                            }
                        }
                    ).show(parentFragmentManager, ConfirmDialog::class.java.simpleName)
                } else {
                    if (file.renameTo(newFile)) {
                        requireContext().toast(
                            getString(
                                R.string.rename_file_successfully,
                                audioFile.fileName,
                                fileName
                            )
                        )
                        getVM().loadAudioFiles()
                    }
                }
            }
        ).show(parentFragmentManager, SaveDialog::class.java.simpleName)
    }
}