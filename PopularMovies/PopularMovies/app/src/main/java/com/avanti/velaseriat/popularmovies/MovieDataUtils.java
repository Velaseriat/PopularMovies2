package com.avanti.velaseriat.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by velaseriat on 3/19/17.
 */

public class MovieDataUtils {

    private static String API_KEY = "";

    private static final String MOVIE_URL_BASE      = "https://api.themoviedb.org/3/movie";
    private static final String VIDEOS_KEY_PATH      = "videos";
    private static final String REVIEWS_KEY_PATH      = "reviews";

    private static final String POPULAR_KEY_PATH    = "popular";
    private static final String TOP_RATED_KEY_PATH  = "top_rated";

    private static final String IMG_URL_BASE        = "http://image.tmdb.org/t/p";
    private static final String VIDEO_URL_BASE      = "https://www.youtube.com/watch";

    private static final String VIDEO_KEY_PARAM     = "v";

    private static final String SMALL_IMG_PATH      = "w185";
    private static final String LARGE_IMG_PATH      = "w780";

    private static final String API_KEY_PARAM       = "api_key";

    public static MovieEntry[] getMoviesByPopularity() {
        return getMovieList(POPULAR_KEY_PATH);
    }

    public static MovieEntry[] getMoviesByRating() {
        return getMovieList(TOP_RATED_KEY_PATH);
    }

    public static MovieEntry[] getMoviesByFavorites(Context context) {
        return getMovieFavorites(context);
    }


    private static MovieEntry[] getMovieList(String keyPath) {
        MovieEntry[] mMoviesList = new MovieEntry[0];

        //Build the query here
        //https://api.themoviedb.org/3/movie/157336?api_key={api_key}
        Uri uri = Uri.parse(MOVIE_URL_BASE).buildUpon()
                .appendPath(keyPath)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        String response = getJSONResponse(uri);

        JSONObject jsonObject = null;
        if ( response != null ) {
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Package result and send it
        //do these strings really need to be extracted to string.xml? These are more for code than anything...
        if ( jsonObject != null ) {
            try {
                JSONArray movies = jsonObject.getJSONArray("results");
                mMoviesList = new MovieEntry[movies.length()];
                for ( int i = 0; i < movies.length(); i++) {
                    JSONObject js = movies.getJSONObject(i);

                    MovieEntry me = new MovieEntry(
                            js.getInt("id"),
                            js.getString("title"),
                            js.getDouble("vote_average"),
                            getSmallImage(js.getString("poster_path")),
                            getLargeImage(js.getString("poster_path")),
                            js.getString("release_date"),
                            js.getString("overview"),
                            js.getInt("vote_count"));
                    mMoviesList[i] = me;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mMoviesList;
    }


    //private MovieEntry[] getMovieFavorites() {
    private static MovieEntry[] getMovieFavorites(Context context) {
        MovieEntry[] favoriteMovies;

        Uri favoriteMoviesUri = MovieEntryContract.MovieItem.CONTENT_URI;
        String sortOrder = "UPPER( " + MovieEntryContract.MovieItem.COLUMN_TITLE + ")";

        Cursor cursor = context.getContentResolver().query(favoriteMoviesUri,
                null,
                null,
                null,
                sortOrder);

        try {
            int listSize = cursor.getCount();
            favoriteMovies = new MovieEntry[listSize];

            while (cursor.moveToNext()) {
                int movieIdColumnIndex = cursor.getColumnIndex(MovieEntryContract.MovieItem.MOVIE_ID);
                MovieEntry movie = getMovieDetails(cursor.getInt(movieIdColumnIndex));

                favoriteMovies[cursor.getPosition()] = movie;
            }
        } finally {
            cursor.close();
        }

        return favoriteMovies;
    }

    public static MovieEntry getMovieDetails(int id) {
        Uri uri = Uri.parse(MOVIE_URL_BASE).buildUpon()
                .appendPath(String.valueOf(id))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        String response = getJSONResponse(uri);

        JSONObject jsonObject = null;
        if ( response != null ) {
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MovieEntry movieEntry = null;
        try {
            movieEntry = new MovieEntry(
                    id,
                    jsonObject.getString("title"),
                    jsonObject.getDouble("vote_average"),
                    getSmallImage(jsonObject.getString("poster_path")),
                    getLargeImage(jsonObject.getString("poster_path")),
                    jsonObject.getString("release_date"),
                    jsonObject.getString("overview"),
                    jsonObject.getInt("vote_count"));

            return movieEntry;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieEntry;
    }

    //TODO
    public static Uri[] getMovieTrailerPaths(int id) {
        Uri[] movieTrailerResults = new Uri[0];

        Uri uri = Uri.parse(MOVIE_URL_BASE).buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(VIDEOS_KEY_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        String response = getJSONResponse(uri);

        JSONObject jsonObject = null;
        if ( response != null ) {
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Package result and send it
        //do these strings really need to be extracted to string.xml? These are more for code than anything...
        if ( jsonObject != null ) {
            try {
                JSONArray trailers = jsonObject.getJSONArray("results");
                movieTrailerResults = new Uri[trailers.length()];
                for ( int i = 0; i < trailers.length(); i++) {
                    JSONObject js = trailers.getJSONObject(i);
                    movieTrailerResults[i] = buildVideoUri(js.getString("key"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieTrailerResults;
    }

    //TODO
    public static String[] getMoviewReviews(int id) {

        String[] movieReviewResults = new String[0];

        Uri uri = Uri.parse(MOVIE_URL_BASE).buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(REVIEWS_KEY_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        String response = getJSONResponse(uri);

        JSONObject jsonObject = null;
        if ( response != null ) {
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if ( jsonObject != null ) {
            try {
                JSONArray reviews = jsonObject.getJSONArray("results");
                movieReviewResults = new String[reviews.length()];
                for ( int i = 0; i < reviews.length(); i++) {
                    JSONObject js = reviews.getJSONObject(i);
                    String author = js.getString("author");
                    String content = js.getString("content");
                    movieReviewResults[i] = buildReviewString(author, content);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieReviewResults;
    }

    private static String getJSONResponse(Uri uri) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Get JSON response here
        String response = null;
        try {
            response = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String getSmallImage(String poster_path) {
        return buildImageUri(poster_path, SMALL_IMG_PATH);
    }

    private static String getLargeImage(String poster_path) {
        return buildImageUri(poster_path, LARGE_IMG_PATH);
    }

    private static String buildImageUri(String posterPath, String imgSize) {
        Uri uri = Uri.parse(IMG_URL_BASE).buildUpon()
                .appendPath(imgSize)
                .appendPath(posterPath.replaceFirst("\\/", ""))
                .build();
        return uri.toString();
    }

    private static Uri buildVideoUri(String videoKey) {
        Uri uri = Uri.parse(VIDEO_URL_BASE).buildUpon()
                .appendQueryParameter(VIDEO_KEY_PARAM, videoKey)
                .build();
        return uri;
    }

    private static String buildReviewString(String author, String content) {
        return author + " -\n" + content;
    }

    //This came directly from https://github.com/Velaseriat/ud851-Sunshine/blob/student/S04.03-Solution-AddMapAndSharing/app/src/main/java/com/example/android/sunshine/utilities/NetworkUtils.java
    //I understand everything that is going on here
    private static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }



    public static Boolean isFavoriteMovie(Context context, int movieId) {
        Uri movieUri = buildMovieUriById(movieId);
        Cursor cursor = context.getContentResolver().query(movieUri, null, null, null, null);
        Log.i("MovieDataUtils", movieId + " has " + cursor.getCount());

        try {
            return cursor.getCount() > 0;
        } catch (Exception e){
            return false;
        } finally {
            cursor.close();
        }
    }

    public static boolean setFavoriteMovie(Context context, int movieId, String title) {
        Uri movieUri = buildMovieUriById(movieId);
        ContentResolver contentResolver = context.getContentResolver();

        if (isFavoriteMovie(context, movieId)) {
            int numRemoved = contentResolver.delete(movieUri, null, null);

            //if successfully removed data, then tell caller it is "false" for no longer a favorite, or true if delete failed for some reason
            return !(numRemoved > 0);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieEntryContract.MovieItem.MOVIE_ID, movieId);
            contentValues.put(MovieEntryContract.MovieItem.COLUMN_TITLE, title);

            Uri returnUri = contentResolver.insert(movieUri, contentValues);

            return returnUri != null;
        }
    }

    private static Uri buildMovieUriById(int movieId) {
        String id = Integer.toString(movieId);
        Uri movieUri = MovieEntryContract.MovieItem.CONTENT_URI;
        movieUri = movieUri.buildUpon().appendPath(id).build();
        return movieUri;
    }
}
