package com.uma.lasttime.db;

import android.provider.BaseColumns;

public final class TaskContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TaskContract() {}

    /* Inner class that defines the table contents */
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    public static class TimestampEntry implements BaseColumns {
        public static final String TABLE_NAME = "taskTime";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
    }
}
