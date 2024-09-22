package com.example.voicechanger.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.voicechanger.R
import com.example.voicechanger.databinding.DialogSaveBinding
import com.example.voicechanger.utils.isValidName
import com.example.voicechanger.utils.setOnSafeClickListener
import java.util.Calendar

class SaveDialog(
    private val onNegative: () -> Unit,
    private val onPositive: (String) -> Unit
) : DialogFragment() {
    private var binding: DialogSaveBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogSaveBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.input?.setText(System.currentTimeMillis().toString())

        binding?.ivDel?.visibility = if (binding?.input?.text.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding?.input?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.ivDel?.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        binding?.ivDel?.setOnSafeClickListener {
            binding?.input?.text?.clear()
            binding?.ivDel?.visibility = View.GONE
        }

        binding?.tvCancel?.setOnSafeClickListener {
            onNegative()
            dismiss()
        }

        binding?.tvOk?.setOnSafeClickListener {
            onClickOk()
            dismiss()
        }
    }

    private fun onClickOk() {
        val name = binding?.input?.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(context, resources.getText(R.string.please_enter_text), Toast.LENGTH_SHORT).show()
            return
        }
        if (!name.isValidName()) {
            Toast.makeText(context, resources.getText(R.string.there_is_a_special_character), Toast.LENGTH_SHORT).show()
            return
        }
        onPositive(name)
    }

    override fun onResume() {
        super.onResume()

        val layoutParams = dialog?.window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}