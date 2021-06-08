package dem.xbitly.eventplatform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import dem.xbitly.eventplatform.Message.Message;
import dem.xbitly.eventplatform.Message.MessageAdapter;
import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.chat.Chat;
import dem.xbitly.eventplatform.chat.ChatAdapter;
import dem.xbitly.eventplatform.databinding.ActivityChatBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    private RecyclerView recView;

    private MessageAdapter adapter;

    private ArrayList<String> messages = new ArrayList<>();

    private int count;

    private FirebaseRecyclerOptions<Message> options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recView = binding.messageRecView;
        checkNetwork();

         options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Chats")
                                .child(Integer.toString(getIntent().getIntExtra("chatID", 0))).child("messages").child("all_messages"), Message.class)
                        .build();
        recView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        readMessages();

        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats").child(getIntent().getStringExtra("chatID2"))
                .child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    binding.nameTxt.setText(task.getResult().getValue().toString());
                }
            }
        });


        binding.messageBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.messageEdit.getText().toString().length() != 0){
                    FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0)))
                            .child("messages").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                count = Integer.parseInt(task.getResult().getValue().toString());
                                count++;
                                FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0)))
                                        .child("messages").child("count").setValue(count);
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task2) {
                                        if (task2.isSuccessful()){
                                            HashMap<String, String> messageInfo = new HashMap<>();
                                            messageInfo.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            messageInfo.put("from", task2.getResult().getValue().toString());
                                            messageInfo.put("text", binding.messageEdit.getText().toString());
                                            FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0)))
                                                    .child("messages").child("all_messages").child(Integer.toString(count)).setValue(messageInfo);
                                            binding.messageEdit.setText("");
                                            recView.scrollToPosition(adapter.getItemCount()-1);
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

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(ChatActivity.this)){
            Intent in_intent = new Intent (ChatActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void readMessages(){
        FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0))).child("messages").child("all_messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Message message = snapshot1.getValue(Message.class);
                    messages.add(message.getUserID());
                }
                adapter = new MessageAdapter(options, messages);
                adapter.startListening();
                recView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}