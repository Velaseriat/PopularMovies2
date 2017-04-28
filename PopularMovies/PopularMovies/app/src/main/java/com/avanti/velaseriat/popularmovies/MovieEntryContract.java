package com.avanti.velaseriat.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by velaseriat on 4/24/17.
 */

public final class MovieEntryContract {
    public static final String AUTHORITY = "com.avanti.velaseriat.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static class MovieItem implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME                   = "movies";
        public static final String MOVIE_ID                     = "movie_id";
        public static final String COLUMN_TITLE                 = "title";
    }
}
