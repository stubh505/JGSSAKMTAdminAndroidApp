package com.joythakur.jgssakmtadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.joythakur.jgssakmtadmin.ui.model.Carousel;

import java.io.InputStream;
import java.util.ArrayList;

public class CarouselRecyclerViewAdapter extends RecyclerView.Adapter<CarouselRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Carousel> mDataSet;
    private Context context;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView carouselImage;
        private final TextView carouselTitle;
        private final TextView carouselExcerpt;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");

                }
            });

            carouselImage = v.findViewById(R.id.cardCarouselImage);
            carouselTitle = v.findViewById(R.id.cardCarouselLabel);
            carouselExcerpt = v.findViewById(R.id.cardCarouselBody);
        }

        TextView getCarouselTitle() {
            return carouselTitle;
        }

        TextView getCarouselExcerpt() {
            return carouselExcerpt;
        }

        ImageView getCarouselImage() {
            return carouselImage;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    CarouselRecyclerViewAdapter(ArrayList<Carousel> dataSet, Context context) {
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
                .inflate(R.layout.card_carousel, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getCarouselTitle().setText(mDataSet.get(position).getLabel());
        new DownloadImageTask(viewHolder.getCarouselImage())
                .execute(mDataSet.get(position).getImage());
        viewHolder.getCarouselExcerpt().setText(mDataSet.get(position).getBody());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(context)
                        .setTitle(R.string.cara_del_head)
                        .setMessage(R.string.cara_del_body)
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton(R.string.button_affirmative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CarouselActivity.CallDeleteAPI api = new CarouselActivity.CallDeleteAPI();
                                api.execute("http://jgssakmtback.herokuapp.com/jgssakmt_backend/CarouselAPI/deleteCarousel/"+mDataSet.get(position).getId());
                                mDataSet.remove(position);
                                notifyDataSetChanged();
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