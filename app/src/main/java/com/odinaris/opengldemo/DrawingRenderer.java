package com.odinaris.opengldemo;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DrawingRenderer implements GLSurfaceView.Renderer {

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

    private float[] mPoints = new float[1024]; // 预分配足够大的数组
    private float[] mHaloPoints = new float[1024];
    private int mPointCount = 0;
    private int mHaloPointCount = 0;
    private final float[] mHaloColor = {1.0f, 1.0f, 1.0f, 0.5f}; // 50% transparent white
    private final float[] mLineColor = {1.0f, 1.0f, 1.0f, 0.5f}; // 50% transparent white
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private int mVBO;
    private int mVAO;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mProgram = createProgram();
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        // Initialize VAO and VBO
        int[] vaoIds = new int[1];
        GLES30.glGenVertexArrays(1, vaoIds, 0);
        mVAO = vaoIds[0];
        GLES30.glBindVertexArray(mVAO);

        int[] vboIds = new int[1];
        GLES30.glGenBuffers(1, vboIds, 0);
        mVBO = vboIds[0];
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);

        GLES30.glVertexAttribPointer(mPositionHandle, 2, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);

        updateVBO();

        GLES30.glBindVertexArray(mVAO);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);

        GLES30.glUniform4fv(mColorHandle, 1, mLineColor, 0);
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, mPointCount / 2);

        GLES30.glUniform4fv(mColorHandle, 1, mHaloColor, 0);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mHaloPointCount / 2);

        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
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

        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES30.glDeleteProgram(program);
            throw new RuntimeException("Error linking program: " + GLES30.glGetProgramInfoLog(program));
        }

        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        return program;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            GLES30.glDeleteShader(shader);
            throw new RuntimeException("Error compiling shader: " + GLES30.glGetShaderInfoLog(shader));
        }

        return shader;
    }

    private void updateVBO() {
        if (mPointCount > 0 || mHaloPointCount > 0) {
            FloatBuffer buffer = ByteBuffer.allocateDirect((mPointCount + mHaloPointCount) * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int i = 0; i < mPointCount; i++) {
                buffer.put(mPoints[i]);
            }
            for (int i = 0; i < mHaloPointCount; i++) {
                buffer.put(mHaloPoints[i]);
            }
            buffer.position(0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, (mPointCount + mHaloPointCount) * 4, buffer, GLES30.GL_DYNAMIC_DRAW);
        }
    }

    public void addPoint(float x, float y) {
        if (mPointCount >= mPoints.length || mHaloPointCount >= mHaloPoints.length) {
            // 扩展数组容量
            float[] newPoints = new float[mPoints.length * 2];
            System.arraycopy(mPoints, 0, newPoints, 0, mPointCount);
            mPoints = newPoints;

            float[] newHaloPoints = new float[mHaloPoints.length * 2];
            System.arraycopy(mHaloPoints, 0, newHaloPoints, 0, mHaloPointCount);
            mHaloPoints = newHaloPoints;
        }

        mPoints[mPointCount++] = x;
        mPoints[mPointCount++] = y;
        mHaloPoints[mHaloPointCount++] = x;
        mHaloPoints[mHaloPointCount++] = y;
    }
}
