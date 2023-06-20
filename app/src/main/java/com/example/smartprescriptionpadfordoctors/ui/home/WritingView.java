package com.example.smartprescriptionpadfordoctors.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class WritingView extends View {

    private Paint paint;
    private Path path;
    private boolean isErasing;

    public WritingView(Context context) {
        super(context);
        init();
    }

    public WritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WritingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5f);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isErasing) {
                    // Start erasing by finding and removing the closest point to the touch coordinates
                    eraseDrawing(x, y);
                } else {
                    // Start drawing
                    path.moveTo(x, y);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isErasing) {
                    // Continue drawing
                    path.lineTo(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                // Do any additional logic when touch is released
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private void eraseDrawing(float x, float y) {
        Path updatedPath = new Path();

        PathMeasure pathMeasure = new PathMeasure(path, false);
        float[] segmentStart = new float[2];
        float distance = 0;

        while (pathMeasure.getLength() > distance) {
            pathMeasure.getPosTan(distance, segmentStart, null);

            if (isPointWithinEraserRange(segmentStart[0], segmentStart[1], x, y)) {
                // Move the path measure to the end of the segment
                float[] segmentEnd = new float[2];
                pathMeasure.getPosTan(distance + 1, segmentEnd, null);
                float segmentEndX = segmentEnd[0];
                float segmentEndY = segmentEnd[1];

                // Add a new segment to the updated path with the erased portion
                updatedPath.moveTo(segmentStart[0], segmentStart[1]);
                updatedPath.lineTo(segmentEndX, segmentEndY);

                // Move the distance to the end of the erased segment
                distance += 1;
            } else {
                // Add the segment to the updated path as it is
                if (distance == 0) {
                    updatedPath.moveTo(segmentStart[0], segmentStart[1]);
                } else {
                    updatedPath.lineTo(segmentStart[0], segmentStart[1]);
                }
                distance += 1;
            }
        }

        // Replace the original path with the updated path
        path = updatedPath;
        invalidate();
    }

    private boolean isPointWithinEraserRange(float pointX, float pointY, float eraserX, float eraserY) {
        // Set the eraser range as a circle around the touch point
        float eraserRadius = 10f; // Adjust the size as needed

        float distanceSquared = (pointX - eraserX) * (pointX - eraserX) + (pointY - eraserY) * (pointY - eraserY);
        return distanceSquared <= eraserRadius * eraserRadius;
    }


    public void setErasing(boolean erasing) {
        isErasing = erasing;
    }
}
