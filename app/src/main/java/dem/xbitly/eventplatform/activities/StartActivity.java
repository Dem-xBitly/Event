package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import dem.xbitly.eventplatform.databinding.ActivityStartBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

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

        checkNetwork();
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (StartActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}