package com.joythakur.jgssakmtadmin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.joythakur.jgssakmtadmin.ui.model.Blogs;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditBlogActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Blogs blog;
    private GetBlog getBlog;
    private Integer blogId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blog);
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        blogId = getIntent().getIntExtra("blogId", 1001);

        getBlog = new GetBlog();
        getBlog.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/BlogsAPI/getBlog/"+blogId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_blog, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setData(Blogs b) {
        EditText blogTitle = findViewById(R.id.blogTitleEditText);
        blogTitle.setText(b.getTitle());
        EditText blogExcerpt = findViewById(R.id.blogExcerptEditText);
        blogExcerpt.setText(b.getExcerpt());
        EditText blogImgUrl = findViewById(R.id.blogImgurlEditText);
        blogImgUrl.setText(b.getImgUrl());
        EditText blogContent = findViewById(R.id.blogEditContentText);
        blogContent.setText(b.getContent());
    }

    public void saveEdits(View view) {
        EditText blogTitle = findViewById(R.id.blogTitleEditText);
        String title = blogTitle.getText().toString();
        EditText blogExcerpt = findViewById(R.id.blogExcerptEditText);
        String excerpt = blogExcerpt.getText().toString();
        EditText blogImgUrl = findViewById(R.id.blogImgurlEditText);
        String imgUrl = blogImgUrl.getText().toString();
        EditText blogContent = findViewById(R.id.blogEditContentText);
        String content = blogContent.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("imgUrl", imgUrl);
            jsonObject.put("excerpt", excerpt);
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CallAPI call = new CallAPI();
        call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/BlogsAPI/editBlog/"+blogId, jsonObject.toString());
    }

    private class GetBlog extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;
        Blogs b;

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

                jsonObject = new JSONObject(res);

                System.out.println(jsonObject);

                b = new Blogs();
                b.setExcerpt(jsonObject.getString("excerpt"));
                b.setTitle(jsonObject.getString("title"));
                b.setImgUrl(jsonObject.getString("imgUrl"));
                b.setContent(jsonObject.getString("content"));

                return res;
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
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();
//                urlConnection.setChunkedStreamingMode(0);
//                out = new BufferedOutputStream(urlConnection.getOutputStream());
//
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                out.close();

                try(OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
                //urlConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
