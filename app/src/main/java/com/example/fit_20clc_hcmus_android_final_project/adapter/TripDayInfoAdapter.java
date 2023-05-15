//package com.example.fit_20clc_hcmus_android_final_project.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
//import com.example.fit_20clc_hcmus_android_final_project.R;
//import com.example.fit_20clc_hcmus_android_final_project.data_struct.Post;
//import com.example.fit_20clc_hcmus_android_final_project.databinding.CustomItemTripDayBinding;
//
//
//public class TripDayInfoAdapter extends RecyclerView.Adapter<com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter.ViewHolder>{
//    private Post[] posts={
//            new Post(R.drawable.bali,"Lang Thao","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000),
//            new Post(R.drawable.bali,"Khanh Nguyen","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000),
//            new Post(R.drawable.bali,"Hoai Phuong","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000),
//            new Post(R.drawable.bali,"Minh Quang","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000),
//            new Post(R.drawable.bali,"Toan Hao","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000),
//            new Post(R.drawable.bali,"Minh Tri","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000),
//            new Post(R.drawable.bali,"Minh Thong","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000)
//    };
//    Context context;
//
//    private com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter.Callbacks listener;
//    public void setListener(com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter.Callbacks listener) {
//        this.listener = listener;
//    }
//    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
//    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
//    public interface Callbacks {
//        void swapToPost();
//    }
//
//    /**
//     * Initialize the posts of the Adapter.
//     * <p>
//     * param posts String[] containing the data to populate views to be used
//     * by RecyclerView.
//     */
//
//    public TripDayInfoAdapter(Context _context) {
//        this.context = _context;
//    }
//
////    public PostAdapter(Context _context, ArrayList<String> posts) {
////        this.context = _context;
////        this.posts = posts;
////    }
//
//    /**
//     * Provide a reference to the type of views that you are using
//     * (custom ViewHolder).
//     */
//
//    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        private final ImageView avatar;
//        private final ImageView main_image;
//        private  final TextView name;
//        private final TextView date;
//        private final TextView description;
//        private final TextView travel_period;
//        private final TextView cost;
//
//        private ItemClickListener itemClickListener;
//
//        public ViewHolder(View view) {
//            super(view);
//            // Define click listener for the ViewHolder's View
//
//            name = (TextView) view.findViewById(R.id.profile_name);
//            date = (TextView) view.findViewById(R.id.profile_created_date);
//            description = (TextView) view.findViewById(R.id.post_title);
//            travel_period = (TextView) view.findViewById(R.id.trip_time);
//            cost = (TextView) view.findViewById(R.id.trip_cost);
//            avatar = (ImageView) view.findViewById(R.id.profile_image);
//            main_image = (ImageView) view.findViewById(R.id.trip_image);
//
//            view.setOnClickListener(this);
//        }
//
//        public void setItemClickListener(ItemClickListener itemClickListener)
//        {
//            this.itemClickListener = itemClickListener;
//        }
//
//        @Override
//        public void onClick(View v) {
//            itemClickListener.onClick(v,getAdapterPosition(),false);
//        }
//
//    }
//
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        // Create a new view, which defines the UI of the list item
//        View view = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.fragment_post_small_item, viewGroup, false);
//
//        return new com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter.ViewHolder(view);
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter.ViewHolder viewHolder, final int position) {
//
//        // Get element from your posts at this position and replace the
//        // contents of the view with that element
//        viewHolder.name.setText(posts[position].getAccount_name());
//        viewHolder.date.setText(posts[position].getCreate_date());
//        viewHolder.description.setText(posts[position].getDescription());
//        viewHolder.travel_period.setText(String.valueOf(posts[position].getTravel_period())+" days");
//        viewHolder.cost.setText(String.valueOf(posts[position].getCost())+"vnd");
//        viewHolder.avatar.setImageResource(posts[position].getAvatar_url());
//        viewHolder.main_image.setImageResource(posts[position].getMain_image());
//
//        viewHolder.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onClick(View view, int position, boolean isLongClick) {
//                //can swap to another activity using this method
//                listener.swapToPost();
//            }
//        });
//    }
//
//    // Return the size of your posts (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return posts.length;
//    }
//}
//
