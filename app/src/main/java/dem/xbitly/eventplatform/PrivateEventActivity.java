package dem.xbitly.eventplatform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

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

import dem.xbitly.eventplatform.databinding.ActivityEventPrivateBinding;
import dem.xbitly.eventplatform.ui.map.MapFragment;

public class PrivateEventActivity extends AppCompatActivity {

    private ActivityEventPrivateBinding binding; //ViewBinding

    //Database
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    Calendar dateAndTime = Calendar.getInstance();

    //All info about event
    private HashMap<String, Integer> event_info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventPrivateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ref = database.getReference("PrivateEvents");
        ref.addValueEventListener(new ValueEventListener(){


            boolean a = true;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int y = Integer.parseInt(snapshot.child("count").getValue().toString())+1;
                ref = database.getReference("PrivateEvents").child(String.valueOf(y));
                if(a){
                    snapshot.getRef().child("count").setValue(y);
                    a = false;
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
                    if (task.isSuccessful()) {
                        Snackbar.make(v, "Successfully", Snackbar.LENGTH_SHORT).show();

                        Intent intent = new PlacePicker.IntentBuilder()
                                .setLatLong(40.748672, -73.985628)
                                .showLatLong(true)
                                .setMapType(MapType.NORMAL)
                                .setFabColor(R.color.blue)
                                .setMarkerDrawable(R.drawable.ic_map_marker)
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

            event_info.put("minute", minute);
            event_info.put("hour", hourOfDay);

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

            event_info.put("day", dayOfMonth);
            event_info.put("month", monthOfYear);
            event_info.put("year", year);

            binding.eventDate.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
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

                Intent intent = new Intent (PrivateEventActivity.this, MapFragment.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}