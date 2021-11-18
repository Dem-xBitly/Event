package dem.xbitly.eventplatform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.bottomnav.home.HomeFragment;
import dem.xbitly.eventplatform.tape.TapeAdapter;

public class SplashScreen extends AppCompatActivity {

    private boolean isUpdateRV = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent = new Intent(SplashScreen.this, StartActivity.class);
            startActivity(intent);
        }

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();
        DatabaseReference ref = dBase.getReference("Reviews");
        DatabaseReference ref2 = dBase.getReference("Invite");

        updateRecycler(ref, ref2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNetwork();
    }

    private void updateRecycler(DatabaseReference ref, DatabaseReference ref2){

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> s = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString()); i++) {
                    s.add((i+1)+"");
                }

                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> s1 = new ArrayList<>();

                        for (int i = 0; i < Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString()); i++) {
                            s1.add((i+1)+"");
                        }

                        String[] ss = s.toArray(new String[0]);
                        String[] ss1 = s1.toArray(new String[0]);

                        SharedPreferences prefs = getSharedPreferences("App", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("fromSplash", true);

                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.putExtra("ss", ss);
                        intent.putExtra("ss1", ss1);
                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle();
                        startActivity(intent, b);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkNetwork(){
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            if (!connected) {
                Intent in_intent = new Intent (getApplicationContext(), InternetErrorConnectionActivity.class);
                startActivity(in_intent);
            }
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
    }
}