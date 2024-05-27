package com.example.pencollab;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pencollab.Activity.DrawingActivity;
import com.example.pencollab.DataBase.Drawing;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class DrawingView extends View {


    private final ArrayList<SerializedPath> paths = new ArrayList<>();
    private Paint drawPaint;
    private boolean isEraserMode = false; // Flag to indicate eraser mode
    private int Width, Height;

    private float eraserSize = 20;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(Color.WHITE);
        setupPaint();
        Width = getWidth();
        Height = getHeight();
    }

    public void setSize(int widht, int height) {
        Width = widht;
        Height = height;
    }

    public void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(20); // Taille du trait
    }

    public void Eraser() {
        isEraserMode = true;
    }

    public void setEraserSize(float size) {
        eraserSize = size;
        Eraser();
    }

    public void setDrawingColor(int color) {
        //drawPaint.setXfermode(null); // Reset Xfermode
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
        updateDrawing();
        return true;
    }

    private void updateDrawing() {
        Context context = getContext();
        if (context instanceof DrawingActivity) {
            DrawingActivity drawingActivity = (DrawingActivity) context;
            drawingActivity.updateDrawing(toJSON());
        }
    }

    public View getDrawingPreview() {
        return new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                Matrix matrix = new Matrix();
                matrix.setScale(
                        (float) getWidth() / Width,
                        (float) getHeight() / Height
                );

                // Appliquer la matrice à chaque chemin avant de le dessiner
                for (SerializedPath serializedPath : paths) {
                    Path scaledPath = new Path(serializedPath.getPath());
                    scaledPath.transform(matrix);
                    drawPaint.setColor(serializedPath.color);
                    canvas.drawPath(scaledPath, drawPaint);
                }

            }
        };
    }


    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(paths);
    }

    public void fromJSON(String json) {
        Gson gson = new Gson();
        SerializedPath[] deserializedPaths = gson.fromJson(json, SerializedPath[].class);
        paths.clear();
        if (deserializedPaths == null) return;
        Collections.addAll(paths, deserializedPaths);
        invalidate();
    }

    // Export the drawing into a PNG file
    public boolean Export(@NonNull Context context, @NonNull String title) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the paths
        for (SerializedPath serializedPath : paths) {
            drawPaint.setColor(serializedPath.color);
            canvas.drawPath(serializedPath.getPath(), drawPaint);
        }

        // Save the Bitmap to a file
        String fileName = title + ".png";
        OutputStream fos;

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            fos = context.getContentResolver().openOutputStream(
                    Objects.requireNonNull(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
