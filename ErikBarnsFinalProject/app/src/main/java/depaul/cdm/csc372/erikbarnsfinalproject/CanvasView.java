package depaul.cdm.csc372.erikbarnsfinalproject;

/**
 * Created by Erik Barns on 11/5/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {

    private int width, height;
    private boolean done;
    private boolean surfaceAvailble;

    private long frameCount = 0;
    private long timeStart = 0;

    private SurfaceHolder holder;

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener listener;
    private float[] sensorData = new float[3];

    private static final int MAX_V = 15;
    private static final float GRAVITY_F = 1;
    private ArrayList<Bitmap> bubbles = new ArrayList<Bitmap>();
    private ArrayList<byte[]> bubbles2;

    private int currentColor = R.color.bgblue;

    //on touch open any bubble who is within radius of touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for(MyShape s : shapes){
                    MyBitmap temp = (MyBitmap) s;
                    int tempheight = temp.bitmap.getHeight()/2;
                    if( in_circle((int)temp.x,(int)temp.y,tempheight,X,Y-tempheight)){
                            openImage(temp.bitmap);
                    }
                }
        }
        return true;
    }
    //onclick of bubble open bubble to view enlarged image
    //pass image and current color
    public void openImage(Bitmap bit){
        Intent intent = new Intent(getContext(), bubbleImage.class);
        intent.putExtra("BitmapImage", bit);
        intent.putExtra("bgcolor",getCurrentColor());
        getContext().startActivity(intent);
    }
    public boolean in_circle(int px, int py, int radius,int x,int y) {
        double dist = Math.sqrt(Math.pow((px - x),2) + Math.pow((py - y),2));
        return dist <= radius;
    }
    class MyShape {
        ShapeDrawable drawable;
        float dx = 5, dy = 5;

        MyShape(Shape shape) {
            drawable = new ShapeDrawable(shape);
        }

        MyShape() {
        }

        void move() {
            float gx = sensorData[1]; // landscape mode
            float gy = sensorData[0]; // landscape mode

            dx += gx * GRAVITY_F;
            dy += gy * GRAVITY_F / 2;
            dx = Math.min(Math.max(dx, -MAX_V), MAX_V);
            dy = Math.min(Math.max(dy, -MAX_V), MAX_V);
            Rect bounds = drawable.getBounds();
            //if shape has reached bounds of screen then reverse the direction
            if (bounds.right >= width && dx > 0 || bounds.left < 0 && dx < 0) dx = -dx;
            if (bounds.bottom >= height && dy > 0 || bounds.top < 0 && dy < 0) dy = -dy;

            bounds.left += dx;
            bounds.right += dx;
            bounds.top += dy;
            bounds.bottom += dy;
        }

        void setBounds(int left, int top, int right, int bottom) {
            drawable.setBounds(left, top, right, bottom);
        }

        void setVelocity(float dx, float dy) {
            this.dx = 1;
            this.dy = 1;
        }

        void setColor(int color) {
            if (drawable != null) drawable.getPaint().setColor(color);
        }

        void draw(Canvas canvas) {
            drawable.draw(canvas);
        }
    }

    class MyBitmap extends MyShape {
        Bitmap bitmap;
        float x, y;

        MyBitmap(Bitmap b) {
            bitmap = b;

        }

        void setBounds(int left, int top, int right, int bottom) {
            x = left;
            y = top;
        }
        //calculate amount to move based on gravity * gyroscope sensor.
        void move() {
            float gx = sensorData[1];
            float gy = sensorData[0];
            dx += gx * GRAVITY_F / 2;
            dy += gy * GRAVITY_F / 2;

            dx = Math.min(Math.max(dx, -MAX_V), MAX_V);
            dy = Math.min(Math.max(dy, -MAX_V), MAX_V);
            //if shape has reached bounds of screen then reverse the direction
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if (x + w >= width && dx > 0 || x < 0 && dx < 0) dx = -dx;
            if (y + h >= height && dy > 0 || y < 0 && dy < 0) dy = -dy;
            //set new coordinates
            x += dx;
            y += dy;
        }
        void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, x, y, null);
        }

    }

    //// SurfaceHolder.Callback

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceAvailble = true;
        startAnimation();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        stopAnimation();
        synchronized (holder) {
            positionShapes();
        }
        startAnimation();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceAvailble = false;
        stopAnimation();
    }

    //////convert byte to bit -- init shapes
    public void getList(ArrayList<byte[]> b2) {
        bubbles2 = b2;
        for (byte[] bytes : bubbles2) {
            Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bubbles.add(temp);
        }
        initShapes();
    }

    private List<MyShape> shapes = new ArrayList<MyShape>();

    private Random random = new Random();

    private Paint paint = new Paint();

    public CanvasView(Context context) {
        super(context);
        init();
        //setup();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // setup();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        //bubbles = b;
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        initSensor(Sensor.TYPE_GYROSCOPE);

    }

    //initialize sensor
    private void initSensor(int type) {
        sensor = sensorManager.getDefaultSensor(type);
        if (sensor != null) {
            listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    for (int i = 0; i < 3; i++)
                        sensorData[i] = event.values[i];
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        }
    }
    //convert byte to bitmap
    public void byteArraytoBit(ArrayList<byte[]> b) {
      //  System.out.println("DRAWING SIZE: " + b.size());
        for (byte[] bytes : b) {
            Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bubbles.add(temp);
        }
    }

    //init bubbles
    public void initShapes() {
        shapes.clear();
        for (Bitmap pic : bubbles) {
            shapes.add(new MyBitmap(pic));

        }
    }
    //canvas color getter and setter
    public void setBg(int color){
        currentColor = color;
    }
    public int getCurrentColor(){
        return currentColor;
    }

    //on restart, stop animation, set new bg, initialize shapes, initialize sensor and start animation
    public void restart(int n) {
        if (width > 0 && height > 0) {
            stopAnimation();
            setBg(n);
            synchronized (holder) {
                initShapes();
                positionShapes();
            }
            if (sensor != null) {
                initSensor(Sensor.TYPE_GYROSCOPE);
            }
            startAnimation();
        }
    }
    //starts animation
    public void startAnimation() {
        done = false;
        if (listener != null)
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        if (surfaceAvailble) startRenderingThread();
    }
    //starts animated thread
    private void startRenderingThread() {
        frameCount = 0;
        timeStart = System.currentTimeMillis();
        new Thread(new Runnable() {
            public void run() {
                while (!done) {
                    Canvas c = null;
                    try {
                        c = holder.lockCanvas();
                        synchronized (holder) {
                            doDraw(c);
                        }
                    } finally {
                        if (c != null) {
                            holder.unlockCanvasAndPost(c);
                        }
                    }
                }
            }
        }).start();

    }
    //stops animation
    public void stopAnimation() {
        done = true;
        if (listener != null)
            sensorManager.unregisterListener(listener);
    }
    //establish initial position of shapes
    private void positionShapes() {
        for (MyShape s : shapes) {
            int w = random.nextInt(50);
            int h = random.nextInt(50);
            int x = random.nextInt(width - 2 * w) + w;
            int y = random.nextInt(height - 2 * h) + h;
            s.setColor(getRandomColor());
            s.setBounds(x, y, x + w, y + h);
            s.setVelocity(1,
                    1);
        }
    }

    //calculate move, draw move
    protected void doDraw(Canvas canvas) {
        canvas.drawARGB(0, 225, 225, 255);
        canvas.drawColor(getResources().getColor(getCurrentColor()));
        for (MyShape shape : shapes) {
            shape.move();
            shape.draw(canvas);
        }
    }


    private static int getRandomColor() {
        Random random = new Random();
        return Color.argb(random.nextInt(230) + 26, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

}

