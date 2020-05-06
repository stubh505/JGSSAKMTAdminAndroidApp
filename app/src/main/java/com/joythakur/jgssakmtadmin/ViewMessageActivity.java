package com.joythakur.jgssakmtadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.joythakur.jgssakmtadmin.ui.model.ContactUs;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ViewMessageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Integer id;
    private TextView name;
    private TextView message;
    private TextView mobile;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(ViewMessageActivity.this)
                        .setTitle(R.string.del_message)
                        .setMessage(R.string.del_message_body)
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallDeleteAPI cd = new CallDeleteAPI();
                                cd.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/ContactUsAPI/deleteMessage/"+id);
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

        id = getIntent().getIntExtra("id", 0);

        GetMessage blog = new GetMessage();
        blog.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/ContactUsAPI/getMessage/"+id);

        name = findViewById(R.id.messageNameHolder);
        message = findViewById(R.id.messageBodyHolder);
        email = findViewById(R.id.messageEmailHolder);
        mobile = findViewById(R.id.messageMobileHolder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message, menu);
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
            Intent i = new Intent(this, EditBlogActivity.class);
            startActivity(i);
        } else if (id == R.id.navAddPage) {
            Intent i = new Intent(this, AddPageActivity.class);
            startActivity(i);
        } else if (id == R.id.navEditPage) {
            Intent i = new Intent(this, PageActivity.class);
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

    private class GetMessage extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;
        ContactUs b;

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

                b = new ContactUs();
                b.setName(jsonObject.getString("name"));
                b.setMobile(jsonObject.getString("mobile"));
                b.setMessage(jsonObject.getString("message"));
                b.setEmail(jsonObject.getString("email"));

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

        private void setData(ContactUs b) {
            name.setText(b.getName());
            email.setText(b.getEmail());
            message.setText(b.getMessage());
            mobile.setText(b.getMobile());
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
