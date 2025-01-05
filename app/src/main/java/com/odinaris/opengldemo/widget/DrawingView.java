package com.odinaris.opengldemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint paint;
    private Path currentPath;
    private List<Path> paths;
    private float startX, startY;
    private boolean drawing = false;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        paths = new ArrayList<>();
        currentPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
        if (drawing) {
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                currentPath.moveTo(x, y);
                drawing = true;
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawing = false;
                connectToNearestClosedPath(startX, startY, x, y);
                currentPath.close();
                paths.add(new Path(currentPath));
                currentPath.reset();
                invalidate();
                break;
        }
        return true;
    }

    private void connectToNearestClosedPath(float startX, float startY, float endX, float endY) {
        Path nearestStartPath = findNearestPath(startX, startY);
        Path nearestEndPath = findNearestPath(endX, endY);

        if (nearestStartPath != null) {
            float[] nearestPoint = getNearestPointOnPath(nearestStartPath, startX, startY);
            currentPath.moveTo(nearestPoint[0], nearestPoint[1]);
        } else {
            currentPath.moveTo(startX, startY);
        }

        if (nearestEndPath != null && !nearestEndPath.equals(nearestStartPath)) {
            float[] nearestPoint = getNearestPointOnPath(nearestEndPath, endX, endY);
            currentPath.lineTo(nearestPoint[0], nearestPoint[1]);
        } else {
            currentPath.lineTo(endX, endY);
        }
    }

    private Path findNearestPath(float x, float y) {
        Path nearestPath = null;
        float minDistance = Float.MAX_VALUE;

        for (Path path : paths) {
            float distance = getMinDistanceFromPath(path, x, y);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPath = path;
            }
        }

        return nearestPath;
    }

    private float getMinDistanceFromPath(Path path, float x, float y) {
        PathMeasure measure = new PathMeasure(path, false);
        float length = measure.getLength();
        float[] position = new float[2];
        float minDistance = Float.MAX_VALUE;

        for (float t = 0; t <= length; t += 1) {
            measure.getPosTan(t, position, null);
            float dx = position[0] - x;
            float dy = position[1] - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    private float[] getNearestPointOnPath(Path path, float x, float y) {
        PathMeasure measure = new PathMeasure(path, false);
        float length = measure.getLength();
        float[] position = new float[2];
        float minDistance = Float.MAX_VALUE;
        float[] nearestPoint = new float[2];

        for (float t = 0; t <= length; t += 1) {
            measure.getPosTan(t, position, null);
            float dx = position[0] - x;
            float dy = position[1] - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint[0] = position[0];
                nearestPoint[1] = position[1];
            }
        }

        return nearestPoint;
    }
}