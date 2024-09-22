package com.example.voicechanger.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.voicechanger.R
import java.io.File

fun Context.shareFile(filePath: String) {
    val file = File(filePath)
    val uri = FileProvider.getUriForFile(
        this,
        "${this.packageName}.provider",
        file
    )
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "audio/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, this.getString(R.string.share_audio)))
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}