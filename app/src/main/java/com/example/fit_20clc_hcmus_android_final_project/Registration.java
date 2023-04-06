package com.example.fit_20clc_hcmus_android_final_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.RegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RegistrationBinding binding;

    private String DEFAULT_USER_BIO = "something about me";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding=RegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _username=binding.username.getText().toString();
                String _email=binding.email.getText().toString();
                String _phone_number=binding.phoneNumber.getText().toString();
                String _password=binding.password.getText().toString();
                String _confirm_password=binding.confirmPassword.getText().toString();
                String _address=binding.address.getText().toString();

                if (TextUtils.isEmpty(_username)){
                    Toast.makeText(Registration.this,"Please enter username",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(_email)){
                    Toast.makeText(Registration.this,"Please enter email",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!isValidEmail(_email)){
                    Toast.makeText(Registration.this,"Email is invalid",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(_phone_number)){
                    Toast.makeText(Registration.this,"Please enter phone number",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (_phone_number.length()!=10){
                    Toast.makeText(Registration.this,"Phone number is invalid",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(_password)){
                    Toast.makeText(Registration.this,"Please enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (_password.length()<6){
                    Toast.makeText(Registration.this,"Password must have at least 6 characters",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(_confirm_password)){
                    Toast.makeText(Registration.this,"Please enter password again to confirm",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!_confirm_password.equals(_password)){
                    Toast.makeText(Registration.this,"Password and confirm password didn't match ",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(_address)){
                    Toast.makeText(Registration.this,"Please enter address",Toast.LENGTH_SHORT).show();
                    return;
                }

                createAccount(_username,_email,_phone_number,_password,_address);
            }
        });

        binding.callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void createAccount(String username, String email, String phone_number, String password,String address){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        System.out.println("Authentication success");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    List<String> favorite_locations=new ArrayList<String>();
                    List<String> plans=new ArrayList<String>();

                    User data = new User(username, phone_number, address, DEFAULT_USER_BIO, plans, favorite_locations);

                    FirebaseUser user= task.getResult().getUser();
                    db.collection(DatabaseAccess.ACCESS_ACCOUNT_COLLECTION)
                            .document(user.getUid())
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Registration.this,"Create account successfully",Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registration.this,"Fail to create account",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private boolean isValidEmail(String email){
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(email);
        return matcher.matches();
    }
}
