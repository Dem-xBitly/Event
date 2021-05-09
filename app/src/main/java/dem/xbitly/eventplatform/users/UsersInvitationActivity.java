package dem.xbitly.eventplatform.users;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.databinding.ActivityUsersInvitationBinding;
import dem.xbitly.eventplatform.users.User;
import dem.xbitly.eventplatform.users.UserAdapter;

public class UsersInvitationActivity extends AppCompatActivity {
    private UserAdapter adapter;

    private ActivityUsersInvitationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.usersList.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users"), User.class)
                        .build();

        adapter=new UserAdapter(options);
        binding.usersList.addItemDecoration(new DividerItemDecoration(binding.usersList.getContext(), DividerItemDecoration.VERTICAL));
        binding.usersList.setAdapter(adapter);

        binding.inviteUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arr = adapter.getUsers_ids();
                Toast.makeText(getApplicationContext(), arr.size(), Toast.LENGTH_LONG).show();
            }
        });


    }




    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}