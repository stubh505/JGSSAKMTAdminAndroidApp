package com.joythakur.jgssakmtadmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joythakur.jgssakmtadmin.ui.model.Paragraph;

import java.io.InputStream;
import java.util.ArrayList;

public class ViewPageRecyclerViewAdapter extends RecyclerView.Adapter<ViewPageRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Paragraph> mDataSet;
    private Context context;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView paraHeader;
        private final TextView paraBody;
        private final ImageView paraImage;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");

                }
            });

            paraHeader = v.findViewById(R.id.cardViewParaHeader);
            paraBody = v.findViewById(R.id.cardViewParaBody);
            paraImage = v.findViewById(R.id.cardViewParaImage);
        }

        TextView getParaHeader() {
            return paraHeader;
        }

        TextView getParaBody() {
            return paraBody;
        }

        ImageView getParaImage() {
            return paraImage;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the data set of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    ViewPageRecyclerViewAdapter(ArrayList<Paragraph> dataSet, Context context) {
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
                .inflate(R.layout.card_view_paragraph, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Log.d(TAG, "Element " + position + " set.");

        // Get element from your data set at this position and replace the contents of the view
        // with that element
        Spanned htmlAsSpanned = Html.fromHtml(mDataSet.get(position).getBody());
        viewHolder.getParaHeader().setText(mDataSet.get(position).getHeader());
        viewHolder.getParaBody().setText(htmlAsSpanned);
        new DownloadImageTask(viewHolder.getParaImage())
                .execute(mDataSet.get(position).getImgUrl());
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

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
}