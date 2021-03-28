package dem.xbitly.eventplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dem.xbitly.eventplatform.databinding.ActivityEventPrivateBinding;

public class PrivateEventActivity extends AppCompatActivity {

    private ActivityEventPrivateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventPrivateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}