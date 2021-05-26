package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.databinding.ActivityMainBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        checkNetwork();

        mAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        } else {
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile, R.id.navigation_message, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (MainActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }

}