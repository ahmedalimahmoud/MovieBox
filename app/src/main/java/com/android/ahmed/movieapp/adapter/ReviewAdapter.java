package com.android.ahmed.movieapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.ahmed.movieapp.R;
import com.android.ahmed.movieapp.models.MovieReviews;

import java.util.List;

/**
 * Created by ahmed on 5/5/2017.
 */

public class ReviewAdapter extends ArrayAdapter {
    private List<MovieReviews> movieReviewsList;
    private final int resource;
    private final LayoutInflater inflater;

    public ReviewAdapter(Context context, int resource, List<MovieReviews> objects) {
        super(context, resource, objects);
        movieReviewsList = objects;
        this.resource = resource;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ReviewAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ReviewAdapter.ViewHolder();
            convertView = inflater.inflate(resource, null);
            viewHolder.author = (TextView) convertView.findViewById(R.id.article_title);
            viewHolder.content = (TextView) convertView.findViewById(R.id.article_body);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReviewAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.author.setText(movieReviewsList.get(position).getAuthor() + " :");
        viewHolder.content.setText(movieReviewsList.get(position).getContent());
        return convertView;

    }

    class ViewHolder {
        private TextView author;
        private TextView content;
    }
}