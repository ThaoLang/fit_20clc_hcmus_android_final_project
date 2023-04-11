package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.app.DatePickerDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
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

    private String LAUNCH_MODE = TripsPage.CREATE_PLAN_MODE;
    private Plan newPlan;
    private int typeOfSelectedDatePicker; //0: none, 1: departure date, 2: return date

    private Uri selectedImageUri = null;
    private String EMPTY_PLAN_NAME = "Please provide the plan's name!";
    private String EMPTY_DEPARTURE_DATE= "Please provide the departure date!";
    private String EMPTY_RETURN_DATE= "Please provide the return date!";
    private String INVALID_DEPARTURE_DATE = "Invalid departure date provided. Format: dd/MM/yyyy";
    private String INVALID_RETURN_DATE = "Invalid return date provided. Format: dd/MM/yyyy";
    private final String INVALID_OCCURENCE_DATE = "Please revise the departure date and the return date!";
    private String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    public static String IDENTIFIED_CODE = "CREATE_PLAN";
    public static String RETURN_NEW_PLAN_CODE = "RETURN_NEW_PLAN";

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

        newPlan = new Plan();

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
                newPlan.setName(inputPlanName);
                newPlan.setDeparture_date(inputDepartureDate);
                newPlan.setReturn_date(inputReturnDate);
                newPlan.setPublicAttribute(isPublic);
                newPlan.setOwner_email(DatabaseAccess.getMainUserInfo().getUserEmail());
                newPlan.setImageLink(selectedImageUri.toString());

                Bundle returnBundle = new Bundle();
                returnBundle.putByteArray(RETURN_NEW_PLAN_CODE, Plan.planToByteArray(newPlan));

                Intent returnIntent = new Intent();
                returnIntent.putExtra(IDENTIFIED_CODE, returnBundle);
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
        LAUNCH_MODE = getIntent().getStringExtra("MODE");

        if(LAUNCH_MODE.equals(TripsPage.EDIT_PLAN_MODE))
        {

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
                    System.out.println("ImageUri: " + selectedImageUri);
                    try {
                        getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        image.setImageBitmap(selectedImage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    });

}
