package com.teddybrothers.qrcodereader

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.frame.Frame
import com.teddybrothers.qrcodereader.databinding.LayoutResultBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var firebaseVisionImagee: FirebaseVisionImage

    var isDetected = false
    lateinit var options: FirebaseVisionBarcodeDetectorOptions
    lateinit var detector: FirebaseVisionBarcodeDetector
    lateinit var animator: ObjectAnimator
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.findViewById<TextView>(R.id.title).text = "QRCode Scanner"


        val vto = scannerLayout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scannerLayout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                scannerLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val destination = (scannerLayout.y +
                        scannerLayout.height
                        )

                animator = ObjectAnimator.ofFloat(
                    scannerBar, "translationY",
                    scannerLayout.y,
                    destination
                )

                animator.repeatMode = ValueAnimator.REVERSE
                animator.repeatCount = ValueAnimator.INFINITE
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.duration = 3000
                animator.start();//To change body of created functions use File | Settings | File Templates.
            }

        })

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
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_QR_CODE,
                FirebaseVisionBarcode.FORMAT_ALL_FORMATS
            ).build()
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        cameraView.setLifecycleOwner(this)
        cameraView.addFrameProcessor { frame -> processImage(getVisionImageFromFrame(frame)) }
    }

    private fun processImage(firebaseVisionImage: FirebaseVisionImage) {
        firebaseVisionImagee = firebaseVisionImage
        detector.detectInImage(firebaseVisionImage)
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener { firebaseVisionBarcodes ->
                if (!isDetected && firebaseVisionBarcodes.isNotEmpty()) {
                    processResult(firebaseVisionBarcodes.first())
                    isDetected = true
                }

            }

    }

    private fun processResult(firebaseVisionBarcode: FirebaseVisionBarcode) {
        when (val valueType = firebaseVisionBarcode.valueType) {
            FirebaseVisionBarcode.TYPE_TEXT -> {
                firebaseVisionBarcode.rawValue?.let { text -> createDialog(valueType, text) }
            }
            FirebaseVisionBarcode.TYPE_CONTACT_INFO -> {
                val info = StringBuilder("Name :")
                    .append(firebaseVisionBarcode.contactInfo?.name?.formattedName)
                    .append("\n")
                    .append("Address : ")
                    .append(
                        firebaseVisionBarcode.contactInfo?.addresses?.get(0)?.addressLines?.get(
                            0
                        )
                    )
                    .append("\n")
                    .append("Email : ")
                    .append(firebaseVisionBarcode.contactInfo?.emails?.get(0)?.address)
                createDialog(valueType, info.toString())
            }
            FirebaseVisionBarcode.TYPE_URL -> {
                firebaseVisionBarcode.rawValue?.let { text -> createDialog(valueType, text) }
            }

        }

        val resultScan = ResultScan().apply {
            type = firebaseVisionBarcode.valueType
            result = firebaseVisionBarcode.rawValue
            dateTime = DateUtils.getLocalTime("dd MMM")
            image = null
        }
        saveResultScan(resultScan)


    }

    private fun createDialog(type: Int, rawValue: String) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetRadiusDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_result, null)
        val binding = DataBindingUtil.bind<LayoutResultBinding>(view)

        if (type == FirebaseVisionBarcode.TYPE_URL) {
            binding?.result?.text = TextUtils.applyURL(this, rawValue, rawValue, rawValue)
            binding?.result?.movementMethod = LinkMovementMethod.getInstance()
        } else {
            binding?.result?.text = rawValue
        }

        binding?.copy?.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", rawValue)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Text Copied", Toast.LENGTH_LONG).show()
        }
        binding?.share?.setOnClickListener {
            SystemUtils.shareIntent(this, rawValue)
        }
        binding?.rescan?.setOnClickListener {
            isDetected = false
            dialog.dismiss()
        }
        dialog.setOnCancelListener {
            isDetected = false
        }

        dialog.setContentView(view)
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

    private fun saveResultScan(resultScan: ResultScan) {
        val list = sessionManager.resultScanList as ArrayList
        list.add(resultScan)
        sessionManager.saveResultScanList(list)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scan, menu)

        val flashItem = menu.findItem(R.id.action_flash)
        val flashItemLayout = flashItem.actionView.findViewById<IconView>(R.id.icon)
        flashItemLayout.text = getString(R.string.icon_lamp)
        flashItemLayout.setOnClickListener {
            if (cameraView.flash == Flash.OFF) {
                cameraView.flash = Flash.TORCH
            } else {
                cameraView.flash = Flash.OFF
            }
        }

        val historyItem = menu.findItem(R.id.action_history)
        val historyItemLayout = historyItem.actionView.findViewById<IconView>(R.id.icon)
        historyItemLayout.text = getString(R.string.icon_history)
        historyItemLayout.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        isDetected = false
    }


}
