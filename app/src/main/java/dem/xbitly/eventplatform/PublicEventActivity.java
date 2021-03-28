package dem.xbitly.eventplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
        ref = database.getReference("PublicEvents").child(mAuth.getCurrentUser().getUid()); //папка со всеми созданными евентами этого человека


        binding.infinityAmountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.eventMaxAmount.setText("Infinity");
                binding.eventMaxAmount.setEnabled(false);

                time.put("max_amount", 0); //если число участников может быть бесконечным, то записываем 0, что означает бесконечность
            }
        });

        binding.nextBtnFromPublicEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.eventNamePublic.getText().toString().isEmpty() && binding.eventMaxAmount.getText().toString().isEmpty()){ //нельзя, чтобы поля пустыми были
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }else { //если все хорошо, то создаем reference для этого мероприятия
                    ref.child(binding.eventNamePublic.getText().toString()).setValue("null").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            time.put("max_amount", Integer.valueOf(binding.eventMaxAmount.getText().toString()));
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

            setDate();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            time.put("day", dayOfMonth);
            time.put("month", monthOfYear);
            time.put("year", year);

            ref.child(binding.eventNamePublic.getText().toString()).setValue(time)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent (PublicEventActivity.this, PickPlaceActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(PublicEventActivity.this, "Some error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };
}