package com.joythakur.jgssakmtadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joythakur.jgssakmtadmin.ui.model.Events;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Events> mDataSet;
    private Context context;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView eventImage;
        private final TextView eventTitle;
        private final TextView eventExcerpt;
        private final TextView eventStart;
        private final TextView eventEnd;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            eventImage = v.findViewById(R.id.eventImage);
            eventTitle = v.findViewById(R.id.eventTitle);
            eventExcerpt = v.findViewById(R.id.eventExcerpt);
            eventEnd = v.findViewById(R.id.eventEnd);
            eventStart = v.findViewById(R.id.eventStart);
        }

        TextView getEventStart() {
            return eventStart;
        }

        TextView getEventEnd() {
            return eventEnd;
        }

        TextView getEventTitle() {
            return eventTitle;
        }

        TextView getEventExcerpt() {
            return eventExcerpt;
        }

        ImageView getEventImage() {
            return eventImage;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    EventRecyclerViewAdapter(ArrayList<Events> dataSet, Context context) {
        mDataSet = dataSet;
        this.context = context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_event, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy hh:mm a", Locale.ENGLISH);

        viewHolder.getEventTitle().setText(mDataSet.get(position).getName());
        new DownloadImageTask(viewHolder.getEventImage())
                .execute(mDataSet.get(position).getImgUrl());
        viewHolder.getEventExcerpt().setText(mDataSet.get(position).getExcerpt());
        viewHolder.getEventEnd().setText(format.format(mDataSet.get(position).getEndTime()));
        viewHolder.getEventStart().setText(format.format(mDataSet.get(position).getStartTime()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditEventActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("eventId", mDataSet.get(position).getEventId());
                context.startActivity(i);
            }
        });
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}