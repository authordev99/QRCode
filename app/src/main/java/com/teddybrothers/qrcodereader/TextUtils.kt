package com.teddybrothers.qrcodereader

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat

object TextUtils {
    @JvmStatic
    fun applyURL(
        mContext: Context,
        fulltext: String,
        textHyperlink: String,
        url: String
    ): SpannableStringBuilder {

        val spannableStringBuilder = SpannableStringBuilder(fulltext)
        val startIndex = fulltext.indexOf(textHyperlink)

        if (startIndex >= 0) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    mContext.startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.linkColor =  R.color.linkColor
                    ds.color = ContextCompat.getColor(mContext, R.color.linkColor)
                }
            }
            spannableStringBuilder.setSpan(
                StyleSpan(Typeface.NORMAL),
                startIndex,
                startIndex + textHyperlink.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableStringBuilder.setSpan(
                clickableSpan,
                startIndex,
                startIndex + textHyperlink.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannableStringBuilder
    }
}
