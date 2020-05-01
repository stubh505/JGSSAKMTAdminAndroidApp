package com.joythakur.jgssakmtadmin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.joythakur.jgssakmtadmin.ui.model.Paragraph;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AddPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditPageRecyclerViewAdapter adapter;
    ArrayList<Paragraph> paras;
    private String posted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page);
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

        RecyclerView recyclerView = findViewById(R.id.addPageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paras = new ArrayList<>();

        adapter = new EditPageRecyclerViewAdapter(paras, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_page, menu);
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
        } else if (id == R.id.navEditBlog) {
            Intent i = new Intent(this, BlogActivity.class);
            startActivity(i);
        } else if (id == R.id.navEditPage) {
            Intent i = new Intent(this, PageActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addPara(View view) {
        Paragraph p = new Paragraph();
        paras.add(p);
        adapter.notifyDataSetChanged();
    }

    public void addPage(View view) {
        EditText pageHeader = findViewById(R.id.addPageHeaderAddText);
        final String header = pageHeader.getText().toString();
        EditText pageExcerpt = findViewById(R.id.addPageExcerptAddText);
        final String excerpt = pageExcerpt.getText().toString();
        EditText pageName = findViewById(R.id.addPageNameAddText);
        final String name = pageName.getText().toString();


        if (!header.matches("([\\w.?:;]+[ ]*)+")) {
            Snackbar.make(view, "Please enter a valid header", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (!excerpt.matches("([\\w.?:;]+[ ]*)+")) {
            Snackbar.make(view, "Please enter a valid excerpt", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (name.equals("") || name.matches("[ ]+")) {
            Snackbar.make(view, "Please enter a valid name", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {
            for (Paragraph p:paras) {
                if ((p.getImgUrl() == null || p.getImgUrl().equals("") || p.getImgUrl().matches("[ ]+"))
                        && (p.getBody() == null || p.getBody().equals("") || p.getBody().matches("[ ]+"))
                        && (p.getHeader() == null || p.getHeader().equals("") || p.getHeader().matches("[ ]+"))) {
                    paras.remove(p);
                }
            }
            for (Paragraph p:paras) {
                if (p.getHeader() == null || p.getHeader().equals("") || p.getHeader().matches("[ ]+")) {
                    Snackbar.make(view, "Insert valid headers for all the paragraphs", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                } else if (p.getImgUrl() == null || !p.getImgUrl().matches("(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?")
                        || p.getImgUrl().matches("[ ]+") || p.getImgUrl().equals("")) {

                    p.setImgUrl("");
                } else if (p.getBody() == null || p.getBody().equals("") || p.getBody().matches("[ ]+")){
                    Snackbar.make(view, "Insert a valid body for all paragraphs", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray paraArray = new JSONArray();

            for (Paragraph p:paras) {
                JSONObject paraObject = new JSONObject();
                paraObject.put("header", p.getHeader());
                paraObject.put("body", p.getBody());
                paraObject.put("imgUrl", p.getImgUrl());
                paraArray.put(paraObject);
            }
            jsonObject.put("name", name);
            jsonObject.put("header", header);
            jsonObject.put("excerpt", excerpt);
            jsonObject.put("paragraphs", paraArray);

            CallAPI call = new CallAPI();
            call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/PagesAPI/addNewPage/", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        private Integer pageId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                try(OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = data.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    pageId = Integer.parseInt(response.toString());
                }
                //urlConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent(AddPageActivity.this, ViewPageActivity.class);
            i.putExtra("pageId", pageId);
            startActivity(i);
        }
    }
}
