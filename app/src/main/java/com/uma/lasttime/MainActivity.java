package com.uma.lasttime;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uma.lasttime.db.TaskContract;
import com.uma.lasttime.db.TaskDbHelper;
import com.uma.lasttime.models.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
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
        String[] columnsTask = {
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskContract.TaskEntry._ID
        };
        Cursor cursorTask = db.query(TaskContract.TaskEntry.TABLE_NAME, columnsTask, null, null, null, null, null);

        String[] columnsTimestamps = {
                TaskContract.TimestampEntry._ID,
                TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP,
                TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID
        };
        String where = TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID + " = ?";
        String orderBy = TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        String title = "", description = "";
        Integer id;
        try{
            while(cursorTask.moveToNext()){
                title = cursorTask.getString(cursorTask.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
                description = cursorTask.getString(cursorTask.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
                id = cursorTask.getInt(cursorTask.getColumnIndex(TaskContract.TaskEntry._ID));

                String[] whereArgs = { id + "" };
                Cursor cursorTimestamp = db.query(TaskContract.TimestampEntry.TABLE_NAME, columnsTimestamps, where, whereArgs, null, null, orderBy);

                if(cursorTimestamp.moveToNext()){
                    long timestampInMillis = cursorTimestamp.getLong(cursorTimestamp.getColumnIndex(TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP));
                    Date date = new Date(timestampInMillis);
                    java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");
                    String formattedDate = dateFormat.format(date) + " " + clockFormat.format(date);

                    arrayList.add(new Task(id, title, description, getString(R.string.last_done) + ": " + formattedDate));
                }else {
                    arrayList.add(new Task(id, title, description, getString(R.string.not_done)));
                }

            }
        }finally {
            cursorTask.close();
        }

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList));
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long idEntry) -> {
            Task task = arrayList.get(position);
            if (task != null) {
                Intent intent = new Intent(getApplicationContext(), DetailsTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("taskId", String.valueOf(task.getId()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long idDelete) -> {
            Task task = arrayList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.msg_remove_task)
                    .setPositiveButton(R.string.msg_yes, (DialogInterface dialog, int which) -> {
                        String whereDelete = TaskContract.TaskEntry._ID+ " = ?";
                        db.delete(TaskContract.TaskEntry.TABLE_NAME, whereDelete, new String[]{String.valueOf(task.getId())});
                        this.onRestart();
                    }).setNegativeButton(R.string.msg_no, (DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                    }).create().show();
            return true;
        });
    }

    public void goToCreateTask(View v){
        Intent intent = new Intent(this, CreateTaskActivity.class);
        startActivity(intent);
        initList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}