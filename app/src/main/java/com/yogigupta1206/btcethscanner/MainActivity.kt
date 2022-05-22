package com.yogigupta1206.btcethscanner

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.yogigupta1206.btcethscanner.databinding.ActivityMainBinding
import com.yogigupta1206.utils.CALL_DEVELOPER
import com.yogigupta1206.utils.CALL_REQUEST
import com.yogigupta1206.utils.PERMISSION_REQUEST_CAMERA
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        setOnNavigationItemSelectListener()
        setClickListeners()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        requestCamera()
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
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindCameraPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT)
                    .show()
            } catch (e: InterruptedException) {
                Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        mBinding.previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        mBinding.previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
        val preview: Preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(mBinding.previewView.surfaceProvider)
        val camera: Camera =
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }
}