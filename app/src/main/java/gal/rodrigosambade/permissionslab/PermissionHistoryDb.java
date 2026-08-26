package gal.rodrigosambade.permissionslab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

final class PermissionHistoryDb extends SQLiteOpenHelper {
    private static final String DB_NAME = "permission_history.db";

    PermissionHistoryDb(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "created_at TEXT NOT NULL," +
                "event TEXT NOT NULL)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    void add(String event) {
        ContentValues values = new ContentValues();
        values.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date()));
        values.put("event", event);
        getWritableDatabase().insert("history", null, values);
    }

    List<String> latest(int limit) {
        List<String> rows = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(
                "history",
                new String[]{"created_at", "event"},
                null, null, null, null,
                "id DESC",
                Integer.toString(limit))) {
            while (c.moveToNext()) {
                rows.add(c.getString(0) + " — " + c.getString(1));
            }
        }
        return rows;
    }
}
