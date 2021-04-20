package dem.xbitly.eventplatform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.util.Calendar;
import java.util.HashMap;

import dem.xbitly.eventplatform.databinding.ActivityPublicEventBinding;
import dem.xbitly.eventplatform.ui.map.MapFragment;

public class PublicEventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    Calendar dateAndTime = Calendar.getInstance();

    private HashMap<String, Integer> time;

    private ActivityPublicEventBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublicEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        time = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("PublicEvents").child(mAuth.getCurrentUser().getUid()); //раздел со всеми созданными евентами этого человека


        binding.infinityAmountBtn.setOnClickListener(v -> {
            binding.eventMaxAmount.setText("Infinity");
            binding.eventMaxAmount.setEnabled(false);

            time.put("max_amount", 0); //если число участников может быть бесконечным, то записываем 0, что означает бесконечность
        });

        binding.pickDateBtn.setOnClickListener(v -> setDate());

        binding.pickTimeBtn.setOnClickListener(v -> setTime());

        binding.nextBtnFromPublicEventBtn.setOnClickListener(v -> {
            if (binding.eventNamePublic.getText().toString().isEmpty() || binding.eventMaxAmount.getText().toString().isEmpty()
                    || binding.eventTime.getText().toString().isEmpty() || binding.eventDate.getText().toString().isEmpty()) { //нельзя, чтобы поля пустыми были
                Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
            } else {
                if (binding.eventMaxAmount.getText().toString().equals("Infinity")) {
                    time.put("max_amount", 0);
                } else {
                    time.put("max_amount", Integer.valueOf(binding.eventMaxAmount.getText().toString()));
                }
                //если все хорошо, то создаем reference для этого мероприятия
                ref.child(binding.eventNamePublic.getText().toString()).setValue(time).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(v, "Successfully", Snackbar.LENGTH_SHORT).show();

                        Intent intent = new PlacePicker.IntentBuilder()
                                .setLatLong(40.748672, -73.985628)
                                .showLatLong(true)
                                .setMapType(MapType.NORMAL)
                                .setFabColor(R.color.blue)
                                .setMarkerDrawable(R.drawable.ic_map_marker)
                                .build(PublicEventActivity.this);

                        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
                    }else {
                        Snackbar.make(v, "Some errors", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });



    }

    // отображаем диалоговое окно для выбора даты
    public void setDate() {
        new DatePickerDialog(PublicEventActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime() {
        new TimePickerDialog(PublicEventActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            time.put("minute", minute);
            time.put("hour", hourOfDay);

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

            time.put("day", dayOfMonth);
            time.put("month", monthOfYear);
            time.put("year", year);

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
                ref.child(binding.eventNamePublic.getText().toString()).child("adress").child("latitude").setValue(latitude);
                ref.child(binding.eventNamePublic.getText().toString()).child("adress").child("longitude").setValue(longitude);

                Intent intent = new Intent (PublicEventActivity.this, MapFragment.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}