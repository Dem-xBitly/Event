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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dem.xbitly.eventplatform.MainActivity;
import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.bottomnav.map.MapFragment;
import dem.xbitly.eventplatform.databinding.ActivityUsersInvitationBinding;
import dem.xbitly.eventplatform.users.User;
import dem.xbitly.eventplatform.users.UserAdapter;

public class UsersInvitationActivity extends AppCompatActivity {
    private UserAdapter adapter;

    private ActivityUsersInvitationBinding binding;


    private String user_name;

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
        binding.usersList.setAdapter(adapter);

        //получаем имя текущего пользователя
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user_name = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        binding.inviteUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arr = adapter.getUsers_ids();
                System.out.println(arr.size());
                for (int i=0; i<arr.size(); ++i){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    String key = FirebaseDatabase.getInstance().getReference("Users").push().getKey(); //генерируем ключ приглашения
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("from").setValue(user_name);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("event_number").setValue(getIntent().getIntExtra("event_number", 0));
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("time").setValue(currentDateandTime);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("event_name").setValue(getIntent().getStringExtra("event_name"));
                    FirebaseDatabase.getInstance().getReference().child("Users").child(arr.get(i)).child("invitations").child(key)
                            .child("accepted").setValue(false);



                    FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(Integer.toString(getIntent().getIntExtra("event_number", 0)))
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