package com.android.ahmed.movieapp.ui;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ahmed.movieapp.R;
import com.android.ahmed.movieapp.adapter.ReviewAdapter;
import com.android.ahmed.movieapp.models.MovieReviews;

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

public class ReviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieReviews>> {
    int ID;
    ListView lvReviews;
    TextView NoResult;
    private static final int REVIEWS_LOADER = 5;
    private static final String SEARCH_QUERY_URL_EXTRA = "URL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getSupportActionBar().setTitle(getString(R.string.reviews));
        lvReviews=(ListView) findViewById(R.id.review_list);
        NoResult=(TextView)findViewById(R.id.no_result);
        Bundle extras = getIntent().getExtras();
        ID = extras.getInt("ID");
        viewReview(ID);
    }

    private void viewReview(int id) {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=21ba08e3b68174860025a5de7e5640cc");

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> reviewLoader = loaderManager.getLoader(REVIEWS_LOADER);
        if (reviewLoader == null) {
            loaderManager.initLoader(REVIEWS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(REVIEWS_LOADER, queryBundle, this);
        }
    }

    @Override
    public Loader<List<MovieReviews>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<MovieReviews>>(this) {
            List<MovieReviews> data;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (data != null) {
                    deliverResult(data);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<MovieReviews> loadInBackground() {
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
                    List<MovieReviews> movieReviewsList = new ArrayList<>();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        MovieReviews movieReviews = new MovieReviews();
                        movieReviews.setAuthor(finalObject.getString("author"));
                        movieReviews.setContent(finalObject.getString("content"));
                        Log.v("USER",finalObject.getString("author"));
                        movieReviewsList.add(movieReviews);
                    }
                    return movieReviewsList;
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
            public void deliverResult(List<MovieReviews> data) {
                this.data = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<MovieReviews>> loader, final List<MovieReviews> result) {
        if (result != null) {
            ReviewAdapter adapter = new ReviewAdapter(getApplicationContext(), R.layout.review_item, result);
            lvReviews.setAdapter(adapter);
            if(result.size()==0)
            {
                lvReviews.setVisibility(View.INVISIBLE);
                NoResult.setVisibility(View.VISIBLE);
            }
            else
            {
                lvReviews.setVisibility(View.VISIBLE);
                NoResult.setVisibility(View.INVISIBLE);
            }

        } else {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<MovieReviews>> loader) {

    }


}
