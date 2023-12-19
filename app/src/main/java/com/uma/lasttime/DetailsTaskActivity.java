package com.uma.lasttime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;

import androidx.appcompat.app.AppCompatActivity;

import com.uma.lasttime.db.TaskContract;
import com.uma.lasttime.db.TaskDbHelper;

import java.util.ArrayList;
import java.util.Date;

public class DetailsTaskActivity extends AppCompatActivity {
    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase db;

    private int taskId;
    TextView textViewTitleDetail;
    TextView textViewDescriptionDetail;
    ListView listViewTimestamps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_task);

        taskDbHelper = new TaskDbHelper(getApplicationContext());
        db = taskDbHelper.getWritableDatabase();

        getTaskIdFromBundle();

        textViewTitleDetail = (TextView) findViewById(R.id.titleDetail);
        textViewDescriptionDetail = (TextView) findViewById(R.id.descriptionDetail);
        listViewTimestamps = (ListView) findViewById(R.id.timestampList);

        setTextViewTaskTitle();
        setTextViewTaskDescription();
        setListViewTimestamps();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void getTaskIdFromBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                taskId = Integer.parseInt(bundle.getString("taskId"));
            } catch (NullPointerException e) {
                finish();
            }
        }
    }

    @SuppressLint("Range")
    private void setTextViewTaskTitle() {
        String[] columns = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION
        };
        String where = TaskContract.TaskEntry._ID + " = ?";
        String[] whereArgs = { taskId + "" };

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, where, whereArgs, null, null, null);
        String taskTitle = "";
        try {
            while (cursor.moveToNext()) {
                taskTitle = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
            }
        } finally {
            cursor.close();
        }
        textViewTitleDetail.setText(taskTitle);
    }

    @SuppressLint("Range")
    private void setTextViewTaskDescription() {
        String[] columns = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION
        };
        String where = TaskContract.TaskEntry._ID + " = ?";
        String[] whereArgs = { taskId + "" };

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, where, whereArgs, null, null, null);
        String taskDescription = "";
        try {
            while (cursor.moveToNext()) {
                taskDescription = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
            }
        } finally {
            cursor.close();
        }
        textViewDescriptionDetail.setText(taskDescription);
    }


    @SuppressLint("Range")
    private void setListViewTimestamps() {
        String[] columns = {
                TaskContract.TimestampEntry._ID,
                TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP,
                TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID
        };
        String where = TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID + " = ?";
        String[] whereArgs = { taskId + "" };

        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = db.query(TaskContract.TimestampEntry.TABLE_NAME, columns, where, whereArgs, null, null, null);
        try {
            while (cursor.moveToNext()) {
                long timestampInMillis = cursor.getLong(cursor.getColumnIndex(TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP));
                Date date = new Date(timestampInMillis);
                java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                String formattedDate = dateFormat.format(date);
                arrayList.add(formattedDate);
            }
        } finally {
            cursor.close();
        }

        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<>(this ,
                android.R.layout.simple_list_item_1 ,
                arrayList);
        listViewTimestamps.setAdapter(arrayAdapter);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.updateButton) {
            updateTimestamp();
        }
    }

    private void updateTimestamp() {
        long timestamp = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID, taskId);
        values.put(TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP, timestamp);

        generateToast(db.insert(TaskContract.TimestampEntry.TABLE_NAME, null, values));
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void generateToast(long info) {
        Toast toast;
        if (info == -1) {
            toast = Toast.makeText(getApplicationContext(), R.string.msg_error_on_renew, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(getApplicationContext(), R.string.msg_renew, Toast.LENGTH_LONG);
        }
        toast.show();
    }
}