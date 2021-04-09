package dem.xbitly.eventplatform;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dem.xbitly.eventplatform.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    private ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toSignInBtn.setOnClickListener(v -> {
            Intent in_intent = new Intent (StartActivity.this, LoginActivity.class);
            startActivity(in_intent);
        });

        binding.toSignUpBtn.setOnClickListener(v -> {
            Intent up_intent = new Intent (StartActivity.this, RegisterActivity.class);
            startActivity(up_intent);
        });
    }
}