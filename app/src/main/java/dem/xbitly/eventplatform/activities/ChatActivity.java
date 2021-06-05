package dem.xbitly.eventplatform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.chat.Chat;
import dem.xbitly.eventplatform.chat.ChatAdapter;
import dem.xbitly.eventplatform.databinding.ActivityChatBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    private RecyclerView recView;

    private ChatAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recView = binding.messageRecView;
        checkNetwork();

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Chats")
                                .child(Integer.toString(getIntent().getIntExtra("chatID", 0))).child("messages").child("all_messages"), Chat.class)
                        .build();
        recView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        adapter = new ChatAdapter(options);
        recView.setAdapter(adapter);

    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(ChatActivity.this)){
            Intent in_intent = new Intent (ChatActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}