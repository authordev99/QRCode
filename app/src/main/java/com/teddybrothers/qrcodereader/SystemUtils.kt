package com.teddybrothers.qrcodereader

import android.content.Context
import android.content.Intent


object SystemUtils {
    fun shareIntent(context: Context, content: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share your result")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content)
        context.startActivity(
            Intent.createChooser(
                sharingIntent,
                "Share!"
            )
        )

    }
}