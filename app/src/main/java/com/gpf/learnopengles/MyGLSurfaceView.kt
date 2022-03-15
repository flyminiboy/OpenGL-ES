package com.gpf.learnopengles

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView(context: Context, attrs: AttributeSet?=null) : GLSurfaceView(
    context, attrs
) {

    init {
        setEGLContextClientVersion(3)
        setRenderer(MyGLRender(this))
        // 设置刷新模式
        renderMode = RENDERMODE_WHEN_DIRTY
    }

}