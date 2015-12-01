package depaul.cdm.csc372.erikbarnsfinalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Erik Barns on 11/10/2015.
 */
public class bubbleImage extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bubbleimageview);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Bitmap photo = (Bitmap) extras.get("BitmapImage");
        int color = (int) extras.get("bgcolor");
        //SET IMAGE AND SET BACKGROUND COLOR
        ImageView result_photo = (ImageView) findViewById(R.id.bubimageview);
        View root = result_photo.getRootView();
        root.setBackgroundColor(getResources().getColor(color));
        Bitmap _bmp = Bitmap.createScaledBitmap(photo, (int)(photo.getWidth()*3),(int)(photo.getHeight()  *3), true);
        result_photo.setImageBitmap(_bmp);

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backlisten);

    }
    //finish on click
    private View.OnClickListener backlisten = new View.OnClickListener(){
        public void onClick(View v){
            finish();
        }
    };
}
