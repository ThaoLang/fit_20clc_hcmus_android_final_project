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
    private List<String> favorite_locations;
    Context context;

    private FavoriteLocationAdapter.Callbacks listener;
    public void setListener(FavoriteLocationAdapter.Callbacks listener) {
        this.listener = listener;
    }

    public interface Callbacks {
        void swapToLocationInfo(String locationName);
    }

    public FavoriteLocationAdapter(Context _context,List<String> _favorite_locations) {
        this.context = _context;
        this.favorite_locations=new ArrayList<String>(_favorite_locations);
        Log.e("FAVORITE SIZE", String.valueOf(this.favorite_locations.size()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView main_image;
        private  final TextView name;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.favorite_location_name);
            main_image = view.findViewById(R.id.location_image);

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
    public FavoriteLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_favorite_location_item, viewGroup, false);

        return new FavoriteLocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteLocationAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.name.setText(favorite_locations.get(position));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location")
                .whereEqualTo("formalName", favorite_locations.get(position))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Log.e("HIHIHI", String.valueOf(document.get("images")));

                                List<String> imagesList = (List<String>) document.get("images");
                                Glide.with(context.getApplicationContext())
                                        .load(imagesList.get(0))
                                        .into(viewHolder.main_image);

                            }
                        } else {
                            Random rng=new Random();
                            Log.e("ERROR GLIDE","YES");
                            Glide.with(context.getApplicationContext())
                                    .load(DatabaseAccess.default_image_url[0])
                                    .into(viewHolder.main_image);

                        }

                    } else {
                        Random rng=new Random();
                        Log.e("ERROR GLIDE","NO");
                        Glide.with(context.getApplicationContext())
                                .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                                .into(viewHolder.main_image);
                    }

                });

        viewHolder.setItemClickListener((view, position1, isLongClick) -> listener.swapToLocationInfo(favorite_locations.get(position1)));
    }

    @Override
    public int getItemCount() {
        return favorite_locations.size();
    }

    private static class StreetViewImageLoader extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        public StreetViewImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String location = params[0]; // "latitude,longitude"
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

