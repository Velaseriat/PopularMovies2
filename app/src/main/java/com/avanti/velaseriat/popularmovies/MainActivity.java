package com.avanti.velaseriat.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private GridView mGridView;
    private MovieAdapter mMoviesAdapter;

    private static final int SORT_BY_POPULARITY = 0;
    private static final int SORT_BY_TOP_RATED = 1;
    private static final int SORT_BY_FAVORITES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesAdapter = new MovieAdapter(this);
        mGridView = (GridView) findViewById(R.id.gv_movies_list);
        mGridView.setOnItemClickListener(this);
        mGridView.setAdapter(mMoviesAdapter);

        loadMovies(SORT_BY_POPULARITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //TODO
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popularity:
                loadMovies(SORT_BY_POPULARITY);
                break;
            case R.id.action_sort_top_rated:
                loadMovies(SORT_BY_TOP_RATED);
                break;
            case R.id.action_sort_favorites:
                loadMovies(SORT_BY_FAVORITES);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMovies(int sortType){
        new FetchMovieData().execute(sortType);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(getString(R.string.movie_entry_object_name), (Parcelable) mMoviesAdapter.getItem(position));

        startActivity(intent);
    }

    private class FetchMovieData extends AsyncTask<Integer, Void, MovieEntry[]> {

        @Override
        protected MovieEntry[] doInBackground(Integer... params) {
            switch (params[0]) {
                case SORT_BY_POPULARITY:
                    return MovieDataUtils.getMoviesByPopularity();
                case SORT_BY_TOP_RATED:
                    return MovieDataUtils.getMoviesByRating();
                case SORT_BY_FAVORITES:
                    return MovieDataUtils.getMoviesByFavorites(MainActivity.this);
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        protected void onPostExecute(MovieEntry[] movieEntries) {
            if (mMoviesAdapter != null) {
                mMoviesAdapter.setMovieData(movieEntries);
            }
        }
    }
}
