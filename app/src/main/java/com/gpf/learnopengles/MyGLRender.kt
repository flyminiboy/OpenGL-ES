package com.gpf.learnopengles

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRender(val context: Context) : GLSurfaceView.Renderer {

    // opengl 坐标体系
    val vertext = floatArrayOf(
        -1f, 1f, 0.0f, // 左上
        -1f, -1f, 0.0f, // 左下
        1f, -1f, 0.0f, // 右下
        1f, 1f, 0.0f // 右上
    )

    // 纹理坐标
    val textureCoord = floatArrayOf(
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 0.0f
    )

    // 接收相机数据的纹理id
    val textureId = IntArray(1)
    private var surfaceTexture: SurfaceTexture?=null

    private var program:Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        // 创建纹理对象
        GLES30.glGenTextures(textureId.size, textureId, 0)
        // 将纹理绑定到SurfaceTexture
        surfaceTexture = SurfaceTexture(textureId[0])
        // OES纹理（扩展纹理）-> 作用就是实现YUV格式到RGB的自动转化
        program = GLUtils.createAndLinkProgram(context, R.raw.texture_vertex_shader, R.raw.texture_oes_fragtment_shader)
        // 设置清除渲染时的颜色
        GLES30.glClearColor(0f, 0f, 0f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0,0,width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture?.let { surfaceTexture ->
            // 获取新的纹理数据
            surfaceTexture.updateTexImage()



        }
    }

}