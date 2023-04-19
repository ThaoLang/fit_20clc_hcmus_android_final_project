package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.CommentActivity;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.DetailedPost;

import java.util.List;

public class TripLocationAdapter extends RecyclerView.Adapter<TripLocationAdapter.ViewHolder>{
    
//    private Destination[] destinations={
//            new Destination("Dam Sen Park",
//                            "123 An Duong Vuong Street, Ward 5, District 1, Ho Chi Minh City",
//                    "10am","5pm","03/04/2023","05/04/2023",
//                            "Ride roller coasters, ride ferris wheel, try 5D movies, play thrilling games",
//                            "78 Bach Dang Street, Ward 11, Binh Thanh District, Ho Chi Minh City"),
//            new Destination("Waterbus",
//                            "123 An Duong Vuong Street, Ward 5, District 1, Ho Chi Minh City",
//                    "10am","5pm","03/04/2023","05/04/2023",
//                            "Ride roller coasters, ride ferris wheel, try 5D movies, play thrilling games",
//                    "78 Bach Dang Street, Ward 11, Binh Thanh District, Ho Chi Minh City"),
//            new Destination("Sapa",
//                            "123 An Duong Vuong Street, Ward 5, District 1, Ho Chi Minh City",
//                    "10am","5pm","03/04/2023","05/04/2023",
//                            "Ride roller coasters, ride ferris wheel, try 5D movies, play thrilling games",
//                    "78 Bach Dang Street, Ward 11, Binh Thanh District, Ho Chi Minh City"),
//            new Destination("Madagui",
//                            "123 An Duong Vuong Street, Ward 5, District 1, Ho Chi Minh City",
//                    "10am","5pm","03/04/2023","05/04/2023",
//                            "Ride roller coasters, ride ferris wheel, try 5D movies, play thrilling games",
//                    "78 Bach Dang Street, Ward 11, Binh Thanh District, Ho Chi Minh City"),
//            new Destination("Da Lat",
//                            "123 An Duong Vuong Street, Ward 5, District 1, Ho Chi Minh City",
//                    "10am","5pm","03/04/2023","05/04/2023",
//                            "Ride roller coasters, ride ferris wheel, try 5D movies, play thrilling games",
//                    "78 Bach Dang Street, Ward 11, Binh Thanh District, Ho Chi Minh City"),
//    };
//
    private List<Destination> destinations;
    Context context;

    private boolean isExpand=true;
    private boolean isFavorite=false;


    private TripLocationAdapter.Callbacks listener;
    public void setListener(TripLocationAdapter.Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToDestination();
    }

    /**
     * Initialize the Destinations of the Adapter.
     * <p>
     * param Destinations String[] containing the data to populate views to be used
     * by RecyclerView.
     */

    public TripLocationAdapter(Context _context, List<Destination> _destinations) {
        this.context = _context;
        this.destinations=_destinations;
    }

//    public DestinationAdapter(Context _context, ArrayList<String> Destinations) {
//        this.context = _context;
//        this.Destinations = Destinations;
//    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //private  final TextView location_count;
        private  final TextView trip_time;
        private  final TextView destination;
        private  final TextView number_comment;
        private  final TextView description;
        private final Button location_name;
        private final Button add_favorite;
        private final Button view_on_map;
        private final Button comment;

        private View expand_comtent;

        private final  ImageView arrow_expand;


        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            //location_count = (TextView) view.findViewById(R.id.location_count);
            trip_time = (TextView) view.findViewById(R.id.location_trip_time);
            destination = (TextView) view.findViewById(R.id.destination);
            number_comment = (TextView) view.findViewById(R.id.number_comment);
            description = (TextView) view.findViewById(R.id.description);
            arrow_expand = (ImageView) view.findViewById(R.id.arrow_expand);
            location_name = (Button) view.findViewById(R.id.location_name);
            add_favorite = (Button) view.findViewById(R.id.add_Favorite);
            view_on_map = (Button) view.findViewById(R.id.view_on_map);
            comment = (Button) view.findViewById(R.id.comment);
            expand_comtent=(View) view.findViewById(R.id.content_location_item);


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
    public TripLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_location_item_post, viewGroup, false);

        return new TripLocationAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TripLocationAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your Destinations at this position and replace the
        // contents of the view with that element
        viewHolder.trip_time.setText(destinations.get(position).getStartTime()+" "+
                                        destinations.get(position).getStartDate()+" - "+
                                        destinations.get(position).getEndTime()+" "+
                                        destinations.get(position).getEndDate());
        viewHolder.description.setText(destinations.get(position).getDescription());
        viewHolder.destination.setText(destinations.get(position).getFormalName());
        viewHolder.location_name.setText(destinations.get(position).getAliasName());


        viewHolder.arrow_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String imageName = context.getResources().getResourceEntryName(((BitmapDrawable) viewHolder.arrow_expand.getDrawable()).getBitmap().getGenerationId());

                if(isExpand){
                    viewHolder.arrow_expand.setImageResource(R.drawable.arrow_right_icon);
                    viewHolder.expand_comtent.setVisibility(View.GONE);
                    isExpand=!isExpand;
                }
                else{
                    viewHolder.arrow_expand.setImageResource(R.drawable.arrow_down_icon);
                    viewHolder.expand_comtent.setVisibility(View.VISIBLE);
                    isExpand=!isExpand;
                }
            }
        });

        viewHolder.add_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite){
                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                    viewHolder.add_favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_icon, 0, 0, 0);
                    isFavorite=!isFavorite;
                }
                else{
                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calendar_icon, 0, 0, 0);
                    //viewHolder.add_Favorite.setDra
                    viewHolder.add_favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_heart_icon, 0, 0, 0);
                    isFavorite=!isFavorite;
                }
            }
        });

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //can swap to another activity using this method
                listener.swapToDestination();
            }
        });

        //Comment
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, CommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("plan id","binding.searchView.getQuery().toString()");

                intent.putExtra("plan comment",bundle);
                context.startActivity(intent);


            }
        });
    }

    // Return the size of your Destinations (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return destinations.size();
    }


}

