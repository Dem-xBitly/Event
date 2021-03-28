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

import dem.xbitly.eventplatform.databinding.ActivityProfileSettingsBinding;

public class ProfileSettings extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    private ActivityProfileSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

        binding.maleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.femaleCheck.setChecked(false);
            }
        });

        binding.femaleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.maleCheck.setChecked(false);
            }
        });


        binding.backFromProfileSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSettings.this, SettingsActivity.class));
            }
        });


        binding.applyChangesProfileSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.usernameProfileSettings.getText().toString().isEmpty() || (!binding.maleCheck.isChecked() && !binding.femaleCheck.isChecked())){
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    String gender = binding.maleCheck.isChecked() ? "male" : "female";
                    ref.child("gender").setValue(gender);
                    ref.child("username").setValue( binding.usernameProfileSettings.getText().toString());
                    startActivity(new Intent (ProfileSettings.this, SettingsActivity.class));
                }
            }
        });
    }
}