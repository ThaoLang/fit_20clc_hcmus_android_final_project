package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.adapter.Detailed_Plan_Destination_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DetailedPlan extends AppCompatActivity
{
    private MaterialToolbar toolbar;
    private RecyclerView destinations, travelers;
    private MaterialTextView no_plan, no_invited_friends;
    private FloatingActionButton addButton;
    private Context context;
    private MainActivity main;

    private List<Destination> destinationList;

    private static final String INIT_PARAM = "INIT_PARAM";
    private String InitParam = null;

    private String specPlanId;
    private Plan specPlan;

    private static int selectedDestinationPosition = -1;
    private final static String PERMISSION_CODE = "X31aLjuNNm4j1d";

    public final static String SPEC_DESTINATION = "SPEC_DESTINATION";

    public final static String DETAILED_PLAN_ID = "DETAILED_PLAN_ID";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_plan);
        context = this;

        toolbar = findViewById(R.id.detailed_plan_toolbar);
        destinations = findViewById(R.id.detailed_plan_recyclerview_destinations);
        travelers = findViewById(R.id.detailed_plan_recyclerview_travelers);
        addButton = findViewById(R.id.detailed_plans_fab);

        no_plan = findViewById(R.id.detailed_plan_no_plan);
        no_invited_friends = findViewById(R.id.detailed_plan_no_invited_friends);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.detailed_plan_toolbar_menu_add_friend)
                {
                    Log.i("<<ADD FIREND>>", "add new friend");
                }
                else if(item.getItemId() == R.id.detailed_plan_toolbar_menu_more)
                {

                }

                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddDestination.class);
                Bundle bundle = new Bundle();
                bundle.putString(AddDestination.SETTING_MODE, AddDestination.ADD_DESTINATION);
                bundle.putString("PLAN_ID", specPlanId);
                intent.putExtra(DetailedPlan.SPEC_DESTINATION, bundle);
                launcher.launch(intent);
            }
        });

        Intent planIntent = getIntent();
        specPlanId = planIntent.getStringExtra(DETAILED_PLAN_ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        specPlan = DatabaseAccess.getPlanById(specPlanId);
        destinations.setVisibility(View.VISIBLE);
        travelers.setVisibility(View.VISIBLE);
        no_plan.setVisibility(View.GONE);
        no_invited_friends.setVisibility(View.GONE);
        if(specPlan == null)
        {
            destinations.setVisibility(View.GONE);
            no_plan.setVisibility(View.VISIBLE);
            no_invited_friends.setVisibility(View.VISIBLE);
        }
        else
        {
            toolbar.setTitle(specPlan.getName());
            destinationList = specPlan.getListOfLocations();
            Detailed_Plan_Destination_Adapter adapter = new Detailed_Plan_Destination_Adapter(context, destinationList, launcher, specPlan.getPlanId());
            destinations.setAdapter(adapter);
        }
    }

    private ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        //receive the result from AddDestination activity
                        Log.i("<<Returned result>>", "NEW DESTINATION");
                        Intent intent = result.getData();
                        String mode = intent.getStringExtra(AddDestination.SETTING_MODE);
                        byte[] bytesArray = intent.getByteArrayExtra(AddDestination.RETURN_RESULT);
                        Destination destination = Destination.toObject(bytesArray);
                        if(mode.equals(AddDestination.VIEW_DESTINATION))
                        {
                            return;
                        }
                        else if(mode.equals(AddDestination.EDIT_DESTINATION))
                        {

                        }
                        else //mode == AddDestination.ADD_DESTINATION
                        {
                            Runnable successfulTask = new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Add new destination successfully...", Toast.LENGTH_SHORT).show();
                                    onStart();
                                }
                            };

                            Runnable failedTask = new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Add new destination failed", Toast.LENGTH_SHORT).show();
                                }
                            };
                            DatabaseAccess.addNewDestinationTo(destination, specPlanId, successfulTask, failedTask);
                        }
                    }
                }
            });

    @Override
    public void onPause()
    {
        super.onPause();

    }
}
