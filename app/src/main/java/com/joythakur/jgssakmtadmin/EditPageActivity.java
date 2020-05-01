package com.joythakur.jgssakmtadmin;

import android.content.DialogInterface;
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
import com.joythakur.jgssakmtadmin.ui.model.Page;
import com.joythakur.jgssakmtadmin.ui.model.Paragraph;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Integer pageId;
    private RecyclerView recyclerView;
    private EditPageRecyclerViewAdapter adapter;
    ArrayList<Paragraph> paras;
    private String posted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(EditPageActivity.this)
                        .setTitle(R.string.delete_page)
                        .setMessage(R.string.del_page_body)
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallDeleteAPI cd = new CallDeleteAPI();
                                cd.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/PagesAPI/deletePage/"+pageId);
                            }
                        })
                        .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alert.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.editPageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pageId = getIntent().getIntExtra("pageId", 1001);

        GetPage getPage = new GetPage();
        getPage.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/PagesAPI/getPage/"+pageId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_page, menu);
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

    public void addPara(View view) {
        Paragraph p = new Paragraph();
        paras.add(p);
        adapter.notifyDataSetChanged();
    }

    public void savePage(View view) {
        EditText pageHeader = findViewById(R.id.editPageHeaderEditText);
        final String header = pageHeader.getText().toString();
        EditText pageExcerpt = findViewById(R.id.editPageExcerptEditText);
        final String excerpt = pageExcerpt.getText().toString();
        EditText pageName = findViewById(R.id.editPageNameEditText);
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
                paraObject.put("paragraphId", 0);
                paraObject.put("header", p.getHeader());
                paraObject.put("body", p.getBody());
                paraObject.put("imgUrl", p.getImgUrl());
                paraArray.put(paraObject);
            }
            jsonObject.put("posted", posted);
            jsonObject.put("name", name);
            jsonObject.put("header", header);
            jsonObject.put("excerpt", excerpt);
            jsonObject.put("paragraphs", paraArray);

            CallAPI call = new CallAPI();
            call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/PagesAPI/editPage/" + pageId, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetPage extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;
        Page b;

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

                jsonObject = new JSONObject(res.toString());

                // = null;

                b = new Page();
                b.setExcerpt(jsonObject.getString("excerpt"));
                b.setName(jsonObject.getString("name"));
                b.setHeader(jsonObject.getString("header"));
                posted = jsonObject.getString("posted");

                JSONArray jsonArray = jsonObject.getJSONArray("paragraphs");

                if (jsonArray.length()>0) {
                    paras = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject para = jsonArray.getJSONObject(i);

                        Paragraph p = new Paragraph();
                        p.setBody(para.getString("body"));
                        p.setHeader(para.getString("header"));
                        p.setImgUrl(para.getString("imgUrl"));
                        p.setParagraphId(para.getInt("paragraphId"));

                        paras.add(p);
                    }
                }

                b.setParagraphs(paras);

                return res.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            setData(b);
        }

        private void setData(Page b) {
            EditText pageName = findViewById(R.id.editPageNameEditText);
            pageName.setText(b.getName());
            EditText pageHeader = findViewById(R.id.editPageHeaderEditText);
            pageHeader.setText(b.getHeader());
            EditText pageExcerpt = findViewById(R.id.editPageExcerptEditText);
            pageExcerpt.setText(b.getExcerpt());

            adapter = new EditPageRecyclerViewAdapter(paras, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }

    }

    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1];

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
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
                    //Toast.makeText(EditPageActivity.this, response, Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(EditPageActivity.this, ViewPageActivity.class);
            i.putExtra("pageId", pageId);
            startActivity(i);
        }
    }

    private class CallDeleteAPI extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.connect();

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    //Toast.makeText(EditPageActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            finish();
        }
    }

}
