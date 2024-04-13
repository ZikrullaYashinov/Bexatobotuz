package com.apextech.bexatobotuz.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent


object Assistant {

    fun copyText(activity: Activity, text: String) {
        if (text.isEmpty()) return
        val manager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("", text))
    }

    fun pasteText(activity: Activity): String {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip
        return clipData?.getItemAt(0)?.text.toString()
    }

    fun shareItem(context: Context, text: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}