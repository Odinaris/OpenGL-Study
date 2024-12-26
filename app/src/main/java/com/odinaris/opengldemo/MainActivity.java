package com.odinaris.opengldemo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = -((event.getX() / (float) mGLSurfaceView.getWidth()) * 2 - 1);
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