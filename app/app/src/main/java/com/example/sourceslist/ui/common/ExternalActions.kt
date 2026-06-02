package com.example.sourceslist.ui.common

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URI

private const val ChromePackage = "com.android.chrome"
private const val YouTubePackage = "com.google.android.youtube"
private const val SubstackPackage = "com.substack.app"
private const val SubstackOpenHost = "open.substack.com"

fun copyLink(context: Context, url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Source link", url))
    Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
}

fun openLink(context: Context, url: String) {
    val originalUri = Uri.parse(url)
    val appUri = Uri.parse(url.toAppPreferredUrl())
    val targetedIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
        setPackage(url.preferredPackageName())
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val fallbackIntent = Intent(Intent.ACTION_VIEW, originalUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(targetedIntent)
    } catch (_: ActivityNotFoundException) {
        context.startActivity(fallbackIntent)
    }
}

internal fun String.toAppPreferredUrl(): String {
    val uri = runCatching { URI(this) }.getOrNull() ?: return this
    val host = uri.host.orEmpty()
    if (host.equals(SubstackOpenHost, ignoreCase = true)) return this
    if (!host.endsWith(".substack.com", ignoreCase = true)) return this

    val publication = host.removeSuffix(".substack.com")
    if (publication.isBlank()) return this

    val normalizedPath = uri.rawPath.orEmpty().trimStart('/')
    val rewrittenPath = buildString {
        append("/pub/")
        append(publication)
        if (normalizedPath.isNotEmpty()) {
            append('/')
            append(normalizedPath)
        }
    }

    return URI(
        "https",
        SubstackOpenHost,
        rewrittenPath,
        uri.rawQuery,
        uri.rawFragment
    ).toString()
}

internal fun String.preferredPackageName(): String {
    val host = runCatching { URI(this).host }.getOrNull().orEmpty()

    return when {
        host.contains("youtube.com", ignoreCase = true) ||
            host.contains("youtu.be", ignoreCase = true) -> YouTubePackage

        host.equals(SubstackOpenHost, ignoreCase = true) ||
            host.endsWith(".substack.com", ignoreCase = true) -> SubstackPackage

        else -> ChromePackage
    }
}

internal fun Uri.toAppPreferredUri(): Uri = Uri.parse(toString().toAppPreferredUrl())

internal fun Uri.preferredPackageName(): String = toString().preferredPackageName()
