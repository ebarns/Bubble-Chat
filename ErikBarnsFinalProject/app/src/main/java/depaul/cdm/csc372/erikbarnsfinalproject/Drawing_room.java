package depaul.cdm.csc372.erikbarnsfinalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
/**
 * Created by Erik Barns on 11/7/2015.
 */
public class Drawing_room extends Activity {
    Spinner numObjects;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        ImageView back = (ImageView) findViewById(R.id.back);
        ImageView done = (ImageView) findViewById(R.id.done);
        TextView clear = (TextView) findViewById(R.id.clearcanvas);
        final Drawing d = (Drawing) findViewById(R.id.d1);
        //clear canvas
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                d.clearCanvas();

            }
        });
        //leave drawing room
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        //finished drawing -- crop and convert to byte array
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawing d = (Drawing) findViewById(R.id.d1);
                Bitmap b = getBitmapFromView(d);
                b = scaleDownBitmap(b, 100, getBaseContext());
                b = getCroppedBitmap(b);

                Intent data = new Intent();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 75, stream);
                byte[] bytes = stream.toByteArray();
                data.putExtra("BMP",bytes);

                //data.putExtra("data",b);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        numObjects = (Spinner) findViewById(R.id.colorType);
        numObjects.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, canvasColors));


        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //    li.setBackgroundColor((Integer) numObjects.getSelectedItem());
                   d.setDrawColor((Integer) canvas_colors[numObjects.getSelectedItemPosition()]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        numObjects.setOnItemSelectedListener(listener);
    }

    static final String[] canvasColors = {
            "Black","Lavender","Blue","Light Blue", "Red","Green","Pink","Yellow","Turquoise"
    };
    static final Integer[] canvas_colors = {
            R.color.black,R.color.bgblue, R.color.blue, R.color.border, R.color.red, R.color.green, R.color.pink, R.color.yellow, R.color.turq
    };
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight()/2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //scale bitmap
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    //get bitmap from canvas
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
