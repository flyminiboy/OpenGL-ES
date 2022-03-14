package com.gpf.learnopengles

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import com.gpf.learnopengles.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    val cm by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    val cameraThread = HandlerThread("camera2")
    val cameraHandler by lazy {
        cameraThread.start()
        Handler(cameraThread.looper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)


        }

    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        // CameraCharacteristics 描述相机设备的属性

        val cameraId = CameraCharacteristics.LENS_FACING_BACK.toString()
        val characteristics = cm.getCameraCharacteristics(cameraId)
        cm.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
            }

        }, cameraHandler)
    }
}