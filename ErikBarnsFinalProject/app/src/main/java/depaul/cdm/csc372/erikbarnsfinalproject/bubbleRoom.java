package depaul.cdm.csc372.erikbarnsfinalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * Created by Erik Barns on 11/5/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class bubbleRoom extends Activity {

    //ArrayList<Bitmap> bubs;
    CanvasView animView;
    Spinner numObjects;
    Spinner sensorType;
    ArrayList<Bitmap> bubs;
    ArrayList<byte[]> bubs2;
    private int colorIndex;
//    LinearLayout li=(LinearLayout)findViewById(R.id.home);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);
        //initialize sensor, spinner color list, canvas
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        colorIndex = (int) extras.get("colorIn");
        bubs = new ArrayList<Bitmap>();
        bubs2 = (ArrayList<byte[]>) extras.get("Bytes");
        animView = (CanvasView) findViewById(R.id.v1);
        animView.getList(bubs2);
        numObjects = (Spinner) findViewById(R.id.colorType);
        numObjects.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, canvasColors));
        //if color index is valid assign color
        if(colorIndex > 0)
            numObjects.setSelection(colorIndex);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //    li.setBackgroundColor((Integer) numObjects.getSelectedItem());
             //   animView.setBg((Integer) canvas_colors[numObjects.getSelectedItemPosition()]);
                restart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        //pass color back to main intent for continuity
        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent data = new Intent();
                int index = numObjects.getSelectedItemPosition();
                System.out.println("" + index);
                data.putExtra("colorindex",index);
                setResult(RESULT_OK,data);
                finish();
            }
        });

        numObjects.setOnItemSelectedListener(listener);

    }
    //canvas color strings and resource identification
    static final String[] canvasColors = {
            "Lavender","White","Black","Light Blue", "Red","Green","Pink","Yellow","Turquoise"
    };
    static final Integer[] canvas_colors = {
            R.color.bgblue, R.color.white, R.color.black, R.color.border, R.color.red, R.color.green, R.color.pink, R.color.yellow, R.color.turq
    };
    static final int[] SENSOR_TYPES = {
            Sensor.TYPE_GYROSCOPE,
    };
    public void restart() {
        animView.restart((Integer) canvas_colors[numObjects.getSelectedItemPosition()]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animView.startAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animView.stopAnimation();
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
