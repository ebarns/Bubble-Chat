package depaul.cdm.csc372.erikbarnsfinalproject;

/**
 * Created by Erik Barns on 11/7/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.graphics.Color;
import android.view.View;

import java.util.Random;

public class Drawing extends View {
    private boolean clear = false;
    private Path drawPath;
    private Paint drawpaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private Paint drawPaint = new Paint();
    private Path path = new Path();

    public Drawing(Context context) {
        super(context);
        setup();
    }

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();

    }

    //if clear is true clear the canvas, else draw path
    @Override
    protected void onDraw(Canvas canvas) {
        if(clear) {
            path = new Path();
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRect(0, 0, 0, 0, clearPaint);
            clear = false;
        }
        canvas.drawPath(path, drawPaint);

    }

    public void clearCanvas(){
        clear = true;
        invalidate();
    }

    //ontouch down establish path orign
    //ontouch move, draw line to new point and update points
    //invalidate to update canvas
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float mvX = event.getX();
        float mvY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            path.moveTo(mvX, mvY);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(mvX,mvY);
        } else {
            return false;
        }
        invalidate();
        return true;
    }
    public void setDrawColor(int color){
        drawPaint.setColor(getContext().getResources().getColor(color));
    }

    //setup canvas
    public void setup() {
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
}