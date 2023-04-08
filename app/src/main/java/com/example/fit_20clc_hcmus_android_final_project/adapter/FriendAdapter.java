package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

//    private String[] localDataSet = {
//            "User 1",
//            "User 2",
//            "User 3",
//            "User 4",
//    };
//
//    private ArrayList<String> dataSet = new ArrayList<>(Arrays.asList(localDataSet));

    private ArrayList<User> dataSet;

    Context context;

    private FriendAdapter.Callbacks listener;

    public void setListener(FriendAdapter.Callbacks listener) {
        this.listener = listener;
    }

    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToChat();
    }

//    public FriendAdapter(Context _context) {
//        this.context = _context;
//    }

    public FriendAdapter(Context _context, ArrayList<User> _dataset) {
        this.context = _context;
        this.dataSet = _dataset;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        private final ImageView main_image;
        private  final TextView name;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = (TextView) view.findViewById(R.id.username);
//            main_image = (ImageView) view.findViewById(R.id.location_image);

            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_friend_item, viewGroup, false);

        return new FriendAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FriendAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your posts at this position and replace the
        // contents of the view with that element
        viewHolder.name.setText(dataSet.get(position).getName());
//
//        viewHolder.main_image.setImageResource(favoriteLocations[position].getImage());

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                listener.swapToChat();
            }
        });
    }

    // Return the size of your posts (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataSet!=null)
            return dataSet.size();
        else return 0;
    }
}
