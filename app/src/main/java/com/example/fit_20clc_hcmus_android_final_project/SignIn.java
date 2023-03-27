package com.example.fit_20clc_hcmus_android_final_project;

import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivitySigninBinding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ForkJoinTask;

public class SignIn extends AppCompatActivity{
    ActivitySigninBinding binding;

    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    final private String WRONG_INPUT = "The entered email and/or password is not correct, please check again.\n";
    final private String SUCCESSFUL = "Sign in successfully!!\n";
    final private String NO_EMAIL_INPUT = "Please enter your email.\n";
    final private String NO_PASSWORD_INPUT = "Please enter your password.\n";

//    private final int START_FORGETPASSWORD_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(SignIn.this, warning, Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(InputEmail, InputPassword)
                        .addOnCompleteListener(SignIn.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignIn.this, SUCCESSFUL, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                mainActivityResultLauncher.launch(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignIn.this, WRONG_INPUT, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.buttonForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ForgetPassword.class);

                String InputEmail = binding.editTextEmailAddress.getText().toString();
                if (InputEmail.isEmpty()) {
                    Toast.makeText(SignIn.this, NO_EMAIL_INPUT, Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("email", InputEmail);

                intent.putExtra("SignIn", bundle);

//                startActivityForResult(intent, START_FORGETPASSWORD_CODE);
                forgetPasswordActivityResultLauncher.launch(intent);
            }
        });

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, Registration.class);

                registerActivityResultLauncher.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> forgetPasswordActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        // Intent data = result.getData();
                        //
                   }
                }
            });

    ActivityResultLauncher<Intent> mainActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        // Intent data = result.getData();
                        //
                    }
                }
            });

    ActivityResultLauncher<Intent> registerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        // Intent data = result.getData();
                        //
                    }
                }
            });
}
