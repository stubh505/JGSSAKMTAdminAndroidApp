package com.joythakur.jgssakmtadmin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.joythakur.jgssakmtadmin.ui.model.Blogs;

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
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class BlogActivity extends AppCompatActivity {

    private GetBlogs getBlogs;
    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList<Blogs> blogs;
    private BlogRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BlogActivity.this, AddBlogActivity.class);
                startActivity(i);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        blogs = new ArrayList<>();

        getBlogs = new GetBlogs();

        getBlogs.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/BlogsAPI/getAllBlogs");

        recyclerViewAdapter = new BlogRecyclerViewAdapter(blogs, getApplicationContext());

        recyclerView = findViewById(R.id.blogRecyclerView);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blog, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class GetBlogs extends AsyncTask<String, Void, String> {

        JSONArray jsonArray;

        @Override
        protected String doInBackground(String... apis) {
            try {
                URL url = new URL(apis[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader ips = new InputStreamReader(inputStream);
                String res = "";
                int data = ips.read();
                while (data != -1) {
                    res = res + (char) data;
                    data = ips.read();
                }

                Blogs b;

                jsonArray = new JSONArray(res);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    b = new Blogs();
                    b.setBlogId(jsonObject.getInt("blogId"));
                    b.setImgUrl(jsonObject.getString("imgUrl"));
                    b.setTitle(jsonObject.getString("title"));
                    b.setExcerpt(jsonObject.getString("excerpt"));
                    blogs.add(b);
                }

                /*for (int i = 0; i < jsonArray.length(); i++) {
                    Gson gson = new Gson();
                    String json = jsonArray.getJSONObject(i).toString();
                    System.out.println(json);
                    b = gson.fromJson(json, Blogs.class);
                    blogs.add(b);
                }*/

                /*Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<Blogs>>(){}.getType();
                blogs = gson.fromJson(String.valueOf(jsonArray), collectionType);*/

                return res;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            // recyclerViewAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(recyclerViewAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
    }
}