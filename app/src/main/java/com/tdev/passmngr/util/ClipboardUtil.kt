package com.tdev.passmngr.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ClipboardUtil {

    fun copy(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "$label kopyalandı", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
        }, 30_000L)
    }
}
