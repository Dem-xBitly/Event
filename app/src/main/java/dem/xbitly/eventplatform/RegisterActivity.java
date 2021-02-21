package dem.xbitly.eventplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton back_to_start;
    private Button sign_up;
    private EditText username_edit;
    private EditText email_edit;
    private EditText password_edit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        back_to_start = findViewById(R.id.back_from_register_btn);
        sign_up = findViewById(R.id.sign_up_btn);
        username_edit = findViewById(R.id.username_sign_up);
        email_edit = findViewById(R.id.email_edit);
        password_edit = findViewById(R.id.password_sign_up);

        back_to_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent (RegisterActivity.this, StartActivity.class);
                startActivity(startIntent);
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_edit.getText().toString().isEmpty() || email_edit.getText().toString().isEmpty() || password_edit.getText().toString().isEmpty()){
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(email_edit.getText().toString(),  password_edit.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Snackbar.make(v, "User successfully created", Snackbar.LENGTH_SHORT).show();
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