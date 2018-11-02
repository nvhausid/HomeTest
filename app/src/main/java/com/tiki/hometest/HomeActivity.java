package com.tiki.hometest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tiki.hometest.keyword.KeywordAdapter;
import com.tiki.hometest.keyword.KeywordClickListener;
import com.tiki.hometest.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements KeywordClickListener {

    // Keyword stuffs
    public static final String KEYWORD_URL =
            "https://gist.githubusercontent.com/talenguyen/38b790795722e7d7b1b5db051c5786e5/raw/" +
                    "63380022f5f0c9a100f51a1e30887ca494c3326e/keywords.json";
    public static final String KEYWORD_REQUEST_TAG = "KEYWORD_REQUEST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Setup keyword request
        JsonArrayRequest request = new JsonArrayRequest(
                KEYWORD_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showKeyword(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something here
                        Toast.makeText(HomeActivity.this, "Cannot fetch keywords!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Set request tag so we can cancel it on need
        request.setTag(KEYWORD_REQUEST_TAG);

        // Send keyword request
        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Cancel keyword request
        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().
                cancelAll(KEYWORD_REQUEST_TAG);
    }

    @Override
    public void OnKeywordClick(String keyword) {
        // Do something here
        Toast.makeText(this, keyword, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show keywords into their container.
     *
     * @param keywordArray Array of keywords to be shown
     */
    private void showKeyword(JSONArray keywordArray) {
        ArrayList<String> keywordList = new ArrayList<>();
        try {
            int length = keywordArray.length();
            for (int i = 0; i < length; i++) {
                keywordList.add(keywordArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Load keyword background colors
        String[] colors = getResources().getStringArray(R.array.keyword_colors);
        int[] itemColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            itemColors[i] = Color.parseColor(colors[i]);
        }

        RecyclerView keywordView = findViewById(R.id.keyword_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        KeywordAdapter keywordAdapter = new KeywordAdapter(keywordList, itemColors, this);

        keywordView.setLayoutManager(layoutManager);
        keywordView.setAdapter(keywordAdapter);

        // Add item divider
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.keyword_divider));
        keywordView.addItemDecoration(itemDecorator);
    }
}
