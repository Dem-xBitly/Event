package dem.xbitly.eventplatform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dem.xbitly.eventplatform.databinding.ActivityLoginBinding;
import dem.xbitly.eventplatform.databinding.ActivityMembersBinding;
import dem.xbitly.eventplatform.members.MembersAdapter;

public class MembersActivity extends AppCompatActivity {

    ActivityMembersBinding binding;
    String id[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backFromChatBtn.setOnClickListener(view -> onBackPressed());

        String path;
        if(getIntent().getBooleanExtra("private", false)){
            path = "PrivateEvents";
        } else {
            path = "PublicEvents";
        }

        FirebaseDatabase.getInstance().getReference(path).child(getIntent().getStringExtra("eventID")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String go = Objects.requireNonNull(snapshot.child("go").getValue()).toString();
                id = go.split(",");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MembersActivity.this);
                binding.membersRv.setLayoutManager(linearLayoutManager);
                MembersAdapter membersAdapter = new MembersAdapter(id);
                binding.membersRv.setAdapter(membersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}