package com.android.ahmed.movieapp.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ahmed.movieapp.R;
import com.android.ahmed.movieapp.adapter.ExpandableAdapter;
import com.android.ahmed.movieapp.data.MovieContract;
import com.android.ahmed.movieapp.models.MovieModel;
import com.android.ahmed.movieapp.models.MovieTrailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieTrailer>>{
    @BindView(R.id.photo)
    ImageView backCover;
    @BindView(R.id.cover)
    ImageView frontCover;
    @BindView(R.id.release_date)
    TextView releaseDate;
    @BindView(R.id.overview)
    TextView overview;
    @BindView(R.id.textView2)
    TextView title;
    @BindView(R.id.vote_count)
    RatingBar ratingBar;
    @BindView(R.id.fav_fab)
    FloatingActionButton favourite;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsToolbar;

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ExpandableAdapter expandableAdapter;
    List<String> ExpandHeader;
    HashMap<String,List<MovieTrailer>> movieTrailerItem;
    public static final int TRAILER_LOADER = 4;
    public static final String SEARCH_QUERY_URL_EXTRA = "URL";
    public static final String EXTRA_INDEX = "INDEX";
    boolean check;
    public static  int ID ;
    public static MovieModel movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_INDEX)) {
                movie = savedInstanceState.getParcelable(EXTRA_INDEX);
            }
        } else {
            Bundle extras = getIntent().getExtras();
            movie = extras.getParcelable("movie");
        }

        if(movie!=null) {
            ID=movie.getId();
            viewTrailers(ID);
            check=checkMovie(ID);
            if(check)
            {
                favourite.setImageResource(R.drawable.ic_favorite_48px);
            }
            else {
                favourite.setImageResource(R.drawable.ic_favorite_border_48px);

            }
            collapsToolbar.setTitle(movie.getOriginal_title());
            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w185/"+movie.getBack_cover_path())
                    .error(R.drawable.frontcover)
                    .placeholder(R.drawable.loading)
                    .into(backCover);
            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w185/"+movie.getPoster_path())
                    .error(R.drawable.posternotfound)
                    .placeholder(R.drawable.loading)
                    .into(frontCover);
            overview.setText(movie.getOverview());
            releaseDate.setText(movie.getRelease_date());
            title.setText(movie.getOriginal_title());
            ratingBar.setRating(movie.getVote_average()/2);
        }
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check)
                {
                    removeFromFavourite(movie);
                    check=false;
                }
                else {
                    addToFavourite(movie);
                    check=true;
                }
            }
        });

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MovieModel movies = movie;
        outState.putParcelable(EXTRA_INDEX, movies);
    }
    public  void  addToFavourite(MovieModel movie)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginal_title());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getRelease_date());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getVote_average());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster_path());
        contentValues.put(MovieContract.MovieEntry.COLUMN_COVER, movie.getBack_cover_path());
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(),movie.getOriginal_title()+" "+getResources().getString(R.string.add_favourite), Toast.LENGTH_LONG).show();

        }
        favourite.setImageResource(R.drawable.ic_favorite_48px);
    }
    public  void  removeFromFavourite(MovieModel movie)
    {
        String stringId = Integer.toString(movie.getId());
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        getContentResolver().delete(uri, null, null);
        favourite.setImageResource(R.drawable.ic_favorite_border_48px);
        Toast.makeText(getBaseContext(),movie.getOriginal_title()+" "+getResources().getString(R.string.remove_favourite), Toast.LENGTH_LONG).show();
    }
    private boolean checkMovie(int id)
    {
        String stringId = Integer.toString(id);
        String[] selectionArgs=new String[]{stringId};
        Cursor res=getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                "movie_id=?",
                selectionArgs,
                null);
        if(res.getCount()==0){
            return false;
        }
        else {
            return true;
        }
    }
    public void viewTrailers(int id)
    {
        Bundle queryBundle = new Bundle();
        Log.v("IDDDDDDDD",id+"");
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, "http://api.themoviedb.org/3/movie/"+id+"/videos?api_key=21ba08e3b68174860025a5de7e5640cc");

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailerLoader = loaderManager.getLoader(TRAILER_LOADER);
        if (trailerLoader == null) {
            loaderManager.initLoader(TRAILER_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(TRAILER_LOADER, queryBundle, this);
        }
    }
    public void viewReviews(View view) {
        Intent intent=new Intent(this,ReviewActivity.class);
        intent.putExtra("ID",ID);
        startActivity(intent);
    }
    public void runTrailer(String url)
    {
        Uri web=Uri.parse(url);
        Intent intent=new Intent(Intent.ACTION_VIEW,web);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivity(intent);
        }
    }

    @Override
    public Loader<List<MovieTrailer>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<MovieTrailer>>(this) {
            List<MovieTrailer> data;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (data != null)
                {
                    deliverResult(data);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public List<MovieTrailer> loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(searchQueryUrlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String finalJson = buffer.toString();
                    JSONObject parent = new JSONObject(finalJson);
                    JSONArray parentArray = parent.getJSONArray("results");
                    List<MovieTrailer> movieTrailersList = new ArrayList<>();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        MovieTrailer movieTrailer = new MovieTrailer();
                        movieTrailer.setKey(finalObject.getString("key"));
                        movieTrailer.setName(finalObject.getString("name"));
                        movieTrailersList.add(movieTrailer);
                    }
                    return movieTrailersList;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            public void deliverResult(List<MovieTrailer> data) {
                this.data=data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
        if (data != null) {
            ExpandHeader=new ArrayList<>();
            movieTrailerItem= new HashMap<>();
            ExpandHeader.add(getResources().getString(R.string.trailer));
            movieTrailerItem.put(ExpandHeader.get(0),data);
            expandableAdapter =new ExpandableAdapter(this,ExpandHeader,movieTrailerItem);
            expandableListView.setAdapter(expandableAdapter);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) expandableListView.getLayoutParams();
            params.height = 168*data.size();
            expandableListView.setLayoutParams(params);

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                    MovieTrailer s=movieTrailerItem.get(ExpandHeader.get(i)).get(i1);
                    Toast.makeText(getApplicationContext(),s.getName(),Toast.LENGTH_LONG).show();
                    runTrailer("https://www.youtube.com/watch?v="+s.getKey());
                    return false;
                }
            });
            expandableListView.expandGroup(0);

        } else {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<MovieTrailer>> loader) {
    }

}

