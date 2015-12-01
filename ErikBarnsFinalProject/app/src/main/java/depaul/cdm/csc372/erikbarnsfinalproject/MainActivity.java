package depaul.cdm.csc372.erikbarnsfinalproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class MainActivity extends Activity {
    private static int colorindex = 0;
    private static String logtag = "CameraApp8";
    private static final int REQ_CAP =1;
    private static final int REQ_DRAW = 2;
    private static final int REQ_RM = 3;
    ImageView result_photo;
    private ArrayList<byte[]> bubbles2;
    ArrayList<Bitmap> bubbles  = new ArrayList<Bitmap>();
    ArrayList<Calendar> times = new ArrayList<Calendar>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bubbles2 = new ArrayList<byte[]>();
        ImageView b = (ImageView) findViewById(R.id.button_camera);
        result_photo = (ImageView) findViewById(R.id.image_camera);
        ImageView r = (ImageView) findViewById(R.id.button_room);
        ImageView d = (ImageView) findViewById(R.id.button_draw);
        ImageView i = (ImageView) findViewById(R.id.button_info);
        b.setOnClickListener(cameraListener);
        r.setOnClickListener(roomListener);
        d.setOnClickListener(drawListener);
        i.setOnClickListener(infoListener);
    }

    private View.OnClickListener cameraListener = new View.OnClickListener(){
        public void onClick(View v){
            takePhoto(v);
        }
    };
    private View.OnClickListener roomListener = new View.OnClickListener(){
        public void onClick(View v){
            bubbleRoom(v);
        }
    };
    private View.OnClickListener drawListener = new View.OnClickListener(){
        public void onClick(View v){
            drawingRoom(v);
        }
    };
    private View.OnClickListener infoListener = new View.OnClickListener(){
        public void onClick(View v){
            infoRoom(v);
        }
    };

    //go to info room
    public void infoRoom(View v){
        Intent intent = new Intent(this, infopage.class);
        startActivity(intent);
    }
    //go to drawing room, expect drawing result
    public void drawingRoom(View v){
        Intent intent = new Intent(this, Drawing_room.class);
        startActivityForResult(intent, REQ_DRAW);
    }
    //go to bubble room
    //if bubble surpassed 200 seconds, delete time and bubble
    public void bubbleRoom(View v){
        Calendar now = Calendar.getInstance();
        ArrayList<byte[]> temp = new ArrayList<byte[]>(bubbles2);
        ArrayList<Calendar> tempcal = new ArrayList<Calendar>(times);
        int size = 0;
        for (byte[] x : bubbles2){
            size += x.length;
        }
   //     System.out.println("size: "  +size);

        //removing bubble to account for intent memory issues
        while(size > 475000){
            byte[] tempbyte = temp.get(0);
            temp.remove(tempbyte);
            tempcal.remove(tempcal.get(0));
            size -= tempbyte.length;
          //  System.out.println("new size: " + size);
        }
//        bubbles2 = new ArrayList<byte[]>(temp);
//        times = new ArrayList<Calendar>(tempcal);
        if(bubbles2.size() > 0) {
            for (Calendar time : tempcal) {
//                //if bubble has existed for 200 seconds pop
                long difference = now.getTimeInMillis() - time.getTimeInMillis();
                difference = difference / (1000);
                if (difference > 200) {

                    int remove_index = tempcal.indexOf(time);
                    temp.remove(remove_index);
                    tempcal.remove(remove_index);
                }
            }
        }
        //pass room color and bubbles byte array
            bubbles2 = new ArrayList<byte[]>(temp);
            times = new ArrayList<Calendar>(tempcal);
            Intent intent = new Intent(this, bubbleRoom.class);
            intent.putExtra("Bytes", bubbles2);
            intent.putExtra("colorIn",colorindex);
           // intent.putExtra("bubbles",bubbles);
            startActivityForResult(intent,REQ_RM);
    }
    //open camera intent
    public void takePhoto(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQ_CAP);
    }

    //crops image to circle bitmap scales bitmap to larger size
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

        //creates circular canvas
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //draw bitmap into canvas
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap scaled = Bitmap.createScaledBitmap(output, (int)(output.getWidth()*1.75),(int)(output.getHeight()  *1.75), true);
        return scaled;//
       // return output;
}

    //camera result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        //super.onActivityResult(requestCode,resultCode,intent);
        //if from camera, crop image and store in byte array. add time to array
        if(requestCode == REQ_CAP && resultCode == Activity.RESULT_OK){
            Bundle extras = intent.getExtras();
            Calendar bublife = Calendar.getInstance();
            times.add(bublife);
            Bitmap photo = (Bitmap) extras.get("data");
            photo = getCroppedBitmap(photo);
            //result_photo.setImageBitmap(photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 75, stream);
            byte[] bytes = stream.toByteArray();
            bubbles2.add(bytes);
            Toast.makeText(getBaseContext(),"A picture bubble has been added to a room!",
                    Toast.LENGTH_SHORT).show();

        }
        //if bubble room then store bubble room color
        else if(requestCode == REQ_RM && resultCode == Activity.RESULT_OK){
//            Bundle extras = intent.getExtras();
            colorindex = (int) intent.getIntExtra("colorindex",0);
        }
        //if drawing room add to byte array. add time to array
        else if(requestCode == REQ_DRAW && resultCode == Activity.RESULT_OK){

            byte[] bytes = intent.getByteArrayExtra("BMP");
            bubbles2.add(bytes);
            Calendar bublife = Calendar.getInstance();
            times.add(bublife);
            Toast.makeText(getBaseContext(),"A drawing bubble has been added to a room!",
                    Toast.LENGTH_SHORT).show();
        }
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
