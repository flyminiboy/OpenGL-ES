package com.gpf.learnopengles;

import static android.opengl.GLES30.GL_COMPILE_STATUS;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GLUtils {

    private static String loadShaderSource(Context context, int resId) {

        StringBuilder sb = new StringBuilder();

        BufferedReader br = null;
        try {
            InputStream is = context.getResources().openRawResource(resId);
            br = new BufferedReader(new InputStreamReader(is));

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (Exception e) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                } finally {
                    br = null;
                }
            }
        }


        return sb.toString();
    }

    /**
     * 加载着色器
     * @param type 着色器类型 顶点着色器-GL_VERTEX_SHADER / 片元着色器-GL_FRAGMENT_SHADER
     * @param shaderSource 着色器资源
     * @return
     */
    public static int loadShader(int type, String shaderSource) {

        int res = GLES30.GL_FALSE;

        do {
            // 创建一个空的着色器对象，返回一个可以引用的非零值
            res = GLES30.glCreateShader(type);
            if (res == GLES30.GL_FALSE) {
                break; // 创建失败
            }
            // 将着色器中的源代码设置为string指定的字符串数组中的源代码
            // 要被替换源代码的着色器对象的句柄
            // 指定指向包含要加载到着色器的源代码的字符串
            GLES30.glShaderSource(res, shaderSource);
            // 编译着色器
            // 要被编译着色器对象的句柄
            GLES30.glCompileShader(res);
            //检查编译状态
            int[] compiled = new int[1];
            GLES30.glGetShaderiv(res, GL_COMPILE_STATUS, compiled, 0);
            int compiledRes = compiled[0];
            if (compiledRes == GLES30.GL_FALSE) {
                Log.e(AnyHelperKt.TAG, GLES30.glGetShaderInfoLog(res));
                GLES30.glDeleteShader(res);
                break; // 编译失败
            }
        } while (false);

        return res;
    }

    public static int createAndLinkProgram(Context context, int vertextShaderResId, int fragmentShaderResId) {

        int program = GLES30.GL_FALSE;

        do {

            // 加载着色器
            int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, loadShaderSource(context, vertextShaderResId));
            if (vertexShader == GLES30.GL_FALSE) {
                break;
            }
            int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, loadShaderSource(context, fragmentShaderResId));
            if (fragmentShader == GLES30.GL_FALSE) {
                break;
            }

            // 创建空program，返回可以被引用的非零值
            program = GLES30.glCreateProgram();
            if (program <= 0) {
                break;
            }

            // 将着色器附加到program
            GLES30.glAttachShader(program, vertexShader);
            GLES30.glAttachShader(program, fragmentShader);
            // 链接程序
            GLES30.glLinkProgram(program);
            //检查连接状态
            int[] linked = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linked, 0);
            if (linked[0] == GLES30.GL_FALSE) {
                GLES30.glDeleteProgram(program);
                break;
            }

        }while (false);

        return program;
    }

}
