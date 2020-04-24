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
import com.joythakur.jgssakmtadmin.ui.model.Events;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEventActivity extends FragmentActivity implements
        DatePickerFragment.DatePickedListener, TimePickerFragment.TimePickedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private EditText dateView;
    private EditText timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        dateView = findViewById(R.id.addEventStartDateEdit);
        timeView = findViewById(R.id.addEventStartTimeEdit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_event, menu);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    public void showDatePickerDialog(View v) {
        dateView = (EditText) v;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        timeView = (EditText) v;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDatePicked(String date) {
        dateView.setText(date);
    }

    @Override
    public void onTimePicked(String time) {
        timeView.setText(time);
    }

    public void addEvent(View view) {
        EditText eventTitle = findViewById(R.id.eventTitleAddText);
        final String title = eventTitle.getText().toString();
        EditText eventExcerpt = findViewById(R.id.eventExcerptAddText);
        final String excerpt = eventExcerpt.getText().toString();
        EditText eventImgUrl = findViewById(R.id.eventImgurlAddText);
        final String imgUrl = eventImgUrl.getText().toString();
        EditText eventContent = findViewById(R.id.eventAddContentText);
        final String content = eventContent.getText().toString();

        if (!title.matches("([\\w.?:;]+[ ]*)+")) {
            Snackbar.make(view, "Please enter a valid event name", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (excerpt.equals("") || excerpt.matches("[ ]+")) {
            Snackbar.make(view, "Please enter a valid event excerpt", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (content.equals("") || content.matches("[ ]*")) {
            Snackbar.make(view, "Please enter a valid event description", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            EditText startDateEdit = findViewById(R.id.addEventStartDateEdit);
            EditText startTimeEdit = findViewById(R.id.addEventStartTimeEdit);
            EditText endDateEdit = findViewById(R.id.addEventEndDateEdit);
            EditText endTimeEdit = findViewById(R.id.addEventEndTimeEdit);

            final Date startDate, endDate;
            String startDateTime = startDateEdit.getText().toString() + " " + startTimeEdit.getText().toString();
            String endDateTime = endDateEdit.getText().toString() + " " + endTimeEdit.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm", Locale.ENGLISH);
            try {
                startDate = dateFormat.parse(startDateTime);
                endDate = dateFormat.parse(endDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
                Snackbar.make(view, "Please enter valid event date and time", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S", Locale.ENGLISH);

            if (imgUrl.equals("") || imgUrl.matches("[ ]+")) {
                AlertDialog alert = new AlertDialog.Builder(AddEventActivity.this)
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
                                call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/EventsAPI/addNewEvent", jsonObject.toString());
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
                call.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/EventsAPI/addNewEvent", jsonObject.toString());
            } else {
                Snackbar.make(view, "Insert a valid image URL", BaseTransientBottomBar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
    }

    public void cancel(View view) {
        finish();
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        private Integer eventId;

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
                    eventId = Integer.parseInt(response.toString());
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
            Intent i = new Intent(AddEventActivity.this, ViewEventActivity.class);
            i.putExtra("eventId", eventId);
            startActivity(i);
        }
    }
}
