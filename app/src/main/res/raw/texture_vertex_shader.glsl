// 声明着色器使用OpenGL ES着色语言3.0版本
#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aTextureCoord;
//纹理矩阵
uniform mat4 uTextureMatrix;
out vec2 yuvTexCoords;
void main() {
    gl_Position  = vPosition;
    gl_PointSize = 10.0;
    //只保留x和y分量
    yuvTexCoords = (uTextureMatrix * aTextureCoord).xy;
}

/**

数据类型

layout 布局限定符

in 顶点输入变量用于指定顶点着色器中每个顶点的输入
out 顶点输出变量，每个顶点着色器将在一个或多个输出变量中输出需要传递给片段着色器的数据，
    这些变量会在片段着色器中声明为 in 变量
    顶点着色器输出变量/片段着色器输入变量不能有布局限定符


 */