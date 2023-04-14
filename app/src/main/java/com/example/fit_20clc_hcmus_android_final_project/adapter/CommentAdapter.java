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
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Comment;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Post;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Comment[] comments={
            new Comment("None","None", Comment.commentType.Plan,"None","amazing","None"),
            new Comment("None","None",Comment.commentType.Plan,"None","interesting","None"),
            new Comment("None","None",Comment.commentType.Plan,"None","bad trip","None"),
            new Comment("None","None",Comment.commentType.Plan,"None","rich kid","None"),
            };
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

    public CommentAdapter(Context _context) {
        this.context = _context;
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

        // Get element from your posts at this position and replace the
        // contents of the view with that element

        //viewHolder.name_account.setText(comments[position].get());
        viewHolder.text_comment.setText(comments[position].getText_comment());
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
        return comments.length;
    }
}
