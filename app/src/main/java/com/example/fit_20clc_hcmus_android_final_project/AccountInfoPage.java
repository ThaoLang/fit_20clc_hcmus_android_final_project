package com.example.fit_20clc_hcmus_android_final_project;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_AVATARS_STORAGE;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.runForegroundTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AccountInfoPage extends Fragment {

    private MainActivity main;
    private static Context context;
    private static final String INIT_PARAM = "initParam";

    private TextInputEditText username, userbio, useremail, userphone, useraddress;
    private MaterialButton edit_save_button, logout_button, editImageButton;
    private Uri selectedImageUri = null;
    private ShapeableImageView avatar;

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
        editImageButton = accountScreen.findViewById(R.id.mac_edit_avatar_button);

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
                    editImageButton.setEnabled(true);
                    editImageButton.setCursorVisible(false);
                    editImageButton.setVisibility(View.VISIBLE);
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
                    editImageButton.setEnabled(false);
                    editImageButton.setVisibility(View.INVISIBLE);
                    EDIT_OR_SAVE = false;

                    User mainUserInfo = DatabaseAccess.getMainUserInfo();

                    mainUserInfo.setName(inputusername);
                    mainUserInfo.setPhone(inputuserphone);
                    mainUserInfo.setAddress(inputuseraddress);
                    mainUserInfo.setBio(inputuserbio);

                    if(selectedImageUri == null)
                    {
                        mainUserInfo.setAvatarUrl("None");
                    }
                    else
                    {
                        mainUserInfo.setAvatarUrl(String.valueOf(selectedImageUri));
                    }

                    addNewAvatar();

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

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                selectImageLauncher.launch(intent);
            }
        });

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

            if (mainUserInfo.getAvatarUrl() != null){
                final long MAX_BYTE = 1024 * 2 * 1024;
                StorageReference storageReference = DatabaseAccess.getFirebaseStorage().getReference().child(mainUserInfo.getAvatarUrl());
                storageReference.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        avatar.setImageBitmap(image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(context.getApplicationContext())
                                .load(mainUserInfo.getAvatarUrl())
                                .into(avatar);
                    }
                });
            }
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

    private ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK)
            {
                Intent imageData = result.getData();
                if(imageData != null && imageData.getData() != null)
                {
                    selectedImageUri = imageData.getData();
                    Bitmap selectedImage;
                    System.out.println("ImageUri: " + selectedImageUri);
                    try {
                        main.getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedImage = MediaStore.Images.Media.getBitmap(main.getContentResolver(), selectedImageUri);
//                        selectedImage.setHeight(image.getHeight());
//                        selectedImage.setWidth(image.getWidth());
                        avatar.setImageBitmap(selectedImage);
//                        editImageButton.setCursorVisible(true);
                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                        selectedImageUri = null;
                        avatar.setImageResource(R.drawable.image_48px);
                    }
                }
                else
                {
                    selectedImageUri = null;
                }
            }
        }
    });

    public void addNewAvatar() //wip
    {
        User mainUserInfo = DatabaseAccess.getMainUserInfo();
        Runnable successfulTask = null, failureTask = null;

        Thread backgroundAddition = new Thread(new Runnable() {
            @Override
            public void run() {
//                add and update data in the cloud database
                DatabaseAccess.getFirestore().runTransaction(new Transaction.Function<String>() {
                    @Nullable
                    @Override
                    public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException { return mainUserInfo.getPhone();}
                }).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String userTargetId) {
                        String imageLink = mainUserInfo.getAvatarUrl();
                        if (!imageLink.equals("None")) {
                            Uri imageNeedToUpload = Uri.parse(imageLink);
                            String childName = userTargetId + ".jpg";
                            StorageReference plansImageReference = DatabaseAccess.getFirebaseStorage().getReference().child(ACCESS_AVATARS_STORAGE + childName);
                            System.out.println("imageLocalUri" + imageNeedToUpload);
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg").build();

                            UploadTask uploadTask = (UploadTask) plansImageReference.putFile(imageNeedToUpload, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUrl = ACCESS_AVATARS_STORAGE + childName;
                                    DatabaseAccess.getFirestore().collection("account").document(userTargetId)
                                            .update("avatarUrl", downloadUrl);
                                    mainUserInfo.setAvatarUrl(downloadUrl);
                                    DatabaseAccess.updateUserInfo_In_Database(mainUserInfo, toast(UpdateSucessfully), toast(UpdateFailed));

                                }
                            });
                        }

//                        onStart();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Exception: " + e);
                        if(failureTask != null)
                        {
                            runForegroundTask(failureTask);
                        }
                    }
                });

            }
        });
        backgroundAddition.start();
    }
}