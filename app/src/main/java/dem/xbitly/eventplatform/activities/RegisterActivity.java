package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import dem.xbitly.eventplatform.databinding.ActivityRegisterBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference ref;

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNetwork();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.backFromRegisterBtn.setOnClickListener(v -> {
            Intent startIntent = new Intent (RegisterActivity.this, StartActivity.class);
            startActivity(startIntent);
        });
        binding.signUpBtn.setOnClickListener(v -> {
            if (binding.usernameSignUp.getText().toString().isEmpty() || binding.emailEdit.getText().toString().isEmpty() || binding.passwordSignUp.getText().toString().isEmpty() || (!binding.maleCheckRegister.isChecked() && !binding.femaleCheckRegister.isChecked())){
                Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
            }else {
                mAuth.createUserWithEmailAndPassword(binding.emailEdit.getText().toString(),  binding.passwordSignUp.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Snackbar.make(v, "User successfully created", Snackbar.LENGTH_SHORT).show();
                                ref = database.getReference("Users");

                                String gender = binding.maleCheckRegister.isChecked() ? "male" : "female";

                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put ("name", binding.usernameSignUp.getText().toString());
                                userMap.put ("email", binding.emailEdit.getText().toString());
                                userMap.put ("password", binding.passwordSignUp.getText().toString());
                                userMap.put("gender", gender);

                                ref.child(mAuth.getCurrentUser().getUid()).setValue(userMap);


                                Intent intent = new Intent (RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                // ...
                            } else {
                                Snackbar.make(v, "Some Error: " + task.getException(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (RegisterActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}