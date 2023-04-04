package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.FavoriteLocation;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;

public class FavoriteLocationAdapter extends RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolder>{
    private FavoriteLocation[] favoriteLocations={
            new FavoriteLocation(R.drawable.bali,"Bali"),
            new FavoriteLocation(R.drawable.bali,"Bali"),
            new FavoriteLocation(R.drawable.bali,"Bali"),
            new FavoriteLocation(R.drawable.bali,"Bali"),
            new FavoriteLocation(R.drawable.bali,"Bali"),
            new FavoriteLocation(R.drawable.bali,"Bali")
            };
    Context context;

    private FavoriteLocationAdapter.Callbacks listener;
    public void setListener(FavoriteLocationAdapter.Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToLocationInfo();
    }

    /**
     * Initialize the posts of the Adapter.
     * <p>
     * param posts String[] containing the data to populate views to be used
     * by RecyclerView.
     */

    public FavoriteLocationAdapter(Context _context) {
        this.context = _context;
    }

//    public FavoriteLocationAdapter(Context _context, ArrayList<String> posts) {
//        this.context = _context;
//        this.posts = posts;
//    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView main_image;
        private  final TextView name;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = (TextView) view.findViewById(R.id.location_name);
            main_image = (ImageView) view.findViewById(R.id.location_image);

            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public FavoriteLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_favorite_location_item, viewGroup, false);

        return new FavoriteLocationAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FavoriteLocationAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your posts at this position and replace the
        // contents of the view with that element
        viewHolder.name.setText(favoriteLocations[position].getName());

        viewHolder.main_image.setImageResource(favoriteLocations[position].getImage());

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                listener.swapToLocationInfo();
            }
        });
    }

    // Return the size of your posts (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return favoriteLocations.length;
    }
}
