package com.teddybrothers.qrcodereader

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode


object AppBinding {
    @JvmStatic
    @BindingAdapter(value = ["imageBitmap"])
    fun ImageView.setBitmap(bitmap: Bitmap?) {
        if (bitmap != null)
            this.setImageBitmap(bitmap)
    }

    @JvmStatic
    @BindingAdapter("scanType")
    fun TextView.setScanType(value: Int) {
        var type = "Plain Text"
        if (value == FirebaseVisionBarcode.TYPE_URL) {
            type = "Hyperlink"
        }
        this.text = type
    }

}