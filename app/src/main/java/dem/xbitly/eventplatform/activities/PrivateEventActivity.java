package dem.xbitly.eventplatform.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.util.Calendar;
import java.util.HashMap;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.databinding.ActivityEventPrivateBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class PrivateEventActivity extends AppCompatActivity {

    private ActivityEventPrivateBinding binding; //ViewBinding

    //Database
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private int event_number; //номер евента

    Calendar dateAndTime = Calendar.getInstance();

    //All info about event
    private HashMap<String, String> event_info = new HashMap<>();

    private FusedLocationProviderClient fusedLocationClient;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventPrivateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNetwork();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//
//            return;
//        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                });



        ref = database.getReference("PrivateEvents");
        ref.addValueEventListener(new ValueEventListener(){

            boolean a = true;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    event_number = Integer.parseInt(snapshot.child("count").getValue().toString()) + 1;
                    ref = database.getReference("PrivateEvents").child(String.valueOf(event_number));
                    if(a){
                        snapshot.getRef().child("count").setValue(event_number);
                        a = false;
                    }
                }catch(Exception e){
                    event_number = 1;
                    ref = database.getReference("PrivateEvents").child(String.valueOf(event_number));
                    if (a){
                        snapshot.getRef().child("count").setValue(event_number);
                        a = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.pickDateBtn.setOnClickListener(v -> setDate());

        binding.pickTimeBtn.setOnClickListener(v -> setTime());

        binding.nextBtnFromEventBtn.setOnClickListener(v -> {
            if (binding.eventNamePrivate.getText().toString().isEmpty()|| binding.eventTime.getText().toString().isEmpty()
                    || binding.eventDate.getText().toString().isEmpty()) { //нельзя, чтобы поля пустыми были
                Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
            } else {
                //если все хорошо, то создаем reference для этого мероприятия
                ref.setValue(event_info).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Snackbar.make(v, "Successfully", Snackbar.LENGTH_SHORT).show();

                        Intent intent = new PlacePicker.IntentBuilder()
                                .setLatLong(latitude, longitude)
                                .showLatLong(true)
                                .setMapType(MapType.NORMAL)
                                .setFabColor(R.color.blue)
                                .setMarkerDrawable(R.drawable.ic_location_marker)
                                .build(PrivateEventActivity.this);

                        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
                    }else {
                        Snackbar.make(v, "Some errors", Snackbar.LENGTH_SHORT).show();
                    }
                });

                ref.child("name").setValue(binding.eventNamePrivate.getText().toString());
                ref.child("userID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
        });
    }


    // отображаем диалоговое окно для выбора даты
    public void setDate() {
        new DatePickerDialog(PrivateEventActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime() {
        new TimePickerDialog(PrivateEventActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            event_info.put("time", hourOfDay + ":" + minute);

            binding.eventTime.setText(hourOfDay + ":" + minute);
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            event_info.put("date", dayOfMonth + "." + monthOfYear + "." + year);

            binding.eventDate.setText(dayOfMonth + "." + monthOfYear + "." + year);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                double latitude = addressData.getLatitude();
                double longitude = addressData.getLongitude();
                ref.child("adress").child("latitude").setValue(latitude);
                ref.child("adress").child("longitude").setValue(longitude);

                Intent intent = new Intent (PrivateEventActivity.this, UsersInvitationActivity.class);
                intent.putExtra("event_number", event_number);
                intent.putExtra("event_name", binding.eventNamePrivate.getText().toString());
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (PrivateEventActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }

}