package dem.xbitly.eventplatform.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import dem.xbitly.eventplatform.databinding.ActivityPublicEventBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class PublicEventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    Calendar dateAndTime = Calendar.getInstance();

        private HashMap<String, String> event_info;

    private ActivityPublicEventBinding binding;

    private int event_number;//номер евента

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublicEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkNetwork();

        event_info = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ref = database.getReference("PublicEvents");
        ref.addValueEventListener(new ValueEventListener(){


            boolean a = true;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    event_number = Integer.parseInt(snapshot.child("count").getValue().toString()) + 1;
                    ref = database.getReference("PublicEvents").child(String.valueOf(event_number));
                    if(a){
                        snapshot.getRef().child("count").setValue(event_number);
                        a = false;
                    }
                }catch(Exception e){
                    event_number = 1;
                    ref = database.getReference("PublicEvents").child(String.valueOf(event_number));
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

        //раздел со всеми созданными евентами этого человека

        binding.infinityAmountBtn.setOnClickListener(v -> {
            binding.eventMaxAmount.setText("Infinity");
            binding.eventMaxAmount.setEnabled(false);

            event_info.put("max_amount", "0"); //если число участников может быть бесконечным, то записываем 0, что означает бесконечность
        });

        binding.pickDateBtn.setOnClickListener(v -> setDate());

        binding.pickTimeBtn.setOnClickListener(v -> setTime());

        binding.nextBtnFromPublicEventBtn.setOnClickListener(v -> {

            if (binding.eventNamePublic.getText().toString().isEmpty() || binding.eventMaxAmount.getText().toString().isEmpty()
                    || binding.eventTime.getText().toString().isEmpty() || binding.eventDate.getText().toString().isEmpty()) { //нельзя, чтобы поля пустыми были
                Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
            } else {
                if (binding.eventMaxAmount.getText().toString().equals("Infinity")) {
                    event_info.put("max_amount", "0");
                } else {
                    event_info.put("max_amount", binding.eventMaxAmount.getText().toString());
                }
                event_info.put("name", binding.eventNamePublic.getText().toString());
                event_info.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                //если все хорошо, то создаем reference для этого мероприятия
                ref.setValue(event_info).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(v, "Successfully", Snackbar.LENGTH_SHORT).show();

                        Intent intent = new Intent (PublicEventActivity.this, EventDescriptionActivity.class);
                        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        intent.putExtra("eventID", event_number);
                        intent.putExtra("event_name", binding.eventNamePublic.getText().toString());

                        startActivity(intent);
                    } else {
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

            binding.eventDate.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
        }
    };

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (PublicEventActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }

}