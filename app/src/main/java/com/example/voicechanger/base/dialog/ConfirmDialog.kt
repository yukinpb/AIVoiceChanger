package com.example.voicechanger.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.voicechanger.R


const val CONFIRM_DIALOG_FRAGMENT = "ConfirmDialogFragment"

class ConfirmDialogFragment : BaseDialogFragment() {

    var dialogListener: ConfirmDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is ConfirmDialogListener) {
            dialogListener = parentFragment as ConfirmDialogListener
            return
        }

        if (context is ConfirmDialogListener) {
            dialogListener = context
            return
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(CONFIRM_DIALOG_TITLE) ?: ""
        val content = arguments?.getString(CONFIRM_DIALOG_CONTENT) ?: ""
        val type = arguments?.getInt(CONFIRM_DIALOG_TYPE)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(content)
            .setPositiveButton(R.string.ok) { _, _ ->
                dialogListener?.onClickOk(type)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                dialogListener?.onClickCancel(type)
            }
        return builder.create()
    }

    companion object {
        private const val CONFIRM_DIALOG_TITLE = "CONFIRM_DIALOG_TITLE"
        private const val CONFIRM_DIALOG_CONTENT = "CONFIRM_DIALOG_CONTENT"
        private const val CONFIRM_DIALOG_TYPE = "CONFIRM_DIALOG_TYPE"

        fun getInstance(title: String, content: String, type: Int? = null): ConfirmDialogFragment {
            val bundle = Bundle()
            bundle.putString(CONFIRM_DIALOG_TITLE, title)
            bundle.putString(CONFIRM_DIALOG_CONTENT, content)
            type?.let {
                bundle.putInt(CONFIRM_DIALOG_TYPE, type)
            }
            return ConfirmDialogFragment().apply { arguments = bundle }
        }
    }

    override fun onDetach() {
        super.onDetach()
        dialogListener = null
    }
}

interface ConfirmDialogListener : BaseDialogListener {
    fun onClickOk(type: Int?)
    fun onClickCancel(type: Int?)
}