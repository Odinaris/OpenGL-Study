package com.odinaris.opengldemo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private DrawingRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mRenderer = new DrawingRenderer();
        mGLSurfaceView.setRenderer(mRenderer);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = -((event.getX() / (float) mGLSurfaceView.getWidth()) * 2 - 1); // 取反x轴坐标
        float y = -((event.getY() / (float) mGLSurfaceView.getHeight()) * 2 - 1);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mRenderer.addPoint(x, y);
                mGLSurfaceView.requestRender();
                break;
        }
        return true;
    }
}