package com.avanti.velaseriat.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by velaseriat on 4/26/17.
 */

class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {
    private String[] mReviews = new String[0];

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int reviewLayoutId = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachImmediatelyToParent = false;

        View view = inflater.inflate(reviewLayoutId, parent, shouldAttachImmediatelyToParent);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.setText(mReviews[position]);
    }

    @Override
    public int getItemCount() {
        return mReviews.length;
    }

    public void setReviewData(String[] reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReview = (TextView) itemView;
        }

        public void setText (String text) {
            mReview.setText(text);
        }
    }
}
