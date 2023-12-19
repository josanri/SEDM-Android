package com.uma.lasttime;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.uma.lasttime.db.TaskContract;
import com.uma.lasttime.db.TaskDbHelper;
import com.uma.lasttime.models.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private TaskDbHelper dbHelper;

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TaskDbHelper dbHelper = new TaskDbHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);
        db = dbHelper.getReadableDatabase();
        initList();
    }

    @SuppressLint("Range")
    private void initList(){
        ArrayList<Task> arrayList = new ArrayList<>();
        String[] columns = {
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskContract.TaskEntry._ID
        };
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, null, null, null, null, null);
        Task task;
        String title = "", description = "";
        Integer id;
        try{
            while(cursor.moveToNext()){
                title = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
                description = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
                id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                arrayList.add(new Task(id, title, description));
            }
        }finally {
            cursor.close();
        }

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                Task task = arrayList.get(position);
                if (task != null) {
                    Intent intento = new Intent(getApplicationContext(), DetailsTaskActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("taskId", String.valueOf(task.getId()));
                    intento.putExtras(bundle);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intento);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }


    public void goToCreateTask(View v){
        Intent intent = new Intent(this, CreateTaskActivity.class);
        startActivity(intent);
        initList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}