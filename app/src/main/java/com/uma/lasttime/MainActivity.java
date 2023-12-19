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

import java.util.ArrayList;

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
    }

    @SuppressLint("Range")
    private void initList() {
        ArrayList<Task> arrayList = new ArrayList<>();
        String[] columns = {
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskContract.TaskEntry._ID
        };
        try (Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, null, null, null, null, null)) {
            String title, description;
            int id;
            while (cursor.moveToNext()) {
                title = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
                description = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
                id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                arrayList.add(new Task(id, title, description));
            }
        }

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList));
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Task task = arrayList.get(position);
            if (task != null) {
                Intent intent = new Intent(getApplicationContext(), DetailsTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("taskId", String.valueOf(task.getId()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Task task = arrayList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.msg_remove_task)
                    .setPositiveButton(R.string.msg_yes, (DialogInterface dialog, int which) -> {
                        String where = TaskContract.TaskEntry._ID+ " = ?";
                        db.delete(TaskContract.TaskEntry.TABLE_NAME, where, new String[]{String.valueOf(task.getId())});
                        this.onRestart();
                    }).setNegativeButton(R.string.msg_no, (DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                    }).create().show();
            return true;
        });
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