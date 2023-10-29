package com.example.produe;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.produe.data.ToDoDao;
import com.example.produe.data.ToDoRoomDatabase;
import com.example.produe.widget.Contract;

public class ToDoContentProvider extends ContentProvider {

    // Defines a handle to the Room database
    private ToDoRoomDatabase appDatabase;

    // Defines a Data Access Object to perform the database operations
    private ToDoDao toDoDao;

    // Defines the database name
    private static final String DBNAME = "ProDue_database";

    public static final int TODOS_CODE = 1;

    public static final UriMatcher uriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_TODOS, TODOS_CODE);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();

        // Creates a new database object
        appDatabase = Room.databaseBuilder(context, ToDoRoomDatabase.class, DBNAME).build();

        // Gets a Data Access Object to perform the database operations
        toDoDao = appDatabase.toDoDao();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = uriMatcher.match(uri);

        Cursor qCursor;

        if (match == TODOS_CODE) {
            qCursor = toDoDao.getOrderedTasksCursor();

            if (getContext() != null) {
                qCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return qCursor;
            }
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
