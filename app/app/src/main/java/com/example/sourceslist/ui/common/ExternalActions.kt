package com.example.sourceslist.ui.common

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun copyLink(context: Context, url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Source link", url))
    Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
}

fun openLink(context: Context, url: String) {
    val uri = Uri.parse(url)
    val packageName = when {
        uri.host?.contains("youtube.com", ignoreCase = true) == true ||
            uri.host?.contains("youtu.be", ignoreCase = true) == true -> "com.google.android.youtube"

        else -> "com.android.chrome"
    }
    val targetedIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage(packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(targetedIntent)
    } catch (_: ActivityNotFoundException) {
        context.startActivity(fallbackIntent)
    }
}
