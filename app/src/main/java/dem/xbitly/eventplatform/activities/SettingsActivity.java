package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dem.xbitly.eventplatform.databinding.ActivitySettingsBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

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

        checkNetwork();
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (SettingsActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}