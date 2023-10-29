package com.example.produe.widget;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract implements BaseColumns {

    public static final String SCHEMA = "content://";
    public static final String AUTHORITY = "com.example.produe";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + AUTHORITY);
    public static final String PATH_TODOS = "todo_table";
    public static final Uri PATH_TODOS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODOS).build();
}
