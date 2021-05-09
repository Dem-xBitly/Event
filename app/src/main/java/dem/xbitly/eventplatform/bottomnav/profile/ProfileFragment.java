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

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.SettingsActivity;
import dem.xbitly.eventplatform.StartActivity;
import dem.xbitly.eventplatform.tape.TapeAdapter;
import dem.xbitly.eventplatform.tape.TapeHolder;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;

    private ImageButton logout_btn;
    private ImageButton settings_btn;
    private RecyclerView rv;

    private TextView profile_name;

    private FirebaseDatabase dBase;
    private DatabaseReference ref;

    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        dBase = FirebaseDatabase.getInstance();
        ref = dBase.getReference("Users");
        profile_name = root.findViewById(R.id.profile_name);
        rv = root.findViewById(R.id.profile_posts_recycler);
//        Toast.makeText(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("name").getValue().toString();
                profile_name.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout_btn = root.findViewById(R.id.logout_btn);
        settings_btn = root.findViewById(R.id.settings_btn);
//        profile_name.setText(username);
//        Toast.makeText(getContext(), username, Toast.LENGTH_SHORT).show();
//        profile_name.setText(username);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent (getContext(), StartActivity.class);
                startActivity(intent);
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
        TapeAdapter tapeAdapter = new TapeAdapter(rv);
        rv.setAdapter(tapeAdapter);

        return root;
    }
}