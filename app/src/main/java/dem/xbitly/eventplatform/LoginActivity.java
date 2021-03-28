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

import dem.xbitly.eventplatform.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();



        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailEditSignIn.getText().toString().isEmpty() && binding.passwordSignIn.getText().toString().isEmpty()){
                    Snackbar.make(v, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(binding.emailEditSignIn.getText().toString(), binding.passwordSignIn.getText().toString())
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

        binding.backFromLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (LoginActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

    }
}