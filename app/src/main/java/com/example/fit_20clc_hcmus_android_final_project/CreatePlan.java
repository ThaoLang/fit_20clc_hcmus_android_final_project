package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.app.DatePickerDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreatePlan extends AppCompatActivity {

    private TextInputEditText planName, departureDate, returnDate;
    private MaterialTextView passengers;

    private MaterialButton departureDatePicker, returnDatePicker, continueButton, inviteButton, editImageButton;
    private ShapeableImageView image;

    private SwitchMaterial publicSwitch;

    private Calendar calendar;

    private String SETTING_MODE = TripsPage.CREATE_PLAN_MODE;
    private Plan _plan;
    private int typeOfSelectedDatePicker; //0: none, 1: departure date, 2: return date

    private boolean isTransacted;
    private Uri selectedImageUri = null;
    private String EMPTY_PLAN_NAME = "Please provide the plan's name!";
    private String EMPTY_DEPARTURE_DATE= "Please provide the departure date!";
    private String EMPTY_RETURN_DATE= "Please provide the return date!";
    private String INVALID_DEPARTURE_DATE = "Invalid departure date provided. Format: dd/MM/yyyy";
    private String INVALID_RETURN_DATE = "Invalid return date provided. Format: dd/MM/yyyy";
    private final String INVALID_OCCURENCE_DATE = "Please revise the departure date and the return date!";
    private String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    public static String RETURN_BUNDLE = "CREATE_PLAN";
    public static String RETURN_NEW_PLAN_CODE = "RETURN_NEW_PLAN";
    public static final String RETURN_EDITED_PLAN = "RETURN_EDITED_PLAN";

    public static final String RETURN_RESULT = "RETURN_RESULT";

    public static final String IDENTIFY = "CREATE_PLAN_CODE";

    public static String RETURN_IMAGE_URI = "RETURN_IMAGE_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);
        planName = findViewById(R.id.create_plan_name);
        departureDate = findViewById(R.id.create_plan_depature_date);
        returnDate = findViewById(R.id.create_plan_return_date);
        passengers = findViewById(R.id.create_plan_invited_passengers);

        departureDatePicker = findViewById(R.id.create_plan_depature_date_picker);
        returnDatePicker = findViewById(R.id.create_plan_return_date_picker);
        continueButton = findViewById(R.id.create_plan_continue_button);
        inviteButton = findViewById(R.id.create_plan_invite_button);
        editImageButton = findViewById(R.id.create_plan_edit_image_button);

        image = findViewById(R.id.create_plan_image);
        publicSwitch = findViewById(R.id.create_plan_public_switch);

        typeOfSelectedDatePicker = 0;

        _plan = new Plan();
        isTransacted = false;

        SETTING_MODE = getIntent().getStringExtra("SETTING_MODE");

        if(SETTING_MODE.equals(TripsPage.EDIT_PLAN_MODE))
        {
            String planId = getIntent().getStringExtra(DetailedPlan.DETAILED_PLAN_ID);
            _plan = DatabaseAccess.getPlanById(planId);
        }
        
        
        calendar = Calendar.getInstance();
        int instanceDay = calendar.get(Calendar.DAY_OF_MONTH);
        int instanceMonth = calendar.get(Calendar.MONTH);
        int instanceYear = calendar.get(Calendar.YEAR);

        departureDatePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                typeOfSelectedDatePicker = 1;
                DatePickerDialog dialog = new DatePickerDialog(CreatePlan.this, dateSetListener, instanceYear, instanceMonth, instanceDay);
                dialog.show();
            }
        });

        returnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfSelectedDatePicker = 2;
                DatePickerDialog dialog = new DatePickerDialog(CreatePlan.this, dateSetListener, instanceYear, instanceMonth, instanceDay);
                dialog.show();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPlanName = planName.getText().toString();
                String inputDepartureDate = departureDate.getText().toString();
                String inputReturnDate = returnDate.getText().toString();
                Date departureDate = parseDateFromString(DATE_FORMAT_PATTERN, inputDepartureDate);
                Date returnDate = parseDateFromString(DATE_FORMAT_PATTERN, inputReturnDate);
                boolean isPublic = false;

                if(inputPlanName.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), EMPTY_PLAN_NAME, Toast.LENGTH_LONG).show();
                    return;
                }
                if(inputDepartureDate.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), EMPTY_DEPARTURE_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                if(inputReturnDate.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), EMPTY_RETURN_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                if(isValidDate(DATE_FORMAT_PATTERN, inputDepartureDate) == false)
                {
                    Toast.makeText(getApplicationContext(), INVALID_DEPARTURE_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                if(isValidDate(DATE_FORMAT_PATTERN, inputReturnDate) == false)
                {
                    Toast.makeText(getApplicationContext(), INVALID_RETURN_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                if(departureDate.compareTo(returnDate) > 0)
                {
                    Toast.makeText(getApplicationContext(), INVALID_OCCURENCE_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                if(publicSwitch.isChecked() == true)
                {
                    isPublic = true;
                }
                _plan.setName(inputPlanName);
                _plan.setDeparture_date(inputDepartureDate);
                _plan.setReturn_date(inputReturnDate);
                _plan.setPublicAttribute(isPublic);

                if(SETTING_MODE.equals(TripsPage.CREATE_PLAN_MODE))
                {
                    _plan.setOwner_email(DatabaseAccess.getMainUserInfo().getUserEmail());
                    _plan.setStatus(TripsPage.UPCOMING);
                }

                if(SETTING_MODE.equals(TripsPage.CREATE_PLAN_MODE))
                {
                    if(selectedImageUri == null)
                    {
                        _plan.setImageLink("None");
                    }
                    else
                    {
                        _plan.setImageLink(selectedImageUri.toString());
                    }
                }
                else if(SETTING_MODE.equals(TripsPage.EDIT_PLAN_MODE))
                {
                    if(selectedImageUri != null)
                    {
                        _plan.setImageLink(selectedImageUri.toString());
                    }
                }


                Bundle returnBundle = new Bundle();
//                if(SETTING_MODE.equals(TripsPage.CREATE_PLAN_MODE))
//                {
//                    returnBundle.putByteArray(RETURN_NEW_PLAN_CODE, Plan.planToByteArray(_plan));
//                    returnBundle.putString("CREATE_STATUS", "EXIST");
//                }
//                else if(SETTING_MODE.equals(TripsPage.EDIT_PLAN_MODE))
//                {
//                    returnBundle.putByteArray(RETURN_EDITED_PLAN, Plan.planToByteArray(_plan));
//                    returnBundle.putString("CREATE_STATUS", "EXIST");
//                }
                returnBundle.putByteArray(RETURN_RESULT, Plan.planToByteArray(_plan));
                returnBundle.putString("CREATE_STATUS", "EXIST");
                returnBundle.putString("MODE", SETTING_MODE);

                Intent returnIntent = new Intent();
                returnIntent.putExtra(RETURN_BUNDLE, returnBundle);
                returnIntent.putExtra("IDENTIFY", IDENTIFY);
                isTransacted = true;
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
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

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(SETTING_MODE.equals(TripsPage.EDIT_PLAN_MODE))
        {
            planName.setText(_plan.getName());
            departureDate.setText(_plan.getDeparture_date());
            returnDate.setText(_plan.getReturn_date());
            passengers.setText(String.valueOf(_plan.getPassengers().size()));
            publicSwitch.setChecked(_plan.getPublicAttribute());
            publicSwitch.setVisibility(View.INVISIBLE);
            inviteButton.setVisibility(View.INVISIBLE);

            if(_plan.getImageLink().equals("None"))
            {
                image.setImageResource(R.drawable.image_48px);
            }
            else
            {
                DatabaseAccess.getFirebaseStorage().getReference().child(_plan.getImageLink())
                        .getBytes(1024*2*1024).addOnSuccessListener(
                                new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        image.setImageBitmap(loadedImage);
                                    }
                                }
                        ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("LOAD IMAGE FAILED", e.getMessage());
                                        image.setImageResource(R.drawable.image_48px);
                                    }
                                }
                        );
            }

            continueButton.setText("Edit");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(isTransacted == false)
        {
            setResult(Activity.RESULT_CANCELED);
        }
    }

    private boolean isValidDate(String format_pattern, String value)
    {
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(format_pattern);
            sdf.setLenient(false); //resolves date and times strictly
        }
        catch (NullPointerException | IllegalArgumentException exception)
        {
            throw exception;
        }

        try {
            Date parsedDate = sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    private Date parseDateFromString(String format_pattern, String value)
    {
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(format_pattern);
            sdf.setLenient(false); //resolves date and times strictly
        }
        catch (NullPointerException | IllegalArgumentException exception)
        {
            throw exception;
        }

        try {
            Date parsedDate = sdf.parse(value);
            return parsedDate;
        } catch (ParseException e) {
            return null;
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //arg1 = year
            //arg2 = month (start from 0 to 11) -> month = arg2 + 1;
            //arg3 = day
            //System.out.println("arg1= " + arg1 + " |arg2= " + arg2 + " |arg3= " + arg3);

            //date format: dd/MM/yyyy -> arg3/(arg2+1)/arg1
            String selectedDate = new StringBuilder().append(day).append("/").append(month+1).append("/").append(year).toString();
            if(typeOfSelectedDatePicker == 1)
            {
                departureDate.setText(selectedDate);
            }
            else if(typeOfSelectedDatePicker == 2)
            {
                returnDate.setText(selectedDate);
            }
            else if(typeOfSelectedDatePicker == 0)
            {
                return;
            }
        }
    };

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
//                    System.out.println("ImageUri: " + selectedImageUri);
                    try {
                        getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                        selectedImage.setHeight(image.getHeight());
//                        selectedImage.setWidth(image.getWidth());
                        image.setImageBitmap(selectedImage);
                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                        selectedImageUri = null;
                        image.setImageResource(R.drawable.image_48px);
                    }
                }
                else
                {
                    selectedImageUri = null;
                }
            }
        }
    });

}
