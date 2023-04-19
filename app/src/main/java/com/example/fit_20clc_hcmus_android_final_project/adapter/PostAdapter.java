package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Chat;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Location;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Post;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Post[] posts={
        new Post(R.drawable.bali,"Lang Thao",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,11),
        new Post(R.drawable.bali,"Khanh Nguyen",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,12),
        new Post(R.drawable.bali,"Hoai Phuong",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,13),
        new Post(R.drawable.bali,"Minh Quang",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,1),
        new Post(R.drawable.bali,"Toan Hao",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,2),
        new Post(R.drawable.bali,"Minh Tri",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,3),
        new Post(R.drawable.bali,"Minh Thong",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000,4)
    };

    private ArrayList<Plan> plans;
    Context context;

    private PostAdapter.Callbacks listener;
    public void setListener(PostAdapter.Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToPost(Plan plan);
    }

    /**
     * Initialize the posts of the Adapter.
     * <p>
     * param posts String[] containing the data to populate views to be used
     * by RecyclerView.
     */

    public PostAdapter(Context _context,ArrayList<Plan> _plans) {
        this.context = _context;
        this.plans= new ArrayList<Plan>(_plans);

        Log.e("HOW MANY POST",String.valueOf(this.plans.size()));
    }

//    public PostAdapter(Context _context, ArrayList<String> posts) {
//        this.context = _context;
//        this.posts = posts;
//    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView avatar;
        private final ImageView main_image;
        private  final TextView name;
        private final TextView number_like;
        private final TextView description;
        private final TextView travel_period;
        private final TextView number_comment;
        private final TextView status;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = (TextView) view.findViewById(R.id.profile_name);
            number_comment = (TextView) view.findViewById(R.id.number_comment);
            description = (TextView) view.findViewById(R.id.post_title);
            travel_period = (TextView) view.findViewById(R.id.trip_time);
            number_like = (TextView) view.findViewById(R.id.number_like);
            status = (TextView) view.findViewById(R.id.status);
            avatar = (ImageView) view.findViewById(R.id.profile_image);
            main_image = (ImageView) view.findViewById(R.id.trip_image);

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
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_post_small_item, viewGroup, false);

        return new PostAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your posts at this position and replace the
        // contents of the view with that element
        String account_name;
        String account_avatar="";
        //get account
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("account")
                .whereEqualTo("userEmail", plans.get(position).getOwner_email())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Xử lí khi có kết quả trả về
                                    viewHolder.name.setText(document.get("name").toString());
                                    Glide.with(context.getApplicationContext())
                                            .load(document.get("avatarUrl").toString())
                                            .into(viewHolder.avatar);
                                }
                            } else {

                            }

                        } else {

                        }

                    }
                });
//
        viewHolder.number_like.setText(String.valueOf(plans.get(position).getListOfLike().size()));
        viewHolder.number_comment.setText(String.valueOf(plans.get(position).getListOfComments().size()));
        viewHolder.description.setText(plans.get(position).getName());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate;
        Date endDate;
        try {
            startDate = format.parse(plans.get(position).getDeparture_date());
            endDate = format.parse(plans.get(position).getReturn_date());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long timeDiff = Math.abs(endDate.getTime() - startDate.getTime());
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        viewHolder.travel_period.setText(String.valueOf(daysDiff)+" days");
        viewHolder.main_image.setImageResource(R.drawable.bali);
        String status_text=plans.get(position).getStatus();
        viewHolder.status.setText(status_text);

        if(status_text.equals("Ongoing")){
            viewHolder.status.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ongoing_icon, context.getTheme()), null, null, null);
            viewHolder.status.setTextColor(context.getColor(R.color.CustomColor10));
        }
        else if(status_text.equals("Finished")){
            viewHolder.status.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.done_tick_icon, context.getTheme()), null, null, null);
            viewHolder.status.setTextColor(R.color.md_theme_light_outline);
        }


        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                //can swap to another activity using this method
                listener.swapToPost(plans.get(position));
            }
        });
    }

    // Return the size of your posts (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return plans.size();
    }
}
