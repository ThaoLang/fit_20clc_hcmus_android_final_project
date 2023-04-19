package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class AddDestination extends AppCompatActivity {
    private TextInputEditText formalName, alias, startDate, endDate, startTime, endTime, description;
    private MaterialButton searchButton, startDatePickerButton, endDatePickerButton, startTimePickerButton, endTimePickerButton;
    private MaterialToolbar toolbar;

    private MaterialButton mainButton;
    private String _currentMode;
    private Destination _destination;
    private String _planId;

    private boolean isPermittedToEdit = false;

    public final static String SETTING_MODE = "SETTING_MODE";

    public final static String ADD_DESTINATION = "ADD_DESTINATION";

    public final static String EDIT_DESTINATION = "EDIT_DESTINATION";
    public final static String VIEW_DESTINATION = "VIEW_DESTINATION";
    public final static String RETURN_RESULT = "RETURN_RESULT";

    private final String INVALID_FORMAL_NAME = "Please search for a destination!";
    private final String EMPTY_ALIAS = "Please provide an alias for the destination";
    private final String EMPTY_START_DATE = "Please provide a date for starting the activity at this destination";
    private final String EMPTY_END_DATE = "Please provide a date for completing the activity at this destination ";
    private final String EMPTY_START_TIME = "Please provide the time for starting the activity at this destination";
    private final String EMPTY_END_TIME = "Please provide the time for completing the activity at this destination";
    private final String CONFLICT_START_DATE = "Please check the start date. It's in conflict with the time of the trip";
    private final String CONFLICT_END_DATE = "Please check the end date. It's in conflict with the time of the trip";
    private final String CONFLICT_START_END_DATE = "There is a conflict with start date and end date";


    private int typeOfSelectedDatePicker;
    private int typeOfSelectedTimePicker;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);

        formalName = findViewById(R.id.destination_formal_name);
        alias = findViewById(R.id.destination_alias);
        startDate = findViewById(R.id.destination_start_date);
        endDate = findViewById(R.id.destination_end_date);
        startTime = findViewById(R.id.destination_start_time);
        endTime = findViewById(R.id.destination_end_time);
        description = findViewById(R.id.destination_description);

        searchButton = findViewById(R.id.destination_search_button);
        startDatePickerButton = findViewById(R.id.destination_start_date_picker);
        endDatePickerButton = findViewById(R.id.destination_end_date_picker);
        startTimePickerButton = findViewById(R.id.destination_start_time_picker);
        endTimePickerButton = findViewById(R.id.destination_end_time_picker);
        mainButton = findViewById(R.id.destination_main_button);

        toolbar = findViewById(R.id.destination_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(SETTING_MODE, _currentMode);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        calendar = Calendar.getInstance();
        int instanceDay = calendar.get(Calendar.DAY_OF_MONTH);
        int instanceMonth = calendar.get(Calendar.MONTH);
        int instanceYear = calendar.get(Calendar.YEAR);
        int instanceHour = calendar.get(Calendar.HOUR_OF_DAY);
        int instanceMinute = calendar.get(Calendar.MINUTE);

        _destination = null;
        _planId = "";
        Intent initIntent = getIntent();
        //this bundle always exists
        Bundle initBundle = initIntent.getBundleExtra(DetailedPlan.SPEC_DESTINATION);
        _currentMode = initBundle.getString(SETTING_MODE);
        _planId = initBundle.getString("PLAN_ID");
        if (!_currentMode.equals(ADD_DESTINATION)) {
            byte[] bytesArrayOfSpecPlan = initBundle.getByteArray(DetailedPlan.SPEC_DESTINATION);
            _destination = Destination.toObject(bytesArrayOfSpecPlan);
        }

        startDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfSelectedDatePicker = 1;
                DatePickerDialog dialog = new DatePickerDialog(AddDestination.this, dateSetListener, instanceYear, instanceMonth, instanceDay);
                dialog.show();
            }
        });

        endDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfSelectedDatePicker = 2;
                DatePickerDialog dialog = new DatePickerDialog(AddDestination.this, dateSetListener, instanceYear, instanceMonth, instanceDay);
                dialog.show();
            }
        });

        startTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfSelectedTimePicker = 1;
                TimePickerDialog dialog = new TimePickerDialog(AddDestination.this, timeSetListener, instanceHour, instanceMinute, true);
                dialog.show();
            }
        });

        endTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfSelectedTimePicker = 2;
                TimePickerDialog dialog = new TimePickerDialog(AddDestination.this, timeSetListener, instanceHour, instanceMinute, true);
                dialog.show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddDestination.this, Search.class);
                intent.putExtra(Search.SEARCH_MODE, Search.SEARCH_RETURN_FORMAL_NAME);
                launcher.launch(intent);
            }
        });

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_currentMode.equals(VIEW_DESTINATION))
                {
                    _currentMode = EDIT_DESTINATION;
                    onStart();
                    return;
                }

                Plan planMapping = DatabaseAccess.getPlanById(_planId);
                String finalFormalName = formalName.getText().toString();
                String finalAlias = alias.getText().toString();
                String finalStartDate = startDate.getText().toString();
                String finalEndDate = endDate.getText().toString();
                String finalStartTime = startTime.getText().toString();
                String finalEndTime = endTime.getText().toString();
                String finalDescription = description.getText().toString();
                Date startDateOfDest = parseDateFromString("dd/MM/yyyy", finalStartDate);
                Date startDateOfPlan = parseDateFromString("dd/MM/yyyy", planMapping.getDeparture_date());
                Date endDateOfDest = parseDateFromString("dd/MM/yyyy", finalEndDate);
                Date endDateOfPlan = parseDateFromString("dd/MM/yyyy", planMapping.getReturn_date());
                if(finalFormalName.isEmpty() || finalFormalName.equals("Not found"))
                {
                    Toast.makeText(AddDestination.this, INVALID_FORMAL_NAME, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(finalAlias.isEmpty())
                {
                    Toast.makeText(AddDestination.this, EMPTY_ALIAS, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(finalStartDate.isEmpty())
                {
                    Toast.makeText(AddDestination.this, EMPTY_START_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(finalEndDate.isEmpty())
                {
                    Toast.makeText(AddDestination.this, EMPTY_END_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(finalStartTime.isEmpty())
                {
                    Toast.makeText(AddDestination.this, EMPTY_START_TIME, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(finalEndTime.isEmpty())
                {
                    Toast.makeText(AddDestination.this, EMPTY_END_TIME, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(startDateOfDest.compareTo(startDateOfPlan) < 0 || startDateOfDest.compareTo(endDateOfPlan) > 0)
                {
                    Toast.makeText(AddDestination.this, CONFLICT_START_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(endDateOfDest.compareTo(startDateOfPlan) < 0 || endDateOfDest.compareTo(endDateOfPlan) > 0)
                {
                    Toast.makeText(AddDestination.this, CONFLICT_END_DATE, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(startDateOfDest.compareTo(endDateOfDest) > 0)
                {
                    Toast.makeText(AddDestination.this, CONFLICT_START_END_DATE, Toast.LENGTH_LONG).show();
                    return;
                }

                Destination newDest = new Destination();
                newDest.setFormalName(finalFormalName);
                newDest.setAliasName(finalAlias);
                newDest.setStartDate(finalStartDate);
                newDest.setStartTime(finalStartTime);
                newDest.setEndDate(finalEndDate);
                newDest.setEndTime(finalEndTime);
                newDest.setDescription(finalDescription);

                Intent intent = new Intent();
                intent.putExtra(SETTING_MODE, _currentMode);
                intent.putExtra(RETURN_RESULT, Destination.toByteArray(newDest));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if(DatabaseAccess.getPlanById(_planId).getSet_of_editors().contains(DatabaseAccess.getMainUserInfo().getUserEmail()) ||
                DatabaseAccess.getPlanById(_planId).getOwner_email().equals(DatabaseAccess.getMainUserInfo().getUserEmail()))
        {
            isPermittedToEdit = true;
        }
        else
        {
            isPermittedToEdit = false;
        }

        formalName.setEnabled(true);
        alias.setEnabled(true);
        startDate.setEnabled(false);
        endDate.setEnabled(false);
        startTime.setEnabled(false);
        endTime.setEnabled(false);
        description.setEnabled(true);
        startDatePickerButton.setVisibility(View.INVISIBLE);
        endDatePickerButton.setVisibility(View.INVISIBLE);
        startTimePickerButton.setVisibility(View.INVISIBLE);
        endTimePickerButton.setVisibility(View.INVISIBLE);
        mainButton.setVisibility(View.VISIBLE);
        mainButton.setText("Add");
//        toolbar.setTitle(_destination.getName());
        if (_currentMode.equals(VIEW_DESTINATION)) {
            mainButton.setText("Edit");
            if (isPermittedToEdit == false) {
                mainButton.setVisibility(View.INVISIBLE);
            }
            formalName.setText(_destination.getFormalName());
            alias.setText(_destination.getAliasName());
            startDate.setText(_destination.getStartDate());
            endDate.setText(_destination.getEndDate());
            startTime.setText(_destination.getStartTime());
            endTime.setText(_destination.getEndTime());
            description.setText(_destination.getDescription());
            formalName.setEnabled(false);
            alias.setEnabled(false);
            description.setEnabled(false);
            toolbar.setTitle(_destination.getAliasName());
        } else if (_currentMode.equals(EDIT_DESTINATION)) {
            if (isPermittedToEdit == true) {
                formalName.setText(_destination.getFormalName());
                alias.setText(_destination.getAliasName());
                startDate.setText(_destination.getStartDate());
                endDate.setText(_destination.getEndDate());
                startTime.setText(_destination.getStartTime());
                endTime.setText(_destination.getEndTime());
                description.setText(_destination.getDescription());
                mainButton.setText("Save");
                formalName.setEnabled(true);
                alias.setEnabled(true);
                description.setEnabled(true);
                startDatePickerButton.setVisibility(View.VISIBLE);
                endDatePickerButton.setVisibility(View.VISIBLE);
                startTimePickerButton.setVisibility(View.VISIBLE);
                endTimePickerButton.setVisibility(View.VISIBLE);
                toolbar.setTitle(_destination.getAliasName());
            }
        }
        else if(_currentMode.equals(ADD_DESTINATION))
        {
            if(isPermittedToEdit == true)
            {
                toolbar.setTitle("Add new destination");
                formalName.setEnabled(true);
                alias.setEnabled(true);
                description.setEnabled(true);
                startDatePickerButton.setVisibility(View.VISIBLE);
                endDatePickerButton.setVisibility(View.VISIBLE);
                startTimePickerButton.setVisibility(View.VISIBLE);
                endTimePickerButton.setVisibility(View.VISIBLE);
                mainButton.setText("Add");
                mainButton.setVisibility(View.VISIBLE);
                return;
            }

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
            String selectedDate = new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).toString();
            if (typeOfSelectedDatePicker == 1) {
                startDate.setText(selectedDate);
            } else if (typeOfSelectedDatePicker == 2) {
                endDate.setText(selectedDate);
            } else if (typeOfSelectedDatePicker == 0) {
                return;
            }
        }
    };


    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//            String selectedTime = new StringBuilder().append(hour).append(":").append(minute).toString();
            String selectedTime = LocalTime.of(hour, minute).toString();
            if (typeOfSelectedTimePicker == 1) {
                startTime.setText(selectedTime);
            } else if (typeOfSelectedTimePicker == 2) {
                endTime.setText(selectedTime);
            } else {
                return;
            }
        }
    };

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle returnedBundle = result.getData().getBundleExtra("location search");
                        String returnedFormalName = returnedBundle.getString("location address");
                        formalName.setText(returnedFormalName);
                        alias.setText(returnedFormalName);
                    } else {
                        formalName.setText("Not found");
                    }
                }
            });

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

}
