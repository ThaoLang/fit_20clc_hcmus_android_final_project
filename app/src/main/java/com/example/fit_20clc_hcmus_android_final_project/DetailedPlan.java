package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.adapter.Detailed_Plan_Destination_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.FriendAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailedPlan extends AppCompatActivity
{
    private MaterialToolbar toolbar;
    private RecyclerView destinations, travelers;
    private LinearLayoutManager travelerLinearLayoutManager;
    private MaterialTextView no_plan, no_invited_friends;
    private FloatingActionButton addButton;
    private Context context;
    private MainActivity main;
    private FirebaseFirestore db;

    private List<Destination> destinationList;

    private static final String INIT_PARAM = "INIT_PARAM";
    private String InitParam = null;

    private String specPlanId;
    private Plan specPlan;

    private String mode;

    private boolean isEditable;

    private static int selectedDestinationPosition = -1;
    private final static String PERMISSION_CODE = "X31aLjuNNm4j1d";

    public final static String SPEC_DESTINATION = "SPEC_DESTINATION";
    public final static String SPEC_PLAN = "SPEC_PLAN";

    public final static String DETAILED_PLAN_ID = "DETAILED_PLAN_ID";

    private final static String SETTING_MODE = "SETTING_MODE";

    private boolean isEditing;
    private int currentDestinationIndex=0;

    private boolean loading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_plan);
        context = this;

        isEditable = false;
        isEditing = false;
        toolbar = findViewById(R.id.detailed_plan_toolbar);
        destinations = findViewById(R.id.detailed_plan_recyclerview_destinations);
        travelers = findViewById(R.id.detailed_plan_recyclerview_travelers);
        addButton = findViewById(R.id.detailed_plans_fab);

        no_plan = findViewById(R.id.detailed_plan_no_plan);
        no_invited_friends = findViewById(R.id.detailed_plan_no_invited_friends);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEditing)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailedPlan.this);
                    builder.setMessage("Do you want to continue?")
                            .setTitle("Editing is interrupted");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isEditing = false;
                            finish();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    finish();
                }
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.detailed_plan_toolbar_menu_add_friend)
                {
                    Log.i("<<Invite FRIEND>>", "invite new friend");
                    Intent intent = new Intent(DetailedPlan.this, InviteFriends.class);
                    intent.putExtra("PLAN_ID", specPlanId);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.detailed_plan_toolbar_menu_chat)
                {
                    Log.i("<<OPEN CHAT>>", "talk to friends");

                    Intent intent = new Intent(context, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("PlanId", specPlanId);
                    intent.putExtra("CHAT", bundle);

                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.detailed_plan_toolbar_menu_more)
                {
                    PopupMenu popupMenu = new PopupMenu(DetailedPlan.this, toolbar.getChildAt(2));
                    popupMenu.inflate(R.menu.detailed_plan_toolbar_popup_menu);
                    Menu menu = popupMenu.getMenu();

                    if(isEditable == false)
                    {
                        menu.getItem(0).setVisible(false);
                    }
                    if(specPlan.getOwner_email().equals(DatabaseAccess.getMainUserInfo().getUserEmail())==true)
                    {
                        menu.getItem(0).setVisible(true);
                        menu.getItem(1).setVisible(false);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getItemId() == R.id.detailed_plan_toolbar_popup_menu_edit)
                            {
                                Intent intent = new Intent(DetailedPlan.this, CreatePlan.class);
                                intent.putExtra("SETTING_MODE", TripsPage.EDIT_PLAN_MODE);
                                intent.putExtra(DetailedPlan.DETAILED_PLAN_ID, specPlanId);
                                launcher.launch(intent);
                            }
                            else if(menuItem.getItemId() == R.id.detailed_plan_toolbar_popup_menu_leave)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedPlan.this);
                                builder.setMessage("Do you want to leave the trip")
                                        .setTitle("Confirm to leave the trip");
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                });
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Runnable successfulTask = new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(DetailedPlan.this, "Leave trip successfully!", Toast.LENGTH_LONG).show();
                                                onStart();
                                            }
                                        };
                                        Runnable failureTask = new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(DetailedPlan.this, "Leave trip failed", Toast.LENGTH_LONG).show();
                                            }
                                        };
                                        DatabaseAccess.leaveATrip(specPlanId, successfulTask, failureTask);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
                else if(item.getItemId() == R.id.detailed_plan_toolbar_confirm_changes)
                {
                    //TODO: update the list of destination
                    Runnable successfulTask = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Update destinations successfully!", Toast.LENGTH_LONG).show();
                            isEditing = false;
                            onStart();
                        }
                    };

                    Runnable failureTask = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Update destinations failed!", Toast.LENGTH_LONG).show();
                            isEditing = false;
                            onStart();
                        }
                    };

                    DatabaseAccess.updateDestinationListTo(destinationList, specPlanId, successfulTask, failureTask);
                }
                else if(item.getItemId() == R.id.detailed_plan_toolbar_refresh)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailedPlan.this);
                    builder.setMessage("Do you want to refresh all changes? This process can't be undone");
                    builder.setTitle("Refresh changes");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            specPlan = DatabaseAccess.getClonePlanById(specPlanId);
                            onStart();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            return;
                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("REMOTE"))
                {
                    //it should be a transaction
                    addButton.setImageResource(R.drawable.cheer_48px);
                    DatabaseAccess.getMainUserInfo().addNewPlanId(specPlanId);
                    DatabaseAccess.updateUserInfo_In_Database(DatabaseAccess.getMainUserInfo(), null, null);
                    DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_PLANS_COLLECTION).document(specPlanId)
                            .update("passengers", FieldValue.arrayUnion(DatabaseAccess.getMainUserInfo().getUserEmail()));

                    return;

                }
                else if (specPlan.getStatus().equals(TripsPage.UPCOMING)){
                    Intent intent = new Intent(DetailedPlan.this, AddDestination.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(SETTING_MODE, AddDestination.ADD_DESTINATION);
                    bundle.putString("PLAN_ID", specPlanId);
                    intent.putExtra(DetailedPlan.SPEC_DESTINATION, bundle);
                    launcher.launch(intent);
                }
                else if (specPlan.getStatus().equals((TripsPage.ONGOING))){
                    Intent intent = new Intent(DetailedPlan.this, ViewProgressTripMap.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("PLAN_ID", specPlanId);
                    bundle.putSerializable("All destinations", (Serializable) specPlan.getListOfLocations());
                    intent.putExtras(bundle);
                    startActivity(intent);

//                    bundle.putString(SETTING_MODE, AddDestination.ADD_DESTINATION);
//                    bundle.putString("PLAN_ID", specPlanId);
//                    intent.putExtra(DetailedPlan.SPEC_DESTINATION, bundle);
//                    launcher.launch(intent);



                }

            }
        });

        Intent planIntent = getIntent();
        specPlanId = planIntent.getStringExtra(DETAILED_PLAN_ID);
        mode = planIntent.getStringExtra("MODE");
        System.out.println("mode " + mode);
        if(mode.equals("REMOTE"))
        {
            loading = true;
            DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_PLANS_COLLECTION)
                    .document(specPlanId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            specPlan = documentSnapshot.toObject(Plan.class);
                            loading = false;
                            onStart();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            specPlan = null;
                            loading = false;
                        }
                    });
//            while(loading == true)
//            {
//                System.out.println("Waiting for loading remote plan");
//            }
        }
        else
        {
            specPlan = DatabaseAccess.getClonePlanById(specPlanId);
            destinationList = specPlan.getListOfLocations();
        }
        if(specPlan != null)
        {
            if(specPlan.getSet_of_editors().contains(DatabaseAccess.getMainUserInfo().getUserEmail()) ||
                    specPlan.getOwner_email().equals(DatabaseAccess.getMainUserInfo().getUserEmail()))
            {
                isEditable = true;
            }

            if ((specPlan.getStatus().equals(TripsPage.HISTORY)) || (destinationList.size()==0 && specPlan.getStatus().equals(TripsPage.ONGOING))){
                addButton.setVisibility(View.GONE);
            }
            else if (specPlan.getStatus().equals(TripsPage.ONGOING)){
                addButton.setImageResource(R.drawable.map_icon);
            }
        }

//        else{
//            addButton.setImageResource(R.drawable.map_icon);
//        }

        OnBackPressedCallback overrideBackPressCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                if(isEditing)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailedPlan.this);
                    builder.setMessage("Do you want to continue?")
                            .setTitle("Editing is interrupted");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isEditing = false;
                            finish();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, overrideBackPressCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(specPlan == null)
        {
            return;
        }
        destinations.setVisibility(View.VISIBLE);
        travelers.setVisibility(View.VISIBLE);
        no_plan.setVisibility(View.INVISIBLE);
        no_invited_friends.setVisibility(View.INVISIBLE);
        toolbar.getMenu().getItem(0).setVisible(true);
        toolbar.getMenu().getItem(1).setVisible(true);
        toolbar.getMenu().getItem(2).setVisible(true);
        toolbar.getMenu().getItem(3).setVisible(false);
        toolbar.getMenu().getItem(4).setVisible(false);
        if(isEditable == false)
        {
            toolbar.getMenu().getItem(1).setVisible(false);
        }
        if(specPlan == null)
        {
            destinations.setVisibility(View.INVISIBLE);
            no_plan.setVisibility(View.VISIBLE);
            no_invited_friends.setVisibility(View.VISIBLE);
        }
        else
        {
            toolbar.setTitle(specPlan.getName());
            destinationList = specPlan.getListOfLocations();
            Detailed_Plan_Destination_Adapter adapter = new Detailed_Plan_Destination_Adapter(context, destinationList, launcher, specPlan.getPlanId(),false);
            destinations.setAdapter(adapter);

            //binding.detailedPlanRecyclerviewDestinations.getI
            //get current destination
            CollectionReference roomCol=DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_ROOM_COLLECTION);
            DocumentReference planDoc=roomCol.document(specPlan.getPlanId());
            CollectionReference passengerCol=planDoc.collection(DatabaseAccess.ACCESS_SUB_PASSENGER_COLLECTION);
            passengerCol.whereEqualTo("email",DatabaseAccess.getMainUserInfo().getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    currentDestinationIndex = Integer.parseInt(document.get("currentDestination").toString());
                                }

                                Log.e("Last POST",String.valueOf(currentDestinationIndex));
                                Log.e("HAHA POST",specPlan.getPlanId());
                                Log.e("HIHI POST",DatabaseAccess.getMainUserInfo().getUserEmail());
                                for (int i=0;i<destinationList.size();i++){
                                    destinations.smoothScrollToPosition(i);
                                    View itemView = destinations.getChildAt(i);
                                    //RecyclerView.ViewHolder viewHolder = destinations.findViewHolderForAdapterPosition(i);
                                    Log.e("FINAL DONE",String.valueOf(currentDestinationIndex));
//                                    if (i <currentDestinationIndex && viewHolder.itemView!=null) {
//                                        viewHolder.itemView.setBackgroundColor(Color.parseColor("#999494"));
//                                    }
//                                    else if(i==currentDestinationIndex && viewHolder.itemView!=null) {
//                                        //itemView.requestFocus();
//                                        //destinations.smoothScrollToPosition(currentDestinationIndex);
//                                        viewHolder.itemView.setBackgroundColor(Color.parseColor("#5EAC8B"));
//                                    }

                                    if (i <currentDestinationIndex && itemView!=null) {
                                        itemView.setBackgroundColor(Color.parseColor("#999494"));
                                    }
                                    else if(i==currentDestinationIndex && itemView!=null) {
                                        //itemView.requestFocus();
                                        //destinations.smoothScrollToPosition(currentDestinationIndex);
                                        itemView.setBackgroundColor(Color.parseColor("#5EAC8B"));
                                    }

                                }


                            }
                        }
                        else
                        {
                            // no notification
                        }
                    }
                });
                                           }
        Log.e("TOI LA AI",String.valueOf(currentDestinationIndex));
//        View itemView = destinations.getChildAt(currentDestinationIndex);
//
//
//        if (itemView != null) {
//            itemView.requestFocus();
//            destinations.smoothScrollToPosition(currentDestinationIndex);
//            itemView.setBackgroundColor(Color.parseColor("#0F7764"));
//        }

            //traveler
            db = FirebaseFirestore.getInstance();
            int number_member = specPlan.getPassengers().size();

            if(number_member>0) {
                travelerLinearLayoutManager = new LinearLayoutManager(DetailedPlan.this, RecyclerView.VERTICAL, false);
                travelerLinearLayoutManager.setStackFromEnd(true);
                travelers.setLayoutManager(travelerLinearLayoutManager);

                ArrayList<User> members= new ArrayList<User>();
                for (int i = 0; i < number_member; i++) {
                    db.collection("account")
                            .whereEqualTo("userEmail", specPlan.getPassengers().get(i))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                User member=document.toObject(User.class);
                                                members.add(member);
                                            }
                                        } else {

                                        }

                                    } else {

                                    }
                                    travelers.setAdapter(new FriendAdapter(DetailedPlan.this, members));
                                    travelers.smoothScrollToPosition(0);
                                }
                            });
                }

            }

        if(isEditing == true) //set toolbar to wait for confirm changes
        {
            toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.getMenu().getItem(1).setVisible(false);
            toolbar.getMenu().getItem(2).setVisible(false);
            toolbar.getMenu().getItem(3).setVisible(true);
            toolbar.getMenu().getItem(4).setVisible(true);
        }
    }

    public ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        //receive the result from AddDestination activity
                        Log.i("<<Returned result>>", "NEW DESTINATION");
                        Intent intent = result.getData();
                        String identify = intent.getStringExtra("IDENTIFY");
                        Bundle bundle = intent.getBundleExtra("RETURN_BUNDLE");
                        if (identify.equals(AddDestination.IDENTIFY))
                        {
                            String mode = bundle.getString("MODE");
                            byte[] bytesArray = bundle.getByteArray(AddDestination.RETURN_RESULT);
                            Destination destination = Destination.toObject(bytesArray);
                            if (mode.equals(AddDestination.VIEW_DESTINATION))
                            {
                                return;
                            }
                            else if (mode.equals(AddDestination.EDIT_DESTINATION))
                            {
                                //TODO: edit destination
                                isEditing = true;
                                int index = bundle.getInt("INDEX");
                                destinationList.get(index).setDestination(destination);
                                onStart();
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
                        else if(identify.equals(CreatePlan.IDENTIFY))
                        {
                            String mode = bundle.getString("MODE");
                            byte[] bytesArray = bundle.getByteArray(CreatePlan.RETURN_RESULT);
                            specPlan = Plan.byteArrayToObject(bytesArray);
                            if(mode.equals(TripsPage.EDIT_PLAN_MODE))
                            {
                                Runnable successfulTask = new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Update plan successfully", Toast.LENGTH_LONG).show();
                                        onStart();
                                    }
                                };

                                Runnable failedTask = new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Update plan failed", Toast.LENGTH_LONG).show();
                                    }
                                };

                                DatabaseAccess.updatePlanInfo(specPlan, successfulTask, failedTask);
                            }

                        }
                    }
                    else if(result.getResultCode() == Activity.RESULT_CANCELED)
                    {
                        return;
                    }
                }
            });
    @Override
    public void onPause()
    {
        super.onPause();
    }
}
