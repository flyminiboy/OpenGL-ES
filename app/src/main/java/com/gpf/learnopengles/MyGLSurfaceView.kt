package com.gpf.learnopengles

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(
    context, attrs
) {

    private lateinit var render:MyGLRender

    init {
        setEGLContextClientVersion(3)

        render = MyGLRender(context)
        setRenderer(render)
    }

}