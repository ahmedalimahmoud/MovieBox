package com.android.ahmed.movieapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.ahmed.movieapp.ui.MovieDetailActivity;
import com.android.ahmed.movieapp.R;
import com.android.ahmed.movieapp.models.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    public  Activity host;
    private final LayoutInflater inflater;
    public static List<MovieModel> movieInfoList = new ArrayList<>();

    public MovieAdapter(Activity activity, List<MovieModel> movieInfo, MovieAdapterOnClickHandler clickHandler) {
        host = activity;
        movieInfoList = movieInfo;
        inflater = LayoutInflater.from(host);
    }


    String getIDPosition(int position) {
        return String.valueOf(movieInfoList.get(position).getId());
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(inflater.inflate(R.layout.movie_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, final int position) {

        Picasso.with(host)
                .load("http://image.tmdb.org/t/p/w185/"+movieInfoList.get(position).getPoster_path())
                .placeholder(R.drawable.loading)
                .error(R.drawable.posternotfound)
                .into(holder.cover);


    }
    public interface MovieAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cover)
        ImageView cover;

        public MovieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(host, movieInfoList.get(getAdapterPosition()).getOriginal_title(), Toast.LENGTH_SHORT).show();
               MovieModel movieModel = movieInfoList.get(getAdapterPosition()); // getting the model
            Intent intent = new Intent(host, MovieDetailActivity.class);
            intent.putExtra("movie", movieModel);
            host.startActivity(intent);

        }
    }
    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }

}


