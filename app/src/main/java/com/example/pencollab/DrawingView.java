package com.example.pencollab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import java.util.ArrayList;

public class DrawingView extends View {

    private final ArrayList<SerializedPath> paths = new ArrayList<>();
    private Paint drawPaint;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(10); // Taille du trait
    }

    public void setDrawingColor(int color) {
        drawPaint.setColor(color);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (SerializedPath serializedPath : paths) {
            drawPaint.setColor(serializedPath.color);
            canvas.drawPath(serializedPath.getPath(), drawPaint);
        }
    }

    public View getDrawingPreview() {
        return new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                for (SerializedPath serializedPath : paths) {
                    drawPaint.setColor(serializedPath.color);
                    canvas.drawPath(serializedPath.getPath(), drawPaint);
                }
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Path newPath = new Path();
                newPath.moveTo(touchX, touchY);
                paths.add(new SerializedPath(newPath, drawPaint.getColor()));
                return true;
            case MotionEvent.ACTION_MOVE:
                SerializedPath currentPath = paths.get(paths.size() - 1);
                currentPath.addPoint(touchX, touchY);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }


    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(paths);
    }

    public void fromJSON(String json) {
        Gson gson = new Gson();
        SerializedPath[] deserializedPaths = gson.fromJson(json, SerializedPath[].class);
        paths.clear();
        for (SerializedPath serializedPath : deserializedPaths) {
            paths.add(serializedPath);
        }
        invalidate();
    }

    public class SerializedPath {
        private final ArrayList<PointF> points;
        private final int color;

        public SerializedPath(Path path, int color) {
            this.color = color;
            this.points = new ArrayList<>();
            PathMeasure measure = new PathMeasure(path, false);
            float[] coords = new float[2];
            for (float i = 0; i < measure.getLength(); i += 1) {
                measure.getPosTan(i, coords, null);
                points.add(new PointF(coords[0], coords[1]));
            }
        }

        public Path getPath() {
            Path path = new Path();
            if (!points.isEmpty()) {
                PointF startPoint = points.get(0);
                path.moveTo(startPoint.x, startPoint.y);
                for (int i = 1; i < points.size(); i++) {
                    PointF point = points.get(i);
                    path.lineTo(point.x, point.y);
                }
            }
            return path;
        }

        public int getColor() {
            return color;
        }

        public void addPoint(float x, float y) {
            points.add(new PointF(x, y));
        }
    }

}
