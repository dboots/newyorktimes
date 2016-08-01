package com.donboots.newyorktimes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.donboots.newyorktimes.R;
import com.donboots.newyorktimes.adapters.ArticleArrayAdapter;
import com.donboots.newyorktimes.models.Article;
import com.donboots.newyorktimes.models.FilterSettings;
import com.donboots.newyorktimes.EndlessScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements FilterFragment.FilterDialogListener {
    GridView gvResults;
    ScrollView scrollView;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    FilterFragment filterFragment;
    FragmentManager fm;
    FilterSettings filterSettings = new FilterSettings();
    String query;
    int current_page = 0;

    @Override
    public void onFinishEditDialog(FilterSettings fs) {
        filterSettings = fs;
        onArticleSearch(query, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterSettings.setBeginDate("20160101");
        filterSettings.setSortOrder("Newest");

        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("Arts", true);
        map.put("Fashion & Beauty", true);
        map.put("Sports", true);

        filterSettings.setNewsDesks(map);

        fm = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                onArticleSearch(query, true);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    public void onArticleSearch(String query, boolean append) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();

        params.put("api-key", "64b65865710f4387b29a51b0f6d19592");
        params.put("page", current_page);
        params.put("q", query);

        params.put("sort", filterSettings.getSortOrder());
        params.put("begin_date", filterSettings.getBeginDate());
        params.put("fq", filterSettings.getNewsDesks());

        current_page++;
        if (!append)
            articles.clear();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray results = null;

                try {
                    results = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(results));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //searchItem.expandActionView();
        //searchView.requestFocus();

        int searchImgId = android.support.v7.appcompat.R.id.search_button;

        ImageView v = (ImageView) searchView.findViewById(searchImgId);

        //v.setImageResource(R.drawable.ic_launcher);
        // Customize searchview text and hint colors

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String submitQuery) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                query = submitQuery;
                onArticleSearch(query, false);
                onArticleSearch(query, true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void menuSettings(MenuItem item) {
        filterFragment = new FilterFragment();
        filterFragment.show(fm, "activity_settings");
    }
}
