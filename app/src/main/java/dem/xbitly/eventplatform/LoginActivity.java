package dem.xbitly.eventplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ImageButton back_from_login;
    private EditText email_edit;
    private EditText password_edit;
    private Button sign_in_btn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        back_from_login = findViewById(R.id.back_from_login_btn);
        email_edit= findViewById(R.id.email_edit_sign_in);
        password_edit = findViewById(R.id.password_sign_in);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        mAuth = FirebaseAuth.getInstance();



        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_edit.getText().toString().isEmpty() && password_edit.getText().toString().isEmpty()){
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(email_edit.getText().toString(), password_edit.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Snackbar.make(v, "Error", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}