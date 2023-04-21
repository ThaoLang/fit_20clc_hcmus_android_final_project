package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Comment;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
   // private Comment[] comments_1={
//            new Comment("None","None", Comment.commentType.Plan,"None","amazing","None"),
//            new Comment("None","None",Comment.commentType.Plan,"None","interesting","None"),
//            new Comment("None","None",Comment.commentType.Plan,"None","bad trip","None"),
//            new Comment("None","None",Comment.commentType.Plan,"None","rich kid","None"),
//            };

    private ArrayList<Comment> comments;
    Context context;

    private CommentAdapter.Callbacks listener;
    public void setListener(CommentAdapter.Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToPost();
    }

    /**
     * Initialize the posts of the Adapter.
     * <p>
     * param posts String[] containing the data to populate views to be used
     * by RecyclerView.
     */

    public CommentAdapter(Context _context,ArrayList<Comment> _comments) {
        this.context = _context;
        this.comments=_comments;
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
        private final ImageView image_comment;
        private final ImageView profile_image;
        private  final TextView name_account;
        private final TextView text_comment;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name_account = (TextView) view.findViewById(R.id.profile_name);
            text_comment = (TextView) view.findViewById(R.id.text_comment);
            image_comment = (ImageView) view.findViewById(R.id.image_comment);
            profile_image = (ImageView) view.findViewById(R.id.profile_image);

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
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_comment_item, viewGroup, false);

        return new CommentAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder viewHolder, final int position) {
        String account_name;
        String account_avatar="";
        //get account
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("account")
                .whereEqualTo("userEmail", comments.get(position).getAccountEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Xử lí khi có kết quả trả về
                                    viewHolder.name_account.setText(document.get("name").toString());
                                    Glide.with(context.getApplicationContext())
                                            .load(document.get("avatarUrl").toString())
                                            .into(viewHolder.profile_image);
                                }
                            } else {
                            }

                        } else {
                        }
                    }
                });
        if (comments.get(position).getImageLink().equals("None")) {
            viewHolder.image_comment.setVisibility(View.GONE);
        } else {
            final long MAX_BYTE = 1024 * 2 * 1024*4;
            StorageReference storageReference = DatabaseAccess.getFirebaseStorage().getReference().child(comments.get(position).getImageLink());
            storageReference.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewHolder.image_comment.setImageBitmap(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Random rng = new Random();
                    Glide.with(context.getApplicationContext())
                            .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                            .into(viewHolder.image_comment);
                }
            });
        }

        // Get element from your posts at this position and replace the
        // contents of the view with that element

        //viewHolder.name_account.setText(comments[position].get());
        if (comments.get(position).getText_comment().length()==0)
        {
            viewHolder.text_comment.setVisibility(View.GONE);
        }
        else{
            viewHolder.text_comment.setVisibility(View.VISIBLE);
            viewHolder.text_comment.setText(comments.get(position).getText_comment());
        }
        Log.e("COMMENT TEST",comments.get(position).getText_comment());
        //viewHolder.profile_image.setImageResource(comments[position].getAvatar_url());
        //viewHolder.image_comment.setImageResource(comments[position].getMain_image());

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //can swap to another activity using this method
                listener.swapToPost();
            }
        });
    }

    // Return the size of your posts (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return comments.size();
    }
}
