package dem.xbitly.eventplatform;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dem.xbitly.eventplatform.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backFromSettingsBtn.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, MainActivity.class)));

        binding.toProfileSettingsBtn.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, ProfileSettings.class)));


    }
}