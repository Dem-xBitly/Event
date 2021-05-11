package dem.xbitly.eventplatform.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dem.xbitly.eventplatform.MainActivity;
import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.bottomnav.map.MapFragment;
import dem.xbitly.eventplatform.databinding.ActivityUsersInvitationBinding;
import dem.xbitly.eventplatform.users.User;
import dem.xbitly.eventplatform.users.UserAdapter;

public class UsersInvitationActivity extends AppCompatActivity {
    private UserAdapter adapter;

    private ActivityUsersInvitationBinding binding;

    private int event_name;

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

        FirebaseDatabase.getInstance().getReference("PublicEvents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                event_name = Integer.parseInt(snapshot.child("count").getValue().toString()) + 1; //получаем номер мероприятия, которое создаем
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        binding.inviteUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arr = adapter.getUsers_ids();
//                Toast.makeText(UsersInvitationActivity.this, arr.size(), Toast.LENGTH_LONG).show();
                System.out.println(arr.size());
                for (int i=0; i<arr.size(); ++i){

                    String key = FirebaseDatabase.getInstance().getReference("Users").push().getKey(); //генерируем ключ приглашения
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("from").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("event_name").setValue(event_name);
                    FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(Integer.toString(event_name))
                            .child("invited").child(Integer.toString(i)).setValue(arr.get(i));

                }

                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                startActivity(intent);
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