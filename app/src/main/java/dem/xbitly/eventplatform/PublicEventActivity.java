package dem.xbitly.eventplatform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import dem.xbitly.eventplatform.databinding.ActivityPublicEventBinding;

public class PublicEventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    int PLACE_PICKER_REQUEST = 1;

    Calendar dateAndTime=Calendar.getInstance();
    String date_time;

    private HashMap <String, Integer> time;

    private ActivityPublicEventBinding binding;

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


        binding.infinityAmountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.eventMaxAmount.setText("Infinity");
                binding.eventMaxAmount.setEnabled(false);

                time.put("max_amount", 0); //если число участников может быть бесконечным, то записываем 0, что означает бесконечность
            }
        });

        binding.pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        binding.pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        binding.nextBtnFromPublicEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.eventNamePublic.getText().toString().isEmpty() || binding.eventMaxAmount.getText().toString().isEmpty() || binding.timeTxt.getText().toString().equals("time") || binding.dateTxt.getText().toString().equals("date")){ //нельзя, чтобы поля пустыми были
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }else {
                    if (binding.eventMaxAmount.getText().toString().equals("Infinity")){
                        time.put("max_amount", 0);
                    }else {
                        time.put("max_amount", Integer.valueOf(binding.eventMaxAmount.getText().toString()));
                    }
                    //если все хорошо, то создаем reference для этого мероприятия
                    ref.child(binding.eventNamePublic.getText().toString()).setValue(time).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Snackbar.make(v, "Successfully", Snackbar.LENGTH_SHORT).show();

                                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                                try {
                                    startActivityForResult(builder.build(PublicEventActivity.this),
                                            PLACE_PICKER_REQUEST);
                                } catch (GooglePlayServicesRepairableException e) {
                                    e.printStackTrace();
                                } catch (GooglePlayServicesNotAvailableException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Snackbar.make(v, "Some errors", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
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
    // установка начальных даты и времени
    private void setInitialDateTime() {

        Toast.makeText(this, DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME).toString(),Toast.LENGTH_LONG).show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            time.put("minute", minute);
            time.put("hour", hourOfDay);

            binding.timeTxt.setText(hourOfDay + ":" + minute);
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

            binding.dateTxt.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {

            if (resultCode == RESULT_OK) {
                Place place = (Place) PlacePicker.getPlace(data, this);
                StringBuilder stringBuilder = new StringBuilder();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                stringBuilder.append("LATITUDE : ");
                stringBuilder.append(latitude);
                stringBuilder.append("\n");
                stringBuilder.append("LONGITUDE");
                stringBuilder.append(longitude);

                ref.child(binding.eventNamePublic.getText().toString()).child("latitude").setValue(latitude);

                Intent intent = new Intent (PublicEventActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}