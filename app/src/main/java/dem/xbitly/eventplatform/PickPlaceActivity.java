package dem.xbitly.eventplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;

import dem.xbitly.eventplatform.databinding.ActivityPickPlaceBinding;

public class PickPlaceActivity extends AppCompatActivity {

    private ActivityPickPlaceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

//    private void getLocationPermission(){
//        mLocationPermissionGranted = false;
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
}