package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by SawS on 8/12/16.
 */
public class MovieContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie_collection";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATINGS = "ratings";
        public static final String COLUMN_DATE = "release_date";
    }
}
