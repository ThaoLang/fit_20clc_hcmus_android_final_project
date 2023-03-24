package com.example.fit_20clc_hcmus_android_final_project;

import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivitySigninBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ForkJoinTask;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    ActivitySigninBinding binding;

    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    final private String WRONG_EMAIL_INPUT = "The entered email is not correct, please check again.\n";
    final private String WRONG_PASSWORD_INPUT = "The entered password is not correct, please check again.\n";
    final private String SUCCESSFUL = "Sign in successfully!!\n";
    final private String NO_EMAIL_INPUT = "Please enter your email.\n";
    final private String NO_PASSWORD_INPUT = "Please enter your password.\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.buttonConfirm.getId()) {

            String warning = "";
            String InputEmail = binding.editTextEmailAddress.getText().toString();
            String InputPassword = binding.editTextPassword.getText().toString();

            if (InputEmail.isEmpty()) {
                warning = warning.concat(NO_EMAIL_INPUT);
            }
            if (InputPassword.isEmpty()) {
                warning = warning.concat(NO_PASSWORD_INPUT);
            }

            if (!warning.isEmpty()) //input data has problems.
            {
                Toast.makeText(this, warning, Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(InputEmail, InputPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignIn.this, SUCCESSFUL, Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignIn.this, WRONG_EMAIL_INPUT, Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignIn.this, WRONG_PASSWORD_INPUT, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            // check email and password from database
//            Query queryByEmail = FirebaseFirestore.getInstance().collection("account")
//                    .whereEqualTo("email", InputEmail);
//            AggregateQuery countQuery = queryByEmail.count();
//            countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        AggregateQuerySnapshot snapshot = task.getResult();
//                        if (snapshot.getCount() == 0) {
//                            Toast.makeText(SignIn.this, WRONG_EMAIL_INPUT, Toast.LENGTH_SHORT).show();
//                        } else {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            if(!InputPassword.equals(WRONG_PASSWORD_INPUT)) //insert database password here
//                            {
//                                Toast.makeText(SignIn.this, WRONG_PASSWORD_INPUT, Toast.LENGTH_SHORT).show();
//                            } else{
//                                //create successfully
//                                //move to next page
//                            }
//                        }
//                    }
//                }
//            });

        } else if (v.getId() == binding.buttonConfirm.getId()) { //forget password
            Intent intent = new Intent(SignIn.this, ForgetPassword.class);
            System.out.println(intent);
            //startActivityForResult(intent, START_FORGET_CODE);
        }
    }
}
