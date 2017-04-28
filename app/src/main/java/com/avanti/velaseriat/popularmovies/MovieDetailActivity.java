package com.avanti.velaseriat.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener, MovieTrailerAdapter.TrailerItemClickListener {

    private MovieEntry mMovieEntry;
    private ImageView mBackgroundImage;
    private ImageButton mFavoritesImageButton;

    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewsRecyclerView;

    private MovieTrailerAdapter mMovieTrailerAdapter;
    private MovieReviewAdapter mMovieReviewAdapter;


    private boolean mDetailToggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieEntry = getIntent().getParcelableExtra(getString(R.string.movie_entry_object_name));

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);

        mTrailerRecyclerView.setLayoutManager(trailersLayoutManager);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        mMovieTrailerAdapter = new MovieTrailerAdapter(this);
        mMovieReviewAdapter = new MovieReviewAdapter();

        setBackgroundImage();
        setMovieDetailInfo();
        setMovieFavoriteData();
        setMovieTrailerData();
        setMovieReviewData();

        mTrailerRecyclerView.setAdapter(mMovieTrailerAdapter);
        mReviewsRecyclerView.setAdapter(mMovieReviewAdapter);
    }

    private void setMovieFavoriteData() {
        mFavoritesImageButton = (ImageButton) findViewById(R.id.ib_favorite_button);
        mFavoritesImageButton.setOnClickListener(this);
        new FetchMovieFavoriteTask().execute();
    }

    private void setMovieTrailerData() {
        new FetchMovieTrailersTask().execute();
    }

    private void setMovieReviewData() {
        new FetchMovieReviewTask().execute();
    }

    private void toggleMovieFavorite() {
        new SetMovieFavoriteTask().execute();
    }

    private void setMovieDetailInfo() {
        if (mMovieEntry != null) {
            ((TextView) findViewById(R.id.tv_detail_title))     .setText(mMovieEntry.getTitle());
            ((TextView) findViewById(R.id.tv_detail_release))   .setText(getString(R.string.released_string) + " " + mMovieEntry.getRelease());
            ((TextView) findViewById(R.id.tv_detail_rating))    .setText(mMovieEntry.getRating() + " (" + mMovieEntry.getVotes() + " " + getString(R.string.votes_string) + ")");
            ((TextView) findViewById(R.id.tv_detail_synopsis))  .setText( "\n\n" + mMovieEntry.getSynopsis());
        }

    }

    private void setBackgroundImage() {
        if (mMovieEntry != null) {
            mBackgroundImage = (ImageView) findViewById(R.id.iv_detail_image);
            mBackgroundImage.setOnClickListener(this);
            Picasso.with(this).load(mMovieEntry.getLargeImagePath()).into(mBackgroundImage);
        }
    }

    private void setFavoriteButtonStatus(boolean isFavorite) {
        int buttonImageResource = isFavorite ? R.drawable.btn_star_big_on : R.drawable.btn_star_big_off;
        mFavoritesImageButton.setImageResource(buttonImageResource);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_favorite_button:
                toggleMovieFavorite();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onListItemClick(Uri videoUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
        startActivity(intent);
    }


    //Background tasks
    //grabs if movie is favorite
    //grabs trailers
    //grabs reviews

    private class SetMovieFavoriteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return MovieDataUtils.setFavoriteMovie(MovieDetailActivity.this, mMovieEntry.getId(), mMovieEntry.getTitle());
        }

        @Override
        protected void onPostExecute(Boolean favoriteMovieResult) {
            super.onPostExecute(favoriteMovieResult);
            setFavoriteButtonStatus(favoriteMovieResult);
        }
    }

    private class FetchMovieFavoriteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return MovieDataUtils.isFavoriteMovie(MovieDetailActivity.this, mMovieEntry.getId());
        }

        @Override
        protected void onPostExecute(Boolean favoriteMovieResult) {
            super.onPostExecute(favoriteMovieResult);
            setFavoriteButtonStatus(favoriteMovieResult);
        }
    }

    private class FetchMovieTrailersTask extends AsyncTask<Void, Void, Uri[]> {

        @Override
        protected Uri[] doInBackground(Void... params) {
            return MovieDataUtils.getMovieTrailerPaths(mMovieEntry.getId());
        }

        @Override
        protected void onPostExecute(Uri[] trailers) {
            super.onPostExecute(trailers);
            mMovieTrailerAdapter.setTrailerData(trailers);
        }
    }

    private class FetchMovieReviewTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            return MovieDataUtils.getMoviewReviews(mMovieEntry.getId());
        }

        @Override
        protected void onPostExecute(String[] reviews) {
            super.onPostExecute(reviews);
            mMovieReviewAdapter.setReviewData(reviews);
        }
    }
}
