package com.joythakur.jgssakmtadmin;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joythakur.jgssakmtadmin.ui.model.Paragraph;

import java.util.ArrayList;

public class EditPageRecyclerViewAdapter extends RecyclerView.Adapter<EditPageRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Paragraph> mDataSet;
    private Context context;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText paraHeader;
        private final EditText paraBody;
        private final EditText paraImage;
        private final TextView paraTitle;
        private final ImageView deletePara;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            paraHeader = v.findViewById(R.id.paraHeaderEdit);
            paraBody = v.findViewById(R.id.paraBodyEdit);
            paraImage = v.findViewById(R.id.paraImageEdit);
            deletePara = v.findViewById(R.id.paraDelete);
            paraTitle = v.findViewById(R.id.paraTitle);

            deletePara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataSet.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            paraHeader.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mDataSet.get(getAdapterPosition()).setHeader(s.toString());
                }
            });

            paraBody.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mDataSet.get(getAdapterPosition()).setBody(s.toString());
                }
            });

            paraImage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mDataSet.get(getAdapterPosition()).setImgUrl(s.toString());
                }
            });
        }

        TextView getParaTitle() {
            return paraTitle;
        }

        EditText getParaHeader() {
            return paraHeader;
        }

        EditText getParaBody() {
            return paraBody;
        }

        EditText getParaImage() {
            return paraImage;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the data set of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    EditPageRecyclerViewAdapter(ArrayList<Paragraph> dataSet, Context context) {
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
                .inflate(R.layout.card_edit_paragraph, viewGroup, false);

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
        viewHolder.getParaTitle().setText("Paragraph " + (position+1));
        viewHolder.getParaHeader().setText(mDataSet.get(position).getHeader());
        viewHolder.getParaBody().setText(mDataSet.get(position).getBody());
        if (mDataSet.get(position).getImgUrl() != null && mDataSet.get(position).getImgUrl().equals("null"))
            viewHolder.getParaImage().setText(mDataSet.get(position).getImgUrl());
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}