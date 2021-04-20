package dem.xbitly.eventplatform;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        binding.maleCheck.setOnClickListener(v -> binding.femaleCheck.setChecked(false));

        binding.femaleCheck.setOnClickListener(v -> binding.maleCheck.setChecked(false));


        binding.backFromProfileSettingsBtn.setOnClickListener(v -> startActivity(new Intent(ProfileSettings.this, SettingsActivity.class)));


        binding.applyChangesProfileSettingsBtn.setOnClickListener(v -> {
            if (binding.usernameProfileSettings.getText().toString().isEmpty() || (!binding.maleCheck.isChecked() && !binding.femaleCheck.isChecked())){
                Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
            }
            else {
                String gender = binding.maleCheck.isChecked() ? "male" : "female";
                ref.child("gender").setValue(gender);
                ref.child("username").setValue( binding.usernameProfileSettings.getText().toString());
                startActivity(new Intent (ProfileSettings.this, SettingsActivity.class));
            }
        });
    }
}