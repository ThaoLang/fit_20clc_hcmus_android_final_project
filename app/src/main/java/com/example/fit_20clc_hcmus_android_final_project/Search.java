package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivitySearchBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.RegistrationBinding;
import com.google.type.LatLng;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.stream.Collectors;

public class Search extends AppCompatActivity {

    private ActivitySearchBinding binding;

    private String _mode;

    public final static String SEARCH_MODE = "MODE";

    public final static String SEARCH_LOCATION_INFO = "SEARCH_LOCATION_INFO";
    public final static String SEARCH_RETURN_FORMAL_NAME = "GET_FORMAL_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding= ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent requestIntent = getIntent();
        if(requestIntent != null)
        {
            _mode = requestIntent.getStringExtra(SEARCH_MODE);
            if(_mode == null)
            {
                throw new MissingFormatArgumentException("Missing _mode to start search activity. Put mode into Intent object when start this activity, intent.putExtras(Search.SEARCH_MODE, Search.<MODE>)");
            }
        }

        if(binding.suggestSearch.getVisibility() == View.VISIBLE)
        {
            binding.suggestSearch.setVisibility(View.GONE);
        }

        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //binding.searchView.setBackgroundColor(Color.TRANSPARENT);
                if(binding.suggestSearch.getVisibility() == View.VISIBLE)
                {
                    binding.suggestSearch.setVisibility(View.GONE);
                }
                return false;
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                String location = binding.searchView.getQuery().toString();
//                List<Address> addressesList = null;
//                System.out.println(query);
//                if(!location.equals(""))
//                {
//                    Geocoder geocoder = new Geocoder((Search.this));
//                    try {
//                        addressesList = geocoder.getFromLocationName(location,1);
//
//                        if (addressesList.size()>0)
//                        {
//                            mMap.clear();
//                            Address address = addressesList.get(0);
//                            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
//                            List<String> suggest = addressesList.stream().map((address1) -> address1.getAddressLine(0)).collect(Collectors.toList());
//                            System.out.println(address.getAddressLine(0));
//                            System.out.println(suggest);
//                            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        throw new RuntimeException(e);
//                    }
//
//                }
//                if(suggest_search.getVisibility() == View.VISIBLE)
//                {
//                    suggest_search.setVisibility(View.GONE);
//                }

                Intent intent;
                if(_mode.equals(SEARCH_LOCATION_INFO))
                {
                    intent= new Intent(Search.this, LocationInfo.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("location address",binding.searchView.getQuery().toString());

                    intent.putExtra("location search",bundle);
                    startActivity(intent);
                }
                else if(_mode.equals(SEARCH_RETURN_FORMAL_NAME))
                {
                    Log.e("SEARCH","RETURN FORMAL NAME");
                    intent= new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("location address",binding.searchView.getQuery().toString());

                    intent.putExtra("location search",bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(binding.suggestSearch.getVisibility() != View.VISIBLE)
                {
                    binding.suggestSearch.setVisibility(View.VISIBLE);
                }
                String location = binding.searchView.getQuery().toString();
                List<Address> addressesList = null;

                if(!location.equals("")) {
                    Geocoder geocoder = new Geocoder((Search.this));
                    try {
                        addressesList = geocoder.getFromLocationName(location, 10);

                        if (addressesList.size() > 0) {
                            List<String> suggest = addressesList.stream().map((address1) -> address1.getAddressLine(0)).collect(Collectors.toList());
                            String[] strings = suggest.stream().toArray(String[]::new);
                            Arrays.stream(strings).forEach(System.out::println);
                            System.out.println(suggest);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Search.this,R.layout.custom_suggest_line,strings);
                            binding.suggestSearch.setAdapter(adapter);
                            binding.suggestSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    binding.searchView.setQuery(strings[position],true);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                else{
                    if(binding.suggestSearch.getVisibility() == View.VISIBLE)
                    {
                        binding.suggestSearch.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }

    private void fileList(String newText) {
    }
}
