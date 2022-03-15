#version 300 es
//OpenGL ES3.0外部纹理扩展
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

uniform samplerExternalOES yuvTexSampler;
in vec2 yuvTexCoords;

// OpenGL ES 3.0中将2.0的attribute改成了in，顶点着色器的varying改成out，片段着色器的varying改成了in，也就是说顶点着色器的输出就是片段着色器的输入，另外uniform跟2.0用法一样
out vec4 gl_FragColor; // 需要注意 GLSL 3.0 的变动 OpenGL ES 2.0的gl_FragColor和gl_FragData在3.0中取消掉了，需要自己定义out变量作为片段着色器的输出颜色，如 out vec4 fragColor
void main() {
    gl_FragColor = texture(yuvTexSampler, yuvTexCoords); // 需要注意 GLSL 3.0 的变动 OpenGL ES 3.0的shader中没有texture2D和texture3D等了，全部使用texture替换
}