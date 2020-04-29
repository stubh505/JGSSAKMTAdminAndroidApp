package com.joythakur.jgssakmtadmin;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.joythakur.jgssakmtadmin.ui.model.Page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Page> pageList;
    private PageRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pageList = new ArrayList<>();

        GetPages getPages = new GetPages();

        getPages.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/PagesAPI/getAllPages");

        recyclerViewAdapter = new PageRecyclerViewAdapter(pageList, getApplicationContext());

        recyclerView = findViewById(R.id.blogRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.page, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navAddBlog) {
            Intent i = new Intent(this, AddBlogActivity.class);
            startActivity(i);
        } else if (id == R.id.navHome) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.navAddEvent) {
            Intent i = new Intent(this, AddEventActivity.class);
            startActivity(i);
        } else if (id == R.id.navEditEvent) {
            Intent i = new Intent(this, EventActivity.class);
            startActivity(i);
        } else if (id == R.id.navMessage) {
            Intent i = new Intent(this, MessageActivity.class);
            startActivity(i);
        } else if (id == R.id.navAddPage) {
            Intent i = new Intent(this, AddPageActivity.class);
            startActivity(i);
        } else if (id == R.id.navEditBlog) {
            Intent i = new Intent(this, BlogActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(
                    getApplicationContext(), 2, LinearLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        }
    }

    private class GetPages extends AsyncTask<String, Void, String> {

        JSONArray jsonArray;

        @Override
        protected String doInBackground(String... apis) {
            try {
                URL url = new URL(apis[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader ips = new InputStreamReader(inputStream);
                StringBuilder res = new StringBuilder();
                int data = ips.read();
                while (data != -1) {
                    res.append((char) data);
                    data = ips.read();
                }

                Page b;

                jsonArray = new JSONArray(res.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    b = new Page();
                    b.setPageId(jsonObject.getInt("pageId"));
                    b.setName(jsonObject.getString("name"));
                    b.setExcerpt(jsonObject.getString("excerpt"));
                    pageList.add(b);
                }

                return res.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerView.setAdapter(recyclerViewAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv = findViewById(R.id.noPages);
        tv.setAlpha(0.0f);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}