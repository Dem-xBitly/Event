package dem.xbitly.eventplatform.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.databinding.ActivityEventDescriptionBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class EventDescriptionActivity extends AppCompatActivity {

    private ActivityEventDescriptionBinding binding;
    private DatabaseReference ref, ref2;
    boolean r = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNetwork();

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();
        ref = dBase.getReference("Invite");
        ref2 = dBase.getReference("Users/"+getIntent().getSerializableExtra("userID").toString());

        binding.nextBtnFromEventdiskBtn.setOnClickListener(v -> {
            if (!binding.eventDesc.getText().toString().isEmpty()){
                Date date = new Date();
                SimpleDateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat formatForTime = new SimpleDateFormat("hh:mm");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(r) {
                            int count = 0;
                            try {
                                count = Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString());
                            } catch (Exception e) {
                                ref.child("count").setValue(0);
                            }

                            ref.child("count").setValue((count + 1));
                            ref.child(String.valueOf((count + 1))).child("date").setValue(formatForDate.format(date));
                            ref.child(String.valueOf((count + 1))).child("time").setValue(formatForTime.format(date));
                            ref.child(String.valueOf((count + 1))).child("text").setValue(binding.eventDesc.getText().toString());
                            ref.child(String.valueOf((count + 1))).child("userID").setValue(getIntent().getSerializableExtra("userID").toString());
                            ref.child(String.valueOf((count + 1))).child("eventID").setValue(getIntent().getSerializableExtra("eventID").toString());

                            int finalCount = count;

                            ref2.addValueEventListener(new ValueEventListener() {

                                boolean e = true;

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    String str;
                                    if(e) {
                                        if (Objects.requireNonNull(snapshot2.child("myInvites").getValue()).toString().equals("")){
                                            str = Objects.requireNonNull(snapshot2.child("myInvites").getValue()).toString() + (finalCount + 1);
                                        } else {
                                            str = Objects.requireNonNull(snapshot2.child("myInvites").getValue()).toString() + "," + (finalCount + 1);
                                        }
                                        ref2.child("myInvites").setValue(str);
                                        e = false;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            r = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent intent = new PlacePicker.IntentBuilder()
                        .setLatLong(40.748672, -73.985628)
                        .showLatLong(true)
                        .setMapType(MapType.NORMAL)
                        .setFabColor(R.color.blue)
                        .setMarkerDrawable(R.drawable.ic_location_marker)
                        .build(EventDescriptionActivity.this);

                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);

            }else{
                Snackbar.make(v, "Field cannot be empty", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                double latitude = addressData.getLatitude();
                double longitude = addressData.getLongitude();
                FirebaseDatabase.getInstance().getReference("PublicEvents")
                        .child(Integer.toString(getIntent().getIntExtra("eventID", 0)))
                        .child("adress").child("latitude").setValue(latitude);
                FirebaseDatabase.getInstance().getReference("PublicEvents")
                        .child(Integer.toString(getIntent().getIntExtra("eventID", 0)))
                        .child("adress").child("longitude").setValue(longitude);

                Intent intent = new Intent (EventDescriptionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (EventDescriptionActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}