package com.example.fit_20clc_hcmus_android_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    EditText smartOTP;
    Button confirm;

//    final private String WRONG_OTP_INPUT = "The entered SmartOTP is not correct, please check again.\n";
//    final private String NO_OTP_INPUT = "Please enter your SmartOTP.\n";

    final private String RECEIVE_DATA_CODE = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        smartOTP = (EditText) findViewById(R.id.editTextSmartOTP);
        confirm = (Button) findViewById(R.id.buttonSubmit);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String warning = "";
        String email;

        Intent intent = this.getIntent();
        Bundle ReceivedBundle = intent.getBundleExtra(RECEIVE_DATA_CODE);

        email = ReceivedBundle.getString("email");

        Toast.makeText(this, email, Toast.LENGTH_LONG).show();

        // can send verification email ?

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPassword.this, "Email sent.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

//        String otp = smartOTP.getText().toString();

//        if(otp.isEmpty()) {
//            warning = warning.concat(NO_OTP_INPUT);
//        }


        //if(otp.equals(<email's SmartOTP>) == false)
        //{
        //    warning = warning.concat(WRONG_OTP_INPUT);
        //}

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();


        if(!warning.isEmpty()) //input data has problems.
        {
            Toast.makeText(this, warning, Toast.LENGTH_LONG).show();
            return;
        }
        else{
            //move to next page
        }
    }
}