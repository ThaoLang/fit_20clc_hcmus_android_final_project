package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.fit_20clc_hcmus_android_final_project.BuildConfig;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.FavoriteLocation;
import com.example.fit_20clc_hcmus_android_final_project.HomePage;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.LocationInfo;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.Search;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FavoriteLocationAdapter extends RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolder>{
//    private FavoriteLocation[] favoriteLocations={
//            new FavoriteLocation(R.drawable.bali,"Bali"),
//            new FavoriteLocation(R.drawable.bali,"Bali"),
//            new FavoriteLocation(R.drawable.bali,"Bali"),
//            new FavoriteLocation(R.drawable.bali,"Bali"),
//            new FavoriteLocation(R.drawable.bali,"Bali"),
//            new FavoriteLocation(R.drawable.bali,"Bali")
//            };

    private List<String> favorite_locations;
    Context context;

    private FavoriteLocationAdapter.Callbacks listener;
    public void setListener(FavoriteLocationAdapter.Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToLocationInfo(String locationName);
    }

    /**
     * Initialize the posts of the Adapter.
     * <p>
     * param posts String[] containing the data to populate views to be used
     * by RecyclerView.
     */

    public FavoriteLocationAdapter(Context _context,List<String> _favorite_locations) {
        this.context = _context;
        this.favorite_locations=new ArrayList<String>(_favorite_locations);
        Log.e("FAVORITE SIZE", String.valueOf(this.favorite_locations.size()));
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

            name = (TextView) view.findViewById(R.id.favorite_location_name);
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
//        if (favorite_locations==null){
//            favorite_locations= new List<String>
//        }
        //viewHolder.name.setText(favoriteLocations[position].getName());
//        viewHolder.name.setText(favorite_locations.get(position));
//        StreetViewImageLoader imageLoader = new StreetViewImageLoader(viewHolder.main_image);
//        Geocoder geocoder = new Geocoder(context);
//        String latitude="";
//        String longitude="";
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(favorite_locations.get(position), 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address addressResult = addresses.get(0);
//                latitude = String.valueOf(addressResult.getLatitude());
//                longitude = String.valueOf(addressResult.getLongitude());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        imageLoader.execute(latitude+","+longitude, "640x640", BuildConfig.API_KEY);


        //viewHolder.main_image.setImageResource(favoriteLocations[position].getImage());

        viewHolder.name.setText(favorite_locations.get(position));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location")
                .whereEqualTo("formalName", favorite_locations.get(position))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Xử lí khi có kết quả trả về
                                    Log.e("HIHIHI",document.get("images").toString());

                                    List<String> imagesList = (List<String>) document.get("images");
                                    Glide.with(context.getApplicationContext())
                                            .load(imagesList.get(0))
                                            .into(viewHolder.main_image);

                                }
                            } else {
                                // Xử lí khi không có kết quả trả về
                                Random rng=new Random();
                                Log.e("ERROR GLIDE","YES");
                                Glide.with(context.getApplicationContext())
                                        .load(DatabaseAccess.default_image_url[0])
                                        .into(viewHolder.main_image);

                            }

                        } else {
                            // Xử lí khi có lỗi xảy ra
                            Random rng=new Random();
                            Log.e("ERROR GLIDE","NO");
                            Glide.with(context.getApplicationContext())
                                    .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                                    .into(viewHolder.main_image);
                        }

                    }
                });

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                listener.swapToLocationInfo(favorite_locations.get(position));
            }
        });
    }

    // Return the size of your posts (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return favorite_locations.size();
    }

    private static class StreetViewImageLoader extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView; // ImageView để hiển thị ảnh

        public StreetViewImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String location = params[0]; // Địa điểm dưới dạng "latitude,longitude"
            String size = params[1]; // Kích thước ảnh, ví dụ "640x640"
            String key = params[2]; // Mã API của Google Maps

            // Xây dựng URL cho Google Street View Static API
            String urlString = "https://maps.googleapis.com/maps/api/streetview?location="
                    + location + "&size=" + size + "&key=" + key;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                //Log.e(TAG, "Lỗi khi tải ảnh từ Street View", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Sử dụng ảnh đã lấy (result) trong giao diện người dùng
            // Ví dụ, đặt ảnh vào ImageView
            if (result != null) {
                // Đặt bitmap vào ImageView
                imageView.setImageBitmap(result);
            } else {
                // Xử lý trường hợp không tải được ảnh
            }
        }
    }
}

