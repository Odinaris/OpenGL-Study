package com.odinaris.opengldemo;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DrawingRenderer implements GLSurfaceView.Renderer {

    private final List<Float> mPoints = new ArrayList<>();
    private final List<Float> mHaloPoints = new ArrayList<>();
    private final float[] mHaloColor = {1.0f, 1.0f, 1.0f, 0.5f}; // 50% transparent white
    private final float[] mLineColor = {1.0f, 1.0f, 1.0f, 0.5f}; // 50% transparent white
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  gl_PointSize = 10.0;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mProgram = createProgram();
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 2, GLES30.GL_FLOAT, false, 0, getFloatBuffer(mPoints));
        GLES30.glUniform4fv(mColorHandle, 1, mLineColor, 0);
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, mPoints.size() / 2);
        GLES30.glDisableVertexAttribArray(mPositionHandle);

        // Draw halo
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 2, GLES30.GL_FLOAT, false, 0, getFloatBuffer(mHaloPoints));
        GLES30.glUniform4fv(mColorHandle, 1, mHaloColor, 0);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mHaloPoints.size() / 2);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }


    private int createProgram() {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);
        return program;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    private FloatBuffer getFloatBuffer(List<Float> points) {
        float[] pointArray = new float[points.size()];
        for (int i = 0; i < points.size(); i++) {
            pointArray[i] = points.get(i);
        }
        FloatBuffer buffer = ByteBuffer.allocateDirect(pointArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(pointArray).position(0);
        return buffer;
    }

    public void addPoint(float x, float y) {
        mPoints.add(x);
        mPoints.add(y);
        mHaloPoints.add(x);
        mHaloPoints.add(y);
    }
}
