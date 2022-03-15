package com.gpf.learnopengles

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyGLRender(val glSurfaceView: GLSurfaceView) : GLSurfaceView.Renderer {

    val cm by lazy {
        glSurfaceView.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    val cameraThread = HandlerThread("camera2")
    val cameraHandler by lazy {
        cameraThread.start()
        Handler(cameraThread.looper)
    }
    val mainHandler = Handler()

    val permissions = arrayListOf(Manifest.permission.CAMERA)

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

    private val vertexBuffer by lazy {
        getFloatBuffer(vertext)
    }
    private val textureCoordBuffer by lazy {
        getFloatBuffer(textureCoord)
    }

    // 接收相机数据的纹理id
    val textureId = IntArray(1)
    private var surfaceTexture: SurfaceTexture?=null

    private var program:Int = 0

    fun getFloatBuffer(array: FloatArray): FloatBuffer? {
        //将顶点数据拷贝映射到 native 内存中，以便opengl能够访问
        val buffer: FloatBuffer = ByteBuffer
            .allocateDirect(array.size * 4) //直接分配 native 内存，不会被gc
            .order(ByteOrder.nativeOrder()) //和本地平台保持一致的字节序（大/小头）
            .asFloatBuffer() //将底层字节映射到FloatBuffer实例，方便使用

        buffer.put(array) //将顶点拷贝到 native 内存中
            .position(0) //每次 put position 都会 + 1，需要在绘制前重置为0
        return buffer
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        // 创建纹理对象 ==> 得到只是一个纹理id
        GLES30.glGenTextures(textureId.size, textureId, 0)

        // 将纹理绑定到SurfaceTexture
        surfaceTexture = SurfaceTexture(textureId[0]).apply {
            setOnFrameAvailableListener {
                logE("setOnFrameAvailableListener")
                glSurfaceView.requestRender()
            }

            mainHandler.post {
                // 开启相机预览
                PermissionX.init(glSurfaceView.context as FragmentActivity)
                    .permissions(permissions)
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            openCamera()
                        }
                    }
            }

        }

        program = GLUtils.createAndLinkProgram(glSurfaceView.context, R.raw.texture_vertex_shader, R.raw.texture_oes_fragtment_shader)
        // 设置清除渲染时的颜色
        GLES30.glClearColor(0f, 0f, 0f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0,0,width, height)
    }

    override fun onDrawFrame(gl: GL10?) {

        // 更新数据，其实也是消耗数据，将上一帧的数据处理或者抛弃掉，要不然SurfaceTexture是接收不到最新数据
        // 就会将一帧预览数据推送给外部纹理上
        surfaceTexture?.updateTexImage()

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        // 使用程序片段
        GLES30.glUseProgram(program)



        val vertexLoc = glGetAttribLocation(program, "a_Position")
        val textureLoc = glGetAttribLocation(program, "a_texCoord")

        glEnableVertexAttribArray(vertexLoc)
        glEnableVertexAttribArray(textureLoc)

        glVertexAttribPointer(
            vertexLoc,
            3,
            GL_FLOAT,
            false,
            0,
            vertexBuffer
        )

        glVertexAttribPointer(
            textureLoc,
            2,
            GL_FLOAT,
            false,
            0,
            textureCoordBuffer
        )

        // 激活纹理单元
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        //绑定外部纹理到纹理单元0
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId[0])

        //设置纹理过滤参数
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER,GL_NEAREST)
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE)

        //将此纹理单元传给片段着色器的uTextureSampler外部纹理采样器
        val uTextureLoc = glGetUniformLocation(program, "s_texture")
        glUniform1i(uTextureLoc, 0)

        glDrawArrays(GL_TRIANGLE_FAN, 0, vertext.size / 3)

        glDisableVertexAttribArray(vertexLoc)
        glDisableVertexAttribArray(textureLoc)
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        // CameraCharacteristics 描述相机设备的属性

        val cameraId = CameraCharacteristics.LENS_FACING_BACK.toString()
        cm.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {

                surfaceTexture!!.setDefaultBufferSize(1920, 1080)
                val surface = Surface(surfaceTexture)
                val surfaces = arrayListOf(surface)
                // 通过向相机设备提供 Surfaces 的目标输出集来创建新的相机捕获会话。
                // 第一个参数 作为捕获图像数据目标的一组新表面。
                camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {


                        val builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                        // 将表面添加到此请求的目标列表中
                        // 当向相机设备发出请求时，添加的 Surface 必须是最近一次调用 CameraDevice.createCaptureSession 中包含的 Surface 之一。
                        // 多次添加目标无效。
                        // 参数：
                        // outputTarget - 用作此请求的输出目标的表面
                        builder.addTarget(surface)
                        val request = builder.build()
                        session.setRepeatingRequest(request, object : CameraCaptureSession.CaptureCallback() {
                            override fun onCaptureCompleted(
                                session: CameraCaptureSession,
                                request: CaptureRequest,
                                result: TotalCaptureResult
                            ) {
                                super.onCaptureCompleted(session, request, result)
                            }
                        }, null)

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        logE("创建相机会话失败")
                    }

                },null)
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
            }

        }, cameraHandler)
    }

}