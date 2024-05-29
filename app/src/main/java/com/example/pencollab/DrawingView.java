package com.example.pencollab;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pencollab.Activity.DrawingActivity;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.util.Objects;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

public class DrawingView extends View {
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap bitmap, loadBitmap = null;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint drawPaint;
    private boolean isEraserMode;
    private float x, y;
    private float penSize = 10; // Taille du trait par défaut
    private float eraserSize = 10; // Taille de la gomme par défaut
    private int Widht, Height;
    private int color = Color.BLACK;

    PorterDuffXfermode x_src_over, x_clear;


    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        x_src_over = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        x_clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        init();
    }

    public void init() {
        isEraserMode = false;
        path = new Path();
        drawPaint = new Paint();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(color);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(penSize);
        drawPaint.setXfermode(x_src_over);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        if (canvas == null) {
            canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT);
        }
        canvas.drawBitmap(bitmap, 0, 0, drawPaint);
        drawPaint.setXfermode(x_src_over);

        if (loadBitmap != null) canvas.drawBitmap(loadBitmap, 0, 0, bitmapPaint);
    }

    public int getColor() { return color; }
    public float getPenSize() { return penSize; }

    public void setSize(int width, int height) {
        Widht = width;
        Height = height;
        /*if (bitmap == null) {
            bitmap = Bitmap.createBitmap(Widht, Height, Bitmap.Config.ARGB_8888);
        }
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));*/

    }

    public void setDrawingColor(int color) {
        this.color = color;
        initializePen();
    }

    public void setPenSize(float penSize) {
        this.penSize = penSize;
        this.eraserSize = penSize;
        if(isEraserMode) initializeEraser();
        else initializePen();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, drawPaint);
    }

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        this.x = x;
        this.y = y;
        canvas.drawPath(path, drawPaint);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - this.x);
        float dy = Math.abs(y - this.y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
            this.x = x;
            this.y = y;
        }
        canvas.drawPath(path, drawPaint);
    }

    private void touchUp() {
        path.lineTo(x, y);
        canvas.drawPath(path, drawPaint);
        path.reset();
        if (isEraserMode) {
            drawPaint.setXfermode(x_clear);
        } else {
            drawPaint.setXfermode(x_src_over);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEraserMode) {
                    drawPaint.setXfermode(x_clear);
                } else {
                    drawPaint.setXfermode(x_src_over);
                }
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                if (isEraserMode) {
                    path.lineTo(this.x, this.y);
                    path.reset();
                    path.moveTo(x, y);
                }
                canvas.drawPath(path, drawPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                updateDrawing();
                break;
            default:
                break;
        }
        return true;
    }

    private void updateDrawing() {
        Context context = getContext();
        if (context instanceof DrawingActivity) {
            DrawingActivity drawingActivity = (DrawingActivity) context;
            drawingActivity.updateDrawing(toJSON());
        }
    }

    public void initializePen() {
        isEraserMode = false;
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(color);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(penSize);
        drawPaint.setXfermode(x_src_over);
    }

    public void initializeEraser() {
        isEraserMode = true;
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeWidth(eraserSize);
        drawPaint.setXfermode(x_clear);
    }

    public void clear() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public View getDrawingPreview() {
        return new View(getContext()) {
            @Override
            protected void onDraw(@NonNull Canvas canvas) {
                super.onDraw(canvas);

                if (loadBitmap != null) {
                    // Créer une matrice de mise à l'échelle
                    @SuppressLint("DrawAllocation")
                    Matrix matrix = new Matrix();
                    matrix.postScale(
                            (float) getWidth() / Widht,
                            (float) getHeight() / Height
                    );

                    // Appliquer la matrice au bitmap
                    @SuppressLint("DrawAllocation")
                    Bitmap scaledBitmap = Bitmap.createBitmap(loadBitmap, 0, 0, loadBitmap.getWidth(), loadBitmap.getHeight(), matrix, true);

                    // Dessiner le bitmap mis à l'échelle sur le canvas
                    canvas.drawBitmap(scaledBitmap, 0, 0, bitmapPaint);
                }

            }
        };
    }



    public String toJSON() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bitmap", encodedBitmap);

        return gson.toJson(jsonObject);
    }
    

    public void fromJSON(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String encodedBitmap = jsonObject.get("bitmap").getAsString();

        byte[] decodedBytes = Base64.decode(encodedBitmap, Base64.DEFAULT);

        loadBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // Export the drawing into a PNG file
    public boolean Export(@NonNull Context context, @NonNull String title) {
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

}
