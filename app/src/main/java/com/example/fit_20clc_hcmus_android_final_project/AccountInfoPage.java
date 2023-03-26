package com.example.fit_20clc_hcmus_android_final_project;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class AccountInfoPage extends Fragment {

    private MainActivity main;
    private Context context;
    private static final String INIT_PARAM = "initParam";

    private TextInputEditText username, userbio, useremail, userphone, useraddress;
    private MaterialButton edit_save_button, logout_button;

    private String initParam;
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
        username = accountScreen.findViewById(R.id.mac_text_input_edittext_username);
        userbio = accountScreen.findViewById(R.id.mac_text_input_edittext_userbio);
        userphone = accountScreen.findViewById(R.id.mac_text_input_edittext_userphone);
        useremail = accountScreen.findViewById(R.id.mac_text_input_edittext_useremail);
        useraddress = accountScreen.findViewById(R.id.mac_text_input_edittext_useremail);
        edit_save_button = accountScreen.findViewById(R.id.mac_edit_save_button);
        logout_button = accountScreen.findViewById(R.id.mac_logout_button);

        edit_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EDIT_OR_SAVE == false)//the current mode is edit-mode
                {
                    username.setEnabled(true);
                    userbio.setEnabled(true);
                    useremail.setEnabled(true);
                    userphone.setEnabled(true);
                    useraddress.setEnabled(true);
                    //change edit_save_button to save_button
                    edit_save_button.setText("Save");
                    edit_save_button.setIcon(Drawable.createFromPath("res/drawable/save_48px.xml"));
                    EDIT_OR_SAVE = true;
                }
                else if(EDIT_OR_SAVE)
                {
                    username.setEnabled(false);
                    userbio.setEnabled(false);
                    useremail.setEnabled(false);
                    userphone.setEnabled(false);
                    useraddress.setEnabled(false);
                    edit_save_button.setText("Edit");
                    edit_save_button.setIcon(Drawable.createFromPath("res/drawable/edit_48px.xml"));
                    EDIT_OR_SAVE = false;
                }
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                startActivity(new Intent(getContext(), SignIn.class));
                main.finish();
            }
        });

        return accountScreen;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
