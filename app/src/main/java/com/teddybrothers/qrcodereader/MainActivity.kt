package com.teddybrothers.qrcodereader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.otaliastudios.cameraview.frame.Frame
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var isDetected = false
    lateinit var options: FirebaseVisionBarcodeDetectorOptions
    lateinit var detector: FirebaseVisionBarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    setupCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {

                }

            }).check()
    }

    fun setupCamera() {
        options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE).build()
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        cameraView.setLifecycleOwner(this)
        cameraView.addFrameProcessor { frame -> processImage(getVisionImageFromFrame(frame)) }

    }

    private fun processImage(firebaseVisionImage: FirebaseVisionImage) {
        detector.detectInImage(firebaseVisionImage)
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener { firebaseVisionBarcodes ->
                if (!isDetected) {
                    processResult(firebaseVisionBarcodes)
                }

            }

    }

    private fun processResult(firebaseVisionBarcode: List<FirebaseVisionBarcode>) {
        if (firebaseVisionBarcode.isNotEmpty()) {
            isDetected = true
            firebaseVisionBarcode.forEach {
                val value_type = it.valueType
                when (value_type) {
                    FirebaseVisionBarcode.TYPE_TEXT -> {
                        createDialog(it.rawValue)
                    }
                    FirebaseVisionBarcode.TYPE_CONTACT_INFO -> {
                        val info = StringBuilder("Name :")
                            .append(it.contactInfo?.name?.formattedName)
                            .append("\n")
                            .append("Address : ")
                            .append(it.contactInfo?.addresses?.get(0)?.addressLines?.get(0))
                            .append("\n")
                            .append("Email : ")
                            .append(it.contactInfo?.emails?.get(0)?.address)
                    }
                    FirebaseVisionBarcode.TYPE_URL -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.rawValue))
                        startActivity(intent)
                    }

                }
            }
        }
    }

    private fun createDialog(rawValue: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(rawValue)
            .setPositiveButton("OK") { dialog, i ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getVisionImageFromFrame(frame: Frame): FirebaseVisionImage {
        val data = frame.getData<ByteArray>()
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setHeight(frame.size.height)
            .setWidth(frame.size.width)
            .build()

        return FirebaseVisionImage.fromByteArray(data, metadata)
    }


}
