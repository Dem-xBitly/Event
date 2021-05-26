package dem.xbitly.eventplatform.bottomnav.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.activities.InternetErrorConnectionActivity;
import dem.xbitly.eventplatform.activities.SettingsActivity;
import dem.xbitly.eventplatform.activities.StartActivity;
import dem.xbitly.eventplatform.network.NetworkManager;
import dem.xbitly.eventplatform.tape.TapeAdapter;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;

    private RecyclerView rv;

    private TextView profile_name;

    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        checkNetwork();

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();
        DatabaseReference ref = dBase.getReference("Users");
        profile_name = root.findViewById(R.id.profile_name);
        rv = root.findViewById(R.id.profile_posts_recycler);


        ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                username = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                profile_name.setText(username);

                try {
                    String[] sR = Objects.requireNonNull(snapshot.child("myReviews").getValue()).toString().split(",");
                    String[] sI = Objects.requireNonNull(snapshot.child("myInvites").getValue()).toString().split(",");
                    rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
                    TapeAdapter tapeAdapter = new TapeAdapter(sR, sI, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), root.getContext());
                    rv.setAdapter(tapeAdapter);
                } catch (Exception e) {
                    try {
                        String[] sR = new String[0];
                        String[] sI = Objects.requireNonNull(snapshot.child("myInvites").getValue()).toString().split(",");
                        rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
                        TapeAdapter tapeAdapter = new TapeAdapter(sR, sI, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), root.getContext());
                        rv.setAdapter(tapeAdapter);
                    } catch (Exception e2) {
                        String[] sR = new String[0];
                        String[] sI = new String[0];
                        rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
                        TapeAdapter tapeAdapter = new TapeAdapter(sR, sI, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), root.getContext());
                        rv.setAdapter(tapeAdapter);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageButton logout_btn = root.findViewById(R.id.logout_btn);
        ImageButton settings_btn = root.findViewById(R.id.settings_btn);

        logout_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent (getContext(), StartActivity.class);
            startActivity(intent);
        });

        settings_btn.setOnClickListener(v -> {
            Intent intent = new Intent (getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        return root;
    }
    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this.getContext())){
            Intent in_intent = new Intent (this.getContext(), InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}