package com.sanmay.intenshala.adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanmay.intenshala.R;

// DashboardAdapter.java
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private Cursor mCursor;
    private AdapterView.OnItemClickListener listener;

    private OnItemClickListener mListener;

    public DashboardAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout, parent, false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(String title, String description);
    }

    // Constructor and other methods...

    // Setter method to set the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        int titleIndex = mCursor.getColumnIndex("title");
        int descriptionIndex = mCursor.getColumnIndex("description");

        if (titleIndex == -1 || descriptionIndex == -1) {
            return; // Column not found
        }

        String title = mCursor.getString(titleIndex);
        String description = mCursor.getString(descriptionIndex);

        holder.textViewTitle.setText(title);
        holder.textViewDescription.setText(description);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {

                    mListener.onItemClick(title,description);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
