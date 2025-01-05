package com.odinaris.opengldemo.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.odinaris.opengldemo.R;
import com.odinaris.opengldemo.widget.MyGLSurfaceView;

public class PathLineActivity extends AppCompatActivity {

    private static final String TAG = "PathActivity";

    private MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        mGLSurfaceView = new MyGLSurfaceView(this);
        addContentView(mGLSurfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}