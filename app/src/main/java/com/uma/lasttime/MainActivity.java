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
import android.widget.Button;
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
        db = dbHelper.getReadableDatabase();

        listView = (ListView) findViewById(R.id.listView);
        initList();

        Button goToCreateTaskButton = findViewById(R.id.goToCreate);
        goToCreateTaskButton.setOnClickListener(this::goToCreateTask);
    }

    private void initList() {
        ArrayList<Task> arrayList = new ArrayList<>();
        getTasks(arrayList);

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
                        String whereDelete = TaskContract.TaskEntry._ID + " = ?";
                        db.delete(TaskContract.TaskEntry.TABLE_NAME, whereDelete, new String[]{String.valueOf(task.getId())});
                        this.onRestart();
                    }).setNegativeButton(R.string.msg_no, (DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                    }).create().show();
            return true;
        });
    }

    @SuppressLint("Range")
    private void getTasks(ArrayList<Task> arrayList) {
        String query = "SELECT " +
                "t." + TaskContract.TaskEntry._ID + ", " +
                "t." + TaskContract.TaskEntry.COLUMN_NAME_TITLE + ", " +
                "t." + TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION + ", " +
                "MAX(" + TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP + ") as timestamp " +
                "FROM " + TaskContract.TaskEntry.TABLE_NAME + " t " +
                "LEFT JOIN " + TaskContract.TimestampEntry.TABLE_NAME + " ts ON t." + TaskContract.TaskEntry._ID + " = ts." + TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID +
                " GROUP BY t." + TaskContract.TaskEntry._ID +
                " ORDER BY MAX(ts." + TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP + ") DESC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("t." + TaskContract.TaskEntry._ID));
                String title = cursor.getString(cursor.getColumnIndex("t." + TaskContract.TaskEntry.COLUMN_NAME_TITLE));
                String description = cursor.getString(cursor.getColumnIndex("t." + TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
                long timestampInMillis = cursor.getLong(cursor.getColumnIndex("timestamp"));
                if (timestampInMillis != 0) {
                    Date date = new Date(timestampInMillis);
                    java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");
                    String formattedDate = dateFormat.format(date) + " " + clockFormat.format(date);

                    arrayList.add(new Task(id, title, description, getString(R.string.last_done) + ": " + formattedDate));
                } else {
                    arrayList.add(new Task(id, title, description, getString(R.string.not_done)));
                }
            }
        }
    }

    public void goToCreateTask(View v) {
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