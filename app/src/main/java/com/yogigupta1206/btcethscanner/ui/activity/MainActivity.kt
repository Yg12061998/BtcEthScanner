package com.yogigupta1206.btcethscanner.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.common.util.concurrent.ListenableFuture
import com.yogigupta1206.btcethscanner.R
import com.yogigupta1206.btcethscanner.databinding.ActivityMainBinding
import com.yogigupta1206.utils.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var processingBarcode = AtomicBoolean(false)
    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        setOnNavigationItemSelectListener()
        setClickListeners()
    }

    private fun setClickListeners() {
        mBinding.btnBtc.setOnClickListener {

        }

        mBinding.btnEth.setOnClickListener {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mBinding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setOnNavigationItemSelectListener() {
        mBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_exit -> {
                    showConfirmationDialog()
                }
                R.id.menu_call_support -> {
                    callSupport()
                }
            }
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            true

        }
    }

    private fun callSupport() {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse(CALL_DEVELOPER)
        startActivityForResult(callIntent, CALL_REQUEST)
    }

    private fun showConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.apply {
            setTitle(getString(R.string.exit_confirm))
            setMessage(getString(R.string.are_you_sure_you_want_to_exit))
            setPositiveButton(getString(R.string.exit)) { dialog, _ ->
                dialog.dismiss()
                callExit()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }

    }

    private fun callExit() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CALL_REQUEST) {
            mBinding.navigationView.menu.getItem(0).isChecked = false
        }

    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        mBinding.groupBtnInstructions.hide()
        mBinding.previewView.show()

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    mBinding.previewView.surfaceProvider
                )
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeImageAnalyzer { barcode ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            openQrContent(barcode)
                        }
                    })
                }

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind any bound use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to lifecycleOwner
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }



    private fun openQrContent(qrCode: String?) {
        stopCamera()
        processingBarcode.compareAndSet(true, false)
        Log.d(QRCodeImageAnalyzer::class.java.simpleName, "QR Code Found:\n$qrCode")
        Toast.makeText(this@MainActivity, "QR Code Found:\n$qrCode", Toast.LENGTH_SHORT)
            .show()
    }

    private fun stopCamera() {
        cameraProvider.unbindAll()
        mBinding.previewView.hide()
        mBinding.groupBtnInstructions.show()
    }

}