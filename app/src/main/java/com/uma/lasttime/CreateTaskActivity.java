package com.uma.lasttime;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uma.lasttime.db.TaskContract;
import com.uma.lasttime.db.TaskDbHelper;

public class CreateTaskActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private EditText titleEdit;
    private EditText descriptionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task);

        titleEdit = findViewById(R.id.editTextTitleCreate);
        descriptionEdit = findViewById(R.id.editTextDescriptionCreate);

        TaskDbHelper dbHelper = new TaskDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        Button addNewTaskButton = findViewById(R.id.addNewTaskButton);
        addNewTaskButton.setOnClickListener(this::addNewTask);
    }

    public void addNewTask(View v) {
        ContentValues values;
        String title, description;

        title = titleEdit.getText().toString().trim();
        description = descriptionEdit.getText().toString().trim();
        values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, title);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION, description);
        try {
            db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
            Toast.makeText(getApplicationContext(), R.string.msg_added, Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.msg_error_on_renew, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}