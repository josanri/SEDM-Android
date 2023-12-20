package com.uma.lasttime;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.uma.lasttime.db.TaskContract;
import com.uma.lasttime.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DetailsTaskActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private TextView textViewTitleDetail;
    private TextView textViewDescriptionDetail;
    private ListView listViewTimestamps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_task);

        textViewTitleDetail = (TextView) findViewById(R.id.titleDetail);
        textViewDescriptionDetail = (TextView) findViewById(R.id.descriptionDetail);
        listViewTimestamps = (ListView) findViewById(R.id.timestampList);

        TaskDbHelper taskDbHelper = new TaskDbHelper(getApplicationContext());
        db = taskDbHelper.getWritableDatabase();

        getTaskIdFromBundle();
        getTaskInfo();

        textViewTitleDetail.setText(taskTitle);
        textViewDescriptionDetail.setText(taskDescription);
        setListViewTimestamps();

        textViewTitleDetail.setOnLongClickListener((view) -> {
            Toast.makeText(getApplicationContext(), taskTitle, Toast.LENGTH_SHORT).show();
            return true;
        });
        Button addTimestampButton = findViewById(R.id.updateButton);
        addTimestampButton.setOnClickListener(this::updateTimestamp);
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
    private void getTaskInfo() {
        String[] columns = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION
        };
        String where = TaskContract.TaskEntry._ID + " = ?";
        String[] whereArgs = {taskId + ""};

        ;
        try (Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, where, whereArgs, null, null, null)) {
            if (cursor.moveToNext()) {
                taskTitle = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
                taskDescription = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
            } else {
                finish();
            }
        }
    }

    @SuppressLint("Range")
    private void setListViewTimestamps() {
        String[] columns = {
                TaskContract.TimestampEntry._ID,
                TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP,
                TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID
        };
        String where = TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID + " = ?";
        String[] whereArgs = {taskId + ""};
        String orderBy = TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        ArrayList<String> arrayList = new ArrayList<>();
        try (Cursor cursor = db.query(TaskContract.TimestampEntry.TABLE_NAME, columns, where, whereArgs, null, null, orderBy)) {
            while (cursor.moveToNext()) {
                long timestampInMillis = cursor.getLong(cursor.getColumnIndex(TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP));
                String formattedDate = getFormattedDate(timestampInMillis);
                arrayList.add(formattedDate);
            }
        }

        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                arrayList);
        listViewTimestamps.setAdapter(arrayAdapter);
    }

    @NonNull
    private String getFormattedDate(long timestampInMillis) {
        Date date = new Date(timestampInMillis);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date) + " " + clockFormat.format(date);
        return formattedDate;
    }

    private void updateTimestamp(View v) {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(timeZone);
        long currentTimeMillis = calendar.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID, taskId);
        values.put(TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP, currentTimeMillis);

        boolean dbError = db.insert(TaskContract.TimestampEntry.TABLE_NAME, null, values) == -1;
        generateRenewToast(dbError);
        finish();
    }

    private void generateRenewToast(boolean error) {
        if (error) {
            Toast.makeText(getApplicationContext(), R.string.msg_error_on_renew, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.msg_renew, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

}