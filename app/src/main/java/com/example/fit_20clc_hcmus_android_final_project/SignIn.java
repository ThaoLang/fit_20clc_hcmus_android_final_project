package com.example.fit_20clc_hcmus_android_final_project;

import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivitySigninBinding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity{
    ActivitySigninBinding binding;

    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    final private String WRONG_INPUT = "The entered email and/or password is not correct, please check again.\n";
    final private String SUCCESSFUL = "Sign in successfully!!\n";
    final private String NO_EMAIL_INPUT = "Please enter your email.\n";
    final private String NO_PASSWORD_INPUT = "Please enter your password.\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);

        String email=sp1.getString("email", null);
        String password = sp1.getString("password", null);

        binding.editTextEmailAddress.setText(email);
        binding.editTextPassword.setText(password);

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
                                // Sign in success
                                Toast.makeText(SignIn.this, SUCCESSFUL, Toast.LENGTH_SHORT).show();

                                // Store sign in data
                                SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor Ed=sp.edit();
                                Ed.putString("email",InputEmail);
                                Ed.putString("password",InputPassword);
                                Ed.commit();

                                // Continue
                                Log.e("EMAIL",InputEmail);
                                Log.e("Pass",InputPassword);
                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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

                startActivity(intent);
            }
        });

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, Registration.class);

                startActivity(intent);
            }
        });
    }

}
