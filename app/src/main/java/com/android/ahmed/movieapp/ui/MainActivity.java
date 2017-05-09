package com.android.ahmed.movieapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ahmed.movieapp.R;
import com.android.ahmed.movieapp.adapter.MovieAdapter;
import com.android.ahmed.movieapp.data.MovieContract;
import com.android.ahmed.movieapp.models.MovieModel;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<List<MovieModel>>,MovieAdapter.MovieAdapterOnClickHandler,SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int POPULAR_MOVIE_LOADER = 1;
    public static final int TOP_RATE_MOVIE_LOADER = 2;
    public static final int FAVOURITE_MOVIE_LOADER = 3;
    public static MovieAdapter adapter;
    public static  Cursor res;
    public static  String columns;
    public static  String colors;
    public static  int popular_page=1;
    public static  int top_page=1;
    public static boolean run=false;
    public static boolean page=false;

    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.no_favourite)
    TextView NoFavourite;
    @BindView(R.id.next_fab)
    FloatingActionButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupPreference();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page) {
                    top_page++;
                    viewTOpRateMovies();
                }else {
                    popular_page++;
                    viewPopularMovies();
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setupPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        loadColors(sharedPreferences);
        loadColumns(sharedPreferences);
    }

    public void loadColumns(SharedPreferences sharedPreferences) {
        columns = sharedPreferences.getString(getResources().getString(R.string.pref_size_key), getResources().getString(R.string.pref_size_default));
        viewPopularMovies();
    }
    public void loadColors(SharedPreferences sharedPreferences) {
        colors = sharedPreferences.getString(getResources().getString(R.string.pref_order_key), getResources().getString(R.string.pref_order_black_value));
        Log.v("LOAD COLOR","LOAD COLOR");
        toolbar.setBackgroundColor(Integer.parseInt(colors, 16)+0xFF000000);
    }


    public void viewPopularMovies()
    {
        toolbar.setTitle(getString(R.string.app_name));
        run=false;
        page=false;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> PopularLoader = loaderManager.getLoader(POPULAR_MOVIE_LOADER);
        if (PopularLoader == null) {
            loaderManager.initLoader(POPULAR_MOVIE_LOADER, null, this);
        } else {
            loaderManager.restartLoader(POPULAR_MOVIE_LOADER, null, this);
        }
    }
    public void viewTOpRateMovies()
    {

        toolbar.setTitle(getString(R.string.app_name));
        run=false;
        page=true;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> TopRateLoader = loaderManager.getLoader(TOP_RATE_MOVIE_LOADER);
        if (TopRateLoader == null) {
            loaderManager.initLoader(TOP_RATE_MOVIE_LOADER, null, this);
        } else {
            loaderManager.restartLoader(TOP_RATE_MOVIE_LOADER, null, this);
        }
    }

    public void viewFavouriteMovies()
    {
        toolbar.setTitle(getString(R.string.favourite));
        run=true;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> TopRateLoader = loaderManager.getLoader(FAVOURITE_MOVIE_LOADER);
        if (TopRateLoader == null) {
            loaderManager.initLoader(FAVOURITE_MOVIE_LOADER, null, this);
        } else {
            loaderManager.restartLoader(FAVOURITE_MOVIE_LOADER, null, this);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.popular) {
            viewPopularMovies();
        } else if (id == R.id.top_rate) {
            viewTOpRateMovies();
        } else if (id == R.id.favourite) {
            viewFavouriteMovies();
        }
        else if (id == R.id.activity_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<List<MovieModel>> onCreateLoader(int id, final Bundle args) {
        if(id==POPULAR_MOVIE_LOADER)
        {
            return new AsyncTaskLoader<List<MovieModel>>(this) {
                List<MovieModel> data;

                @Override
                protected void onStartLoading() {

                    if (data != null)
                    {
                        deliverResult(data);
                    }
                    else {
                        forceLoad();
                    }
                }

                @Override
                public List<MovieModel> loadInBackground() {

                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL("http://api.themoviedb.org/3/movie/popular?page="+popular_page+"&api_key=21ba08e3b68174860025a5de7e5640cc");
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
                        List<MovieModel> movieModelList = new ArrayList<>();
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject finalObject = parentArray.getJSONObject(i);
                            MovieModel movieModel = new MovieModel();
                            movieModel.setOriginal_title(finalObject.getString("original_title"));
                            movieModel.setOverview(finalObject.getString("overview"));
                            movieModel.setRelease_date(finalObject.getString("release_date"));
                            movieModel.setPoster_path(finalObject.getString("poster_path"));
                            movieModel.setBack_cover_path(finalObject.getString("backdrop_path"));
                            movieModel.setVote_average((float) finalObject.getDouble("vote_average"));
                            movieModel.setId(finalObject.getInt("id"));
                            movieModelList.add(movieModel);
                        }

                        return movieModelList;
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
                public void deliverResult(List<MovieModel> data) {
                    this.data=data;
                    super.deliverResult(data);
                }
            };

        }
        else if(id==TOP_RATE_MOVIE_LOADER){
            return new AsyncTaskLoader<List<MovieModel>>(this) {
                List<MovieModel> data;

                @Override
                protected void onStartLoading() {

                    if (data != null)
                    {
                        deliverResult(data);
                    }
                    else {
                        forceLoad();
                    }
                }

                @Override
                public List<MovieModel> loadInBackground() {

                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL("http://api.themoviedb.org/3/movie/top_rated?page="+top_page+"&api_key=21ba08e3b68174860025a5de7e5640cc");
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
                        List<MovieModel> movieModelList = new ArrayList<>();
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject finalObject = parentArray.getJSONObject(i);
                            MovieModel movieModel = new MovieModel();
                            movieModel.setOriginal_title(finalObject.getString("original_title"));
                            movieModel.setOverview(finalObject.getString("overview"));
                            movieModel.setRelease_date(finalObject.getString("release_date"));
                            movieModel.setPoster_path(finalObject.getString("poster_path"));
                            movieModel.setVote_average((float) finalObject.getDouble("vote_average"));
                            movieModel.setId(finalObject.getInt("id"));
                            movieModelList.add(movieModel);
                        }

                        return movieModelList;
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
                public void deliverResult(List<MovieModel> data) {
                    this.data=data;
                    super.deliverResult(data);
                }
            };
        }
        else if(id==FAVOURITE_MOVIE_LOADER){
            return new AsyncTaskLoader<List<MovieModel>>(this) {
                List<MovieModel> data;

                @Override
                protected void onStartLoading() {

                    if (data != null)
                    {
                        deliverResult(data);
                    }
                    else {
                        forceLoad();
                    }
                }

                @Override
                public List<MovieModel> loadInBackground() {


                        final List<MovieModel> movieModelList = new ArrayList<>();
                         res=getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                        res.moveToFirst();
                        while (res.isAfterLast()==false) {
                            MovieModel movieModel = new MovieModel();
                            movieModel.setPoster_path(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
                            movieModel.setOriginal_title(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                            movieModel.setOverview(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                            movieModel.setId(res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                            movieModel.setVote_average(res.getFloat(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATE)));
                            movieModel.setRelease_date(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE)));
                            movieModel.setBack_cover_path(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_COVER)));
                            movieModelList.add(movieModel);
                            res.moveToNext();
                        }
                        res.close();

                        return movieModelList;

                }

                @Override
                public void deliverResult(List<MovieModel> data) {
                    this.data=data;
                    super.deliverResult(data);
                }
            };
        }
        else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<MovieModel>> loader, List<MovieModel> result) {
        if (result != null) {
            adapter = new MovieAdapter(this, result, this);
            list.setLayoutManager(new GridLayoutManager(this,Integer.parseInt(columns)));
            list.setAdapter(adapter);
            list.setVisibility(View.VISIBLE);
            NoFavourite.setVisibility(View.INVISIBLE);
            if(loader.getId()==FAVOURITE_MOVIE_LOADER) {
                next.setVisibility(View.INVISIBLE);
                if (result.size() == 0) {
                    list.setVisibility(View.INVISIBLE);
                    NoFavourite.setText(getString(R.string.no_favourite_error));
                    NoFavourite.setVisibility(View.VISIBLE);
                } else {
                    list.setVisibility(View.VISIBLE);
                    NoFavourite.setVisibility(View.INVISIBLE);
                }
            }else {
                next.setVisibility(View.VISIBLE);
            }

        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.error), Toast.LENGTH_SHORT).show();
            if(loader.getId()==POPULAR_MOVIE_LOADER||loader.getId()==TOP_RATE_MOVIE_LOADER) {
                if (!networkUp()) {
                    list.setVisibility(View.INVISIBLE);
                    NoFavourite.setText(getString(R.string.no_connection_error));
                    NoFavourite.setVisibility(View.VISIBLE);
                    next.setVisibility(View.INVISIBLE);
                } else {
                    next.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    NoFavourite.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MovieModel>> loader) {
        res=null;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(String symbol) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (run)
        {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(FAVOURITE_MOVIE_LOADER, null, this);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

        private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_size_key))) {
            loadColumns(sharedPreferences);
        }
        else if (key.equals(getString(R.string.pref_order_key))) {
            Log.v("ON CHANGE","ON CHANGE");
            loadColors(sharedPreferences);
        }
    }
}
