package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import dem.xbitly.eventplatform.activities.MainActivity;
import dem.xbitly.eventplatform.databinding.ActivityUsersInvitationBinding;
import dem.xbitly.eventplatform.network.NetworkManager;
import dem.xbitly.eventplatform.users.User;
import dem.xbitly.eventplatform.users.UserAdapter;

public class UsersInvitationActivity extends AppCompatActivity {
    private UserAdapter adapter;

    private ActivityUsersInvitationBinding binding;


    private String user_name;

    private int event_chat_num;
    private int user_chat_count_num;

    private String event_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNetwork();

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
                FirebaseDatabase.getInstance().getReference("Chats").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            event_chat_num = Integer.parseInt(task.getResult().getValue().toString());
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("count")
                                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        user_chat_count_num = Integer.parseInt(task.getResult().getValue().toString());
                                        writeDataToDB1(event_chat_num, user_chat_count_num);
                                    }
                                }
                            });
                        }
                    }
                });

                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }


        });




    }

    public void writeDataToDB1(int a, int b){
        a++;
        b++;

        FirebaseDatabase.getInstance().getReference("PrivateEvents").child(Integer.toString(getIntent().getIntExtra("event_number", 0))).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    event_name = task.getResult().getValue().toString();
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats").child(String.valueOf(++user_chat_count_num)).child("name").setValue(event_name);
                }
            }
        });
        FirebaseDatabase.getInstance().getReference("Chats").child("count").setValue(a);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("count").setValue(b);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats").child(String.valueOf(b)).child("chatID").setValue(a);
        FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(a)).child("event_number").setValue(Integer.toString(getIntent().getIntExtra("event_number", 0)));
        FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(a)).child("members").child("count").setValue(1);
        FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(a)).child("members").child("1").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(a)).child("messages").child("count").setValue(1);
        FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(a)).child("messages").child("all_messages").child("1")
                .child("from").setValue("App");
        FirebaseDatabase.getInstance().getReference("Chats").child(String.valueOf(a)).child("messages").child("all_messages").child("1")
                .child("text").setValue("Welcome to the chat of the event. Please, be polite to the rest of the chat and have a pleasant discussion!");

        FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(Integer.toString(getIntent().getIntExtra("event_number", 0)))
                .child("chatID").setValue(a);
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (UsersInvitationActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
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