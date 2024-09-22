package com.example.voicechanger.popup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.example.voicechanger.databinding.LayoutPopupMenuSortBinding
import com.example.voicechanger.utils.setOnSafeClickListener

class AudioSortPopup(
    context: Context,
    private val onSortNewest: () -> Unit,
    private val onSortFileName: () -> Unit
) : PopupWindow(context) {

    private val binding: LayoutPopupMenuSortBinding =
        LayoutPopupMenuSortBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupMenu()
    }

    private fun setupMenu() {
        binding.apply {
            llCreated.setOnSafeClickListener {
                ivCreated.isSelected = !ivCreated.isSelected
                onSortNewest()
            }

            llFileName.setOnSafeClickListener {
                ivFileName.isSelected = !ivFileName.isSelected
                onSortFileName()
            }
        }
    }

    fun show(anchorView: View) {
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val xOffset = anchorView.width
        val yOffset = anchorView.height
        showAtLocation(anchorView, 0, location[0] + xOffset, location[1] + yOffset)
    }
}