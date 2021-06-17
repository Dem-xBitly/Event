package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

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

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Uri deepLink = null;
                if (pendingDynamicLinkData != null){
                    deepLink = pendingDynamicLinkData.getLink();
                }
                if (deepLink != null){
                    String event_number_in_private_events = deepLink.getQueryParameter("eventID");
                    FirebaseDatabase.getInstance().getReference("PrivateEvents").child(event_number_in_private_events).child("go").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                String go = task.getResult().getValue().toString();
                                if (!go.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    FirebaseDatabase.getInstance().getReference("PrivateEvents").child(event_number_in_private_events).child("go")
                                            .setValue(go + "," + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("UserPrivateEvents").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                            if (task.isSuccessful()){
                                                int count = Integer.parseInt(task.getResult().getValue().toString());
                                                count++;
                                                String eventId = Integer.toString(count);
                                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("UserPrivateEvents").child(eventId).child("eventID").setValue(event_number_in_private_events);
                                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("UserPrivateEvents").child(eventId).child("privacy").setValue("yes");
                                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("UserPrivateEvents").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            int count_o = Integer.parseInt(task.getResult().getValue().toString());
                                                            count_o++;
                                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child("UserPrivateEvents").child("count").setValue(count_o);

                                                            FirebaseDatabase.getInstance().getReference("PrivateEvents").child(eventId).child("name")
                                                                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                                    if (task.isSuccessful()){
                                                                        String name = task.getResult().getValue().toString();
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats")
                                                                                .child(eventId).child("name").setValue(name);
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats")
                                                                                .child(eventId).child("privacy").setValue("yes");
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats")
                                                                                .child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                                                if (task.isSuccessful()){
                                                                                    int count_j = Integer.parseInt(task.getResult().getValue().toString());
                                                                                    count_j++;
                                                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats")
                                                                                            .child("count").setValue(count_j);

                                                                                    FirebaseDatabase.getInstance().getReference("PrivateEvents").child(event_number_in_private_events).child("chatID").get()
                                                                                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                                                                    if (task.isSuccessful()){
                                                                                                        String chatID = task.getResult().getValue().toString();
                                                                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats")
                                                                                                                .child(eventId).child("chatID").setValue(chatID);
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });


                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                System.out.println("ERROR!!!!");
            }
        });
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (MainActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }

}