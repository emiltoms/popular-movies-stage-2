package com.example.mit.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private final String LOG_TAG = TrailersAdapter.class.getSimpleName();
    private static ArrayList<String> trailersDataset;
    private static Context context;
    private final static String TRAILER_LINK_BASE = "https://www.youtube.com/watch?v=";
    private final static String TRAILER_LINK_BASE_APP = "vnd.youtube:";
    private final String TRAILER_IMAGE_LINK_BASE_START = "https://img.youtube.com/vi/";
    private final String TRAILER_IMAGE_LINK_BASE_END = "/mqdefault.jpg";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTextView;
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

    public TrailersAdapter(ArrayList<String> trailersDataset, Context context) {
        this.trailersDataset = trailersDataset;
        this.context = context;
    }


    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trailer, parent, false);

        Log.i(LOG_TAG, "onCreateViewHolder; We started the party... view = "+view);

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

}



//
//public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
//
//    private ArrayList<String> dataList;
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView mTextView;
//        public ViewHolder(TextView v) {
//            super(v);
//            mTextView = v;
//        }
//    }
//
//    public TrailersAdapter(ArrayList<String> dataList) {
//        this.dataList = dataList;
//    }
//
//    @Override
//    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
//                                                         int viewType) {
//        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_trailer, parent, false);
//
//        ViewHolder viewHolder = new ViewHolder(textView);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mTextView.setText(dataList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return dataList.size();
//    }
//}


//public class TrailersAdapter extends ArrayAdapter<String> {
//    private Context context;
//    private final String TRAILER_LINK_BASE = "https://www.youtube.com/watch?v=";
//    private final String TRAILER_IMAGE_LINK_BASE_START = "https://img.youtube.com/vi/";
//    private final String TRAILER_IMAGE_LINK_BASE_END = "/mqdefault.jpg";
//
//    TrailersAdapter(Activity context, ArrayList<String> trailers) {
//        super(context, 0, trailers);
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int p, View cv, @NonNull ViewGroup parent) {
//        View listItemView = cv;
//        if (listItemView == null) {
//            listItemView = LayoutInflater
//                    .from(getContext())
//                    .inflate(R.layout.item_trailer, parent, false);
//        }
//
//        String key = getItem(p);
//
//        ImageView imageView = listItemView.findViewById(R.id.trailer_item_imageView);
//        assert key != null : "TrailerAdapter error: assert key != null";
//        String imgLink = TRAILER_IMAGE_LINK_BASE_START + key + TRAILER_IMAGE_LINK_BASE_END;
//        Picasso.with(context).load(imgLink).into(imageView);
//
//        return listItemView;
//    }
//}
