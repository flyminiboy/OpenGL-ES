package com.gpf.learnopengles

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.View
import com.gpf.learnopengles.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.mainGlSurface.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mainGlSurface.onPause()
    }

}