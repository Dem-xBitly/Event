package dem.xbitly.eventplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import dem.xbitly.eventplatform.databinding.ActivityRegisterBinding;

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

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.backFromRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent (RegisterActivity.this, StartActivity.class);
                startActivity(startIntent);
            }
        });
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.usernameSignUp.getText().toString().isEmpty() || binding.emailEdit.getText().toString().isEmpty() || binding.passwordSignUp.getText().toString().isEmpty() || (!binding.maleCheckRegister.isChecked() && !binding.femaleCheckRegister.isChecked())){
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(binding.emailEdit.getText().toString(),  binding.passwordSignUp.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Snackbar.make(v, "User successfully created", Snackbar.LENGTH_SHORT).show();
                                        ref = database.getReference("Users");

                                        String gender = binding.maleCheckRegister.isChecked() ? "male" : "female";

                                        HashMap<String, String> userMap = new HashMap<>();
                                        userMap.put ("username", binding.usernameSignUp.getText().toString());
                                        userMap.put ("email", binding.emailEdit.getText().toString());
                                        userMap.put ("password", binding.passwordSignUp.getText().toString());
                                        userMap.put("gender", gender);

                                        ref.child(mAuth.getCurrentUser().getUid().toString()).setValue(userMap);


                                        Intent intent = new Intent (RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        // ...
                                    } else {
                                        Snackbar.make(v, "Some Error", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}