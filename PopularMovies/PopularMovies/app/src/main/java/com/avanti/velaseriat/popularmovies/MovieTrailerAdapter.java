package com.avanti.velaseriat.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by velaseriat on 4/26/17.
 */

class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerViewHolder> {
    private Uri[] mTrailers = new Uri[0];

    final private TrailerItemClickListener mOnClickListener;

    public MovieTrailerAdapter(TrailerItemClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int trailerLayoutId = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachImmediatelyToParent = false;

        View view = inflater.inflate(trailerLayoutId, parent, shouldAttachImmediatelyToParent);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mTrailers.length;
    }

    public void setTrailerData(Uri[] trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    interface TrailerItemClickListener {
        void onListItemClick(Uri videoUrl);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mTrailers[clickedPosition]);
        }
    }
}
