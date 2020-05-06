package com.joythakur.jgssakmtadmin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.joythakur.jgssakmtadmin.ui.model.Page;
import com.joythakur.jgssakmtadmin.ui.model.Paragraph;

import androidx.appcompat.app.ActionBarDrawerToggle;
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
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Integer pageId;
    private RecyclerView recyclerView;
    private ViewPageRecyclerViewAdapter adapter;
    private ArrayList<Paragraph> paras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewPageActivity.this, EditPageActivity.class);
                i.putExtra("pageId", pageId);
                startActivity(i);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.viewPageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pageId = getIntent().getIntExtra("pageId", 1001);

        GetPage getPage = new GetPage();
        getPage.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/PagesAPI/getPage/"+pageId);
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
        } else if (id == R.id.navEditPage) {
            Intent i = new Intent(this, PageActivity.class);
            startActivity(i);
        } else if (id == R.id.navEditBlog) {
            Intent i = new Intent(this, BlogActivity.class);
            startActivity(i);
        } else if (id == R.id.navAddCarousel) {
            Intent i = new Intent(this, AddCarouselActivity.class);
            startActivity(i);
        } else if (id == R.id.navDeleteCarousel) {
            Intent i = new Intent(this, CarouselActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                b.setEdited(Timestamp.valueOf(jsonObject.getString("edited").replace('T', ' ')));
                b.setPosted(Timestamp.valueOf(jsonObject.getString("posted").replace('T', ' ')));

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
            TextView pageName = findViewById(R.id.viewPageHeader);
            pageName.setText(b.getHeader());
            TextView pageEdit = findViewById(R.id.viewPagePostedEdited);
            SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
            String date = format.format(b.getEdited()) + "/" + format.format(b.getPosted());
            pageEdit.setText(date);

            adapter = new ViewPageRecyclerViewAdapter(paras, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }

    }
}
