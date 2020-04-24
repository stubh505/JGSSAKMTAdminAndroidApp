package com.joythakur.jgssakmtadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.joythakur.jgssakmtadmin.ui.model.Events;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Integer eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(EditEventActivity.this)
                        .setTitle(R.string.delete_event)
                        .setMessage(R.string.del_event_body)
                        .setIcon(R.drawable.ic_delete_dark)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallDeleteAPI cd = new CallDeleteAPI();
                                cd.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/EventsAPI/deleteEvent/"+eventId);
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

        eventId = getIntent().getIntExtra("eventId", 1001);

        GetEvent getEvent = new GetEvent();
        getEvent.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/EventsAPI/getEvent/"+eventId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_event, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void saveEdits(View view) {
        EditText eventTitle = findViewById(R.id.eventTitleEditText);
        final String title = eventTitle.getText().toString();
        EditText eventExcerpt = findViewById(R.id.eventExcerptEditText);
        final String excerpt = eventExcerpt.getText().toString();
        EditText eventImgUrl = findViewById(R.id.eventImgurlEditText);
        final String imgUrl = eventImgUrl.getText().toString();
        EditText eventContent = findViewById(R.id.eventEditContentText);
        final String content = eventContent.getText().toString();

        EditText startDateEdit = findViewById(R.id.eventEditStartDateText);
        EditText startTimeEdit = findViewById(R.id.eventEditStartTimeText);
        EditText endDateEdit = findViewById(R.id.eventEditEndDateText);
        EditText endTimeEdit = findViewById(R.id.eventEditEndTimeText);
        final Date startDate, endDate;
        String startDateTime = startDateEdit.getText().toString() + " " + startTimeEdit.getText().toString();
        String endDateTime = endDateEdit.getText().toString() + " " + endTimeEdit.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm", Locale.ENGLISH);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S", Locale.ENGLISH);
        try {
            startDate = dateFormat.parse(startDateTime);
            endDate = dateFormat.parse(endDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            Snackbar.make(view, "Please enter valid event date and time", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }


        if (!title.matches("([\\w.?:;]+[ ]*)+")) {
            Snackbar.make(view, "Please enter a valid title", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (!excerpt.matches("([\\w.?:;]+[ ]*)+")) {
            Snackbar.make(view, "Please enter a valid excerpt", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (content.equals("") || content.matches("[ ]+")) {
            Snackbar.make(view, "Please enter a valid content", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {

            if (imgUrl.equals("") || imgUrl.matches("[ ]+")) {
                AlertDialog alert = new AlertDialog.Builder(EditEventActivity.this)
                        .setTitle(R.string.no_img)
                        .setMessage(R.string.no_img_body)
                        .setIcon(R.drawable.ic_alert_dark)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("name", title);
                                    jsonObject.put("imgUrl", imgUrl);
                                    jsonObject.put("excerpt", excerpt);
                                    jsonObject.put("description", content);
                                    assert startDate != null;
                                    jsonObject.put("startTime", format.format(startDate));
                                    assert endDate != null;
                                    jsonObject.put("endTime", format.format(endDate));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                CallAPI call = new CallAPI();
                                call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/EventsAPI/editEvent/" + eventId, jsonObject.toString());
                            }
                        })
                        .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alert.show();
            } else if (imgUrl.matches("(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?")) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", title);
                    jsonObject.put("imgUrl", imgUrl);
                    jsonObject.put("excerpt", excerpt);
                    jsonObject.put("description", content);
                    assert startDate != null;
                    jsonObject.put("startTime", format.format(startDate));
                    assert endDate != null;
                    jsonObject.put("endTime", format.format(endDate));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CallAPI call = new CallAPI();
                call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/EventsAPI/editEvent/" + eventId, jsonObject.toString());
            } else {
                Snackbar.make(view, "Insert a valid image URL", BaseTransientBottomBar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
    }

    private class GetEvent extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;
        Events b;

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

                b = new Events();
                b.setExcerpt(jsonObject.getString("excerpt"));
                b.setName(jsonObject.getString("name"));
                b.setImgUrl(jsonObject.getString("imgUrl"));
                b.setDescription(jsonObject.getString("description"));
                b.setEndTime(Timestamp.valueOf(jsonObject.getString("endTime").replace('T', ' ')));
                b.setStartTime(Timestamp.valueOf(jsonObject.getString("startTime").replace('T', ' ')));

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

        private void setData(Events b) {
            EditText eventTitle = findViewById(R.id.eventTitleEditText);
            eventTitle.setText(b.getName());
            EditText eventExcerpt = findViewById(R.id.eventExcerptEditText);
            eventExcerpt.setText(b.getExcerpt());
            EditText eventImgUrl = findViewById(R.id.eventImgurlEditText);
            eventImgUrl.setText(b.getImgUrl());
            EditText eventContent = findViewById(R.id.eventEditContentText);
            eventContent.setText(b.getDescription());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            EditText eventStartDate = findViewById(R.id.eventEditStartDateText);
            EditText eventStartTime = findViewById(R.id.eventEditStartTimeText);
            eventStartDate.setText(dateFormat.format(b.getStartTime()));
            eventStartTime.setText(timeFormat.format(b.getStartTime()));
            EditText eventEndDate = findViewById(R.id.eventEditEndDateText);
            EditText eventEndTime = findViewById(R.id.eventEditEndTimeText);
            eventEndDate.setText(dateFormat.format(b.getEndTime()));
            eventEndTime.setText(timeFormat.format(b.getEndTime()));
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
                    //Toast.makeText(EditEventActivity.this, response, Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(EditEventActivity.this, ViewEventActivity.class);
            i.putExtra("eventId", eventId);
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
                    //Toast.makeText(EditEventActivity.this, response, Toast.LENGTH_SHORT).show();
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