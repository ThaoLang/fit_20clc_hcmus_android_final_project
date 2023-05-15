package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private ArrayList<Plan> plans;
    Context context;

    private PostAdapter.Callbacks listener;
    public void setListener(PostAdapter.Callbacks listener) {
        this.listener = listener;
    }

    public interface Callbacks {
        void swapToPost(Plan plan);
    }

    public PostAdapter(Context _context,ArrayList<Plan> _plans) {
        this.context = _context;
        this.plans= new ArrayList<Plan>(_plans);

        Log.e("HOW MANY POST",String.valueOf(this.plans.size()));
    }

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

            name = view.findViewById(R.id.profile_name);
            number_comment = view.findViewById(R.id.number_comment);
            description = view.findViewById(R.id.post_title);
            travel_period = view.findViewById(R.id.trip_time);
            number_like = view.findViewById(R.id.number_like);
            status = view.findViewById(R.id.status);
            avatar = view.findViewById(R.id.profile_image);
            main_image = view.findViewById(R.id.trip_image);

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


    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_post_small_item, viewGroup, false);

        return new PostAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, final int position) {

        //get account
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("account")
                .whereEqualTo("userEmail", plans.get(position).getOwner_email())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                viewHolder.name.setText(String.valueOf(document.get("name")));

                                final long MAX_BYTE = 1024 * 2 * 1024;
                                StorageReference storageReference = DatabaseAccess.getFirebaseStorage().getReference().child(String.valueOf(document.get("avatarUrl")));
                                storageReference.getBytes(MAX_BYTE).addOnSuccessListener(bytes -> {
                                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    viewHolder.avatar.setImageBitmap(image);
                                }).addOnFailureListener(e -> Glide.with(context.getApplicationContext())
                                        .load(String.valueOf(document.get("avatarUrl")))
                                        .into(viewHolder.avatar));
                            }
                        }
                    }
                });
        if (plans.get(position).getImageLink().equals("None")) {
            Random rng = new Random();
            Glide.with(context.getApplicationContext())
                    .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                    .into(viewHolder.main_image);
            //slideModels.add(new SlideModel(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)], ScaleTypes.FIT));
        } else {
            final long MAX_BYTE = 1024 * 2 * 1024;
            StorageReference storageReference = DatabaseAccess.getFirebaseStorage().getReference().child(plans.get(position).getImageLink());
            storageReference.getBytes(MAX_BYTE).addOnSuccessListener(bytes -> {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewHolder.main_image.setImageBitmap(image);
            }).addOnFailureListener(e -> {
                Random rng = new Random();
                Glide.with(context.getApplicationContext())
                        .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                        .into(viewHolder.main_image);
            });
        }
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


        viewHolder.setItemClickListener((view, position1, isLongClick) -> {

            //can swap to another activity using this method
            listener.swapToPost(plans.get(position1));
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }
}
