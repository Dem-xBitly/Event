package dem.xbitly.eventplatform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import dem.xbitly.eventplatform.databinding.ActivityEventDescriptionBinding;
import dem.xbitly.eventplatform.users.UsersInvitationActivity;

public class EventDescriptionActivity extends AppCompatActivity {

    private ActivityEventDescriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.nextFromEventDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.eventDesc.getText().toString().length() != 0){
                    FirebaseDatabase.getInstance().getReference("PublicEvents")
                            .child(Integer.toString(getIntent().getIntExtra("event_number", 0)))
                            .child("description").setValue(binding.eventDesc.getText().toString());
//                    Intent intent = new Intent (EventDe.this, EventDescriptionActivity.class);
//                    intent.putExtra("event_number", event_number);
//                    intent.putExtra("event_name", binding.eventNamePrivate.getText().toString());
//                    startActivity(intent);

                    Intent intent = new PlacePicker.IntentBuilder()
                            .setLatLong(40.748672, -73.985628)
                            .showLatLong(true)
                            .setMapType(MapType.NORMAL)
                            .setFabColor(R.color.blue)
                            .setMarkerDrawable(R.drawable.ic_map_marker)
                            .build(EventDescriptionActivity.this);

                    startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
                }else{
                    Snackbar.make(v, "Field cannot be empty", Snackbar.LENGTH_SHORT).show();
                }
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
                        .child(Integer.toString(getIntent().getIntExtra("event_number", 0)))
                        .child("adress").child("latitude").setValue(latitude);
                FirebaseDatabase.getInstance().getReference("PublicEvents")
                        .child(Integer.toString(getIntent().getIntExtra("event_number", 0)))
                        .child("adress").child("longitude").setValue(longitude);

                Intent intent = new Intent (EventDescriptionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}