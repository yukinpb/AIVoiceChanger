package com.example.voicechanger.popup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.R
import com.example.voicechanger.adapter.LanguageAdapter
import com.example.voicechanger.adapter.LanguageAdapter.Companion.VIEW_TYPE_1
import com.example.voicechanger.databinding.LayoutPopupMenuLocateBinding
import com.example.voicechanger.model.LanguageModel
import com.example.voicechanger.utils.LanguageProvider

class LanguagePopup(
    private val context: Context,
    private val onLanguageSelected: (LanguageModel) -> Unit
) : PopupWindow(context) {
    private val binding: LayoutPopupMenuLocateBinding =
        LayoutPopupMenuLocateBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupMenu()
    }

    private fun setupMenu() {
        val languages = LanguageProvider.getLanguages()

        binding.rvLanguage.layoutManager = LinearLayoutManager(context)
        binding.rvLanguage.adapter = LanguageAdapter(languages, VIEW_TYPE_1) { language ->
            onLanguageSelected(language)
            dismiss()
        }
        binding.rvLanguage.addItemDecoration(DividerItemDecoration(context))
    }

    fun show(anchorView: View) {
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val xOffset = anchorView.width
        val yOffset = anchorView.height
        showAtLocation(anchorView, 0, location[0] + xOffset, location[1] + yOffset)
    }
}

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val paint: Paint = Paint().apply {
        color = context.getColor(R.color.white)
        strokeWidth = context.resources.getDimension(R.dimen._1sdp)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + paint.strokeWidth.toInt()
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}