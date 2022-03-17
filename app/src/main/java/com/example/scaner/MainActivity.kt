package com.example.scaner

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*

private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {
    private lateinit var codScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codeScanner()
    }

    private fun codeScanner(){
        codScanner = CodeScanner(this, scanner_view)

        codScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                tv_textView.text = it.text
            }
        }
        codScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                tv_textView.text = it.message
            }
        }

        scanner_view.setOnClickListener{
            codScanner.startPreview()
        }
    }
    override fun onResume() {
        super.onResume()
        codScanner.startPreview()
    }

    override fun onPause() {
        codScanner.releaseResources()
        super.onPause()
    }
    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission to be able to use this app", Toast.LENGTH_SHORT)
                }
            }
        }
    }
}