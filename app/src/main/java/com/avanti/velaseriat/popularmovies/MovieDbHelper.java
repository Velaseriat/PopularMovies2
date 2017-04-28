package com.avanti.velaseriat.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.avanti.velaseriat.popularmovies.MovieEntryContract.*;


/**
 * Created by velaseriat on 4/24/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VelaseriatMovies.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE "  + MovieItem.TABLE_NAME + " (" +
                MovieItem._ID           + " INTEGER PRIMARY KEY, " +
                MovieItem.MOVIE_ID      + " INTEGER NOT NULL UNIQUE, " +
                MovieItem.COLUMN_TITLE  + " TEXT NOT NULL); ";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE IF EXISTS " + MovieItem.TABLE_NAME);
        onCreate(db);
    }
}
