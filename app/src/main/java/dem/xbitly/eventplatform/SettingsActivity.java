package dem.xbitly.eventplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton back_from_settings_btn;
    private ImageButton to_profile_settings;
    private Switch dark_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        back_from_settings_btn = findViewById(R.id.back_from_settings_btn);
        to_profile_settings = findViewById(R.id.to_profile_settings_btn);
        dark_theme = findViewById(R.id.theme_check);

        back_from_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });

        to_profile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileSettings.class));
            }
        });

    }
}