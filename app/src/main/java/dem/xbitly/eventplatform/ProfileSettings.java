package dem.xbitly.eventplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileSettings extends AppCompatActivity {

    private ImageButton back_from_profile_settings;
    private Button apply_changes;
    private EditText new_username;
    private CheckBox male_check;
    private CheckBox female_check;

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        back_from_profile_settings = findViewById(R.id.back_from_profile_settings_btn);
        apply_changes = findViewById(R.id.apply_changes_profile_settings_btn);
        new_username = findViewById(R.id.username_profile_settings);
        male_check = findViewById(R.id.male_check);
        female_check = findViewById(R.id.female_check);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

        male_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female_check.setChecked(false);
            }
        });

        female_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male_check.setChecked(false);
            }
        });


        back_from_profile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSettings.this, SettingsActivity.class));
            }
        });


        apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new_username.getText().toString().isEmpty() || (!male_check.isChecked() && !female_check.isChecked())){
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    String gender = male_check.isChecked() ? "male" : "female";
                    ref.child("gender").setValue(gender);
                    ref.child("username").setValue( new_username.getText().toString());
                    startActivity(new Intent (ProfileSettings.this, SettingsActivity.class));
                }
            }
        });
    }
}