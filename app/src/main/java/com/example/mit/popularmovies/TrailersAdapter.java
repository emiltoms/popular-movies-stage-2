package com.example.mit.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private final static String TRAILER_LINK_BASE = "https://www.youtube.com/watch?v=";
    private final static String TRAILER_LINK_BASE_APP = "vnd.youtube:";
    private static ArrayList<String> trailersDataset;
    private static Context context;
    private final String LOG_TAG = TrailersAdapter.class.getSimpleName();
    private final String TRAILER_IMAGE_LINK_BASE_START = "https://img.youtube.com/vi/";
    private final String TRAILER_IMAGE_LINK_BASE_END = "/mqdefault.jpg";

    public TrailersAdapter(ArrayList<String> trailersDataset, Context context) {
        this.trailersDataset = trailersDataset;
        this.context = context;
    }

    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trailer, parent, false);

        Log.i(LOG_TAG, "onCreateViewHolder; We started the party... view = " + view);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Picasso.with(context)
                .load(createUrlToTrailerThumbnail(trailersDataset
                        .get(position)))
                .into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return trailersDataset.size();
    }

    private String createUrlToTrailerThumbnail(String movieID) {
        return TRAILER_IMAGE_LINK_BASE_START + movieID + TRAILER_IMAGE_LINK_BASE_END;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.trailer_item_imageView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(TRAILER_LINK_BASE_APP + trailersDataset.get(getAdapterPosition())));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(TRAILER_LINK_BASE + trailersDataset.get(getAdapterPosition())));
                    try {
                        context.startActivity(youtubeAppIntent);
                    } catch (ActivityNotFoundException e) {
                        context.startActivity(webIntent);
                    }
                }
            });
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
