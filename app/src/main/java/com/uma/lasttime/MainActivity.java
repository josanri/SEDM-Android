package com.uma.lasttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.uma.lasttime.db.TaskContract;
import com.uma.lasttime.db.TaskDbHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TaskDbHelper dbHelper = new TaskDbHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry.COLUMN_NAME_TITLE},
                null, null, null, null, null);
        try {
            while (cursor.moveToNext())
            {
                // Add element to list
            }
        } finally {
            cursor.close();
        }
        TextView textView = findViewById(R.id.app_title);
        textView.setText("HEY");
    }

    public void goToDetails(View v){
        Intent intent = new Intent(this, DetailsTaskActivity.class);
        startActivity(intent);
    }
}