package com.example.fit_20clc_hcmus_android_final_project;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountInfoPage extends Fragment {

    private MainActivity main;
    private static Context context;
    private static final String INIT_PARAM = "initParam";

    private TextInputEditText username, userbio, useremail, userphone, useraddress;
    private MaterialButton edit_save_button, logout_button;
    private ImageView avatar;

    private String initParam;

    private static String UpdateSucessfully = "Update successfully...";
    private static String UpdateFailed = "Update failed...";

    private boolean EDIT_OR_SAVE; //true: the current mode is save-mode -> if the button is clicked, it will save changed info
    // , false: the current mode is edit-mode -> if the button is clicked, it will allow to edit the info
    public AccountInfoPage()
    {}

    public static AccountInfoPage newInstance(String initParam)
    {
        AccountInfoPage accountPage = new AccountInfoPage();
        Bundle bundle = new Bundle();
        bundle.putString(INIT_PARAM, initParam);
        accountPage.setArguments(bundle);
        return accountPage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            initParam = getArguments().getString(INIT_PARAM);
        }
        EDIT_OR_SAVE = false;
        try
        {
            context = getContext();
            main = (MainActivity) getActivity();
        }
        catch(IllegalStateException e)
        {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View accountScreen = inflater.inflate(R.layout.activity_manage_account, null);
        //connect to the layout

        avatar = accountScreen.findViewById(R.id.mac_avatar);
        username = accountScreen.findViewById(R.id.mac_text_input_edittext_username);
        userbio = accountScreen.findViewById(R.id.mac_text_input_edittext_userbio);
        userphone = accountScreen.findViewById(R.id.mac_text_input_edittext_userphone);
        useremail = accountScreen.findViewById(R.id.mac_text_input_edittext_useremail);
        useraddress = accountScreen.findViewById(R.id.mac_text_input_edittext_useraddress);
        edit_save_button = accountScreen.findViewById(R.id.mac_edit_save_button);
        logout_button = accountScreen.findViewById(R.id.mac_logout_button);
//        chat_button = accountScreen.findViewById(R.id.mac_chat_button);

        edit_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!EDIT_OR_SAVE)//the current mode is edit-mode
                {
                    username.setEnabled(true);
                    username.setTextColor(Color.parseColor("#A8C0FF"));

                    userbio.setEnabled(true);
//                    useremail.setEnabled(true);
                    userphone.setEnabled(true);
                    useraddress.setEnabled(true);
                    //change edit_save_button to save_button
                    edit_save_button.setText(R.string.save_account);
                    edit_save_button.setIconResource(R.drawable.save_48px);
                    EDIT_OR_SAVE = true;
                } else {
                    String inputusername = String.valueOf(username.getText());
                    String inputuserbio = String.valueOf(userbio.getText());
//                    String inputuseremail = String.valueOf(useremail.getText());
                    String inputuserphone = String.valueOf(userphone.getText());
                    String inputuseraddress = String.valueOf(useraddress.getText());

                    if (inputusername.isEmpty()) {
                        Toast.makeText(getContext(), "Please provide username", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (inputuserphone.isEmpty()) {
                        Toast.makeText(getContext(), "Please provide your phone", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (inputuseraddress.isEmpty()) {
                        Toast.makeText(getContext(), "Please provide your address", Toast.LENGTH_LONG).show();
                        return;
                    }

                    username.setEnabled(false);
                    username.setTextColor(Color.parseColor("#2DD5B7"));

                    userbio.setEnabled(false);
//                    useremail.setEnabled(false);
                    userphone.setEnabled(false);
                    useraddress.setEnabled(false);
                    edit_save_button.setText(R.string.edit_account);
                    edit_save_button.setIconResource(R.drawable.edit_48px);
                    EDIT_OR_SAVE = false;

                    User mainUserInfo = DatabaseAccess.getMainUserInfo();

                    mainUserInfo.setName(inputusername);
                    mainUserInfo.setPhone(inputuserphone);
                    mainUserInfo.setAddress(inputuseraddress);
                    mainUserInfo.setBio(inputuserbio);

                    DatabaseAccess.updateUserInfo_In_Database(mainUserInfo, toast(UpdateSucessfully), toast(UpdateFailed));
//                    fb.collection(DatabaseAccess.ACCESS_ACCOUNT_COLLECTION).document(user.getUid()).set(mainUserInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                DatabaseAccess.up
//                                Toast.makeText(context, "Update successfully...", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(context, "Update failed...", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
                }
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                startActivity(new Intent(getContext(), SignIn.class));
                getParentFragment().onDetach();
            }
        });

//        chat_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(), ChatActivity.class));
//            }
//        });

        return accountScreen;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = DatabaseAccess.getCurrentUser();
        User mainUserInfo = DatabaseAccess.getMainUserInfo();
        if(mainUserInfo != null)
        {
            username.setText(mainUserInfo.getName());
            userbio.setText(mainUserInfo.getBio());
            useremail.setText(user.getEmail());
            useraddress.setText(mainUserInfo.getAddress());
            userphone.setText(mainUserInfo.getPhone());
            Glide.with(this)
                    .load(mainUserInfo.get_avatar_url())
                    .into(avatar);
        }
    }

    public static Runnable toast(String message)
    {
        Runnable foregroundToastAction = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        };
        return  foregroundToastAction;
    }

}