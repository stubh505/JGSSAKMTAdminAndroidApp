package com.joythakur.jgssakmtadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.joythakur.jgssakmtadmin.ui.model.Blogs;

import androidx.appcompat.app.AlertDialog;
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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditBlogActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
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
                AlertDialog alert = new AlertDialog.Builder(EditBlogActivity.this)
                        .setTitle(R.string.delete_blog)
                        .setMessage(R.string.del_blog_body)
                        .setIcon(R.drawable.ic_delete_dark)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallDeleteAPI cd = new CallDeleteAPI();
                                cd.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/BlogsAPI/deleteBlog/"+blogId);
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

        GetBlog getBlog = new GetBlog();
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

    public void saveEdits(View view) {
        EditText blogTitle = findViewById(R.id.blogTitleEditText);
        final String title = blogTitle.getText().toString();
        EditText blogExcerpt = findViewById(R.id.blogExcerptEditText);
        final String excerpt = blogExcerpt.getText().toString();
        EditText blogImgUrl = findViewById(R.id.blogImgurlEditText);
        final String imgUrl = blogImgUrl.getText().toString();
        EditText blogContent = findViewById(R.id.blogEditContentText);
        final String content = blogContent.getText().toString();

        if (!title.matches("([\\w.?:;]+[\\s]*)+")) {
            Snackbar.make(view, "Please enter a valid title", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (!excerpt.matches("([\\w.?:;]+[\\s]*)+")) {
            Snackbar.make(view, "Please enter a valid excerpt", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (content.equals("") || content.matches("[\\s]+")) {
            Snackbar.make(view, "Please enter a valid content", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {

            if (imgUrl.equals("") || imgUrl.matches("[ ]+")) {
                AlertDialog alert = new AlertDialog.Builder(EditBlogActivity.this)
                        .setTitle(R.string.no_img)
                        .setMessage(R.string.no_img_body)
                        .setIcon(R.drawable.ic_delete_dark)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/BlogsAPI/editBlog/" + blogId, jsonObject.toString());
                            }
                        })
                        .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //return;
                                dialog.dismiss();
                            }
                        }).create();
                alert.show();
            } else if (imgUrl.matches("(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?")) {

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
                call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/BlogsAPI/editBlog/" + blogId, jsonObject.toString());
            } else {
                Snackbar.make(view, "Insert a valid image URL", BaseTransientBottomBar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
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
                StringBuilder res = new StringBuilder();
                int data = ips.read();
                while (data != -1) {
                    res.append((char) data);
                    data = ips.read();
                }

                jsonObject = new JSONObject(res.toString());

                b = new Blogs();
                b.setExcerpt(jsonObject.getString("excerpt"));
                b.setTitle(jsonObject.getString("title"));
                b.setImgUrl(jsonObject.getString("imgUrl"));
                b.setContent(jsonObject.getString("content"));

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
//            OutputStream out = null;

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
                    //Toast.makeText(EditBlogActivity.this, response, Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(EditBlogActivity.this, ViewBlogActivity.class);
            i.putExtra("blogId", blogId);
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
                    //Toast.makeText(EditBlogActivity.this, response, Toast.LENGTH_SHORT).show();
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

    public void cancel(View view) {
        finish();
    }
}
