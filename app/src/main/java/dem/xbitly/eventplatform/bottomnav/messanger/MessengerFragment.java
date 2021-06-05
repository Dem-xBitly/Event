package dem.xbitly.eventplatform.bottomnav.messanger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.activities.InternetErrorConnectionActivity;
import dem.xbitly.eventplatform.chat.Chat;
import dem.xbitly.eventplatform.chat.ChatAdapter;
import dem.xbitly.eventplatform.network.NetworkManager;
import dem.xbitly.eventplatform.users.User;

public class MessengerFragment extends Fragment {

    private MessengerViewModel messengerViewModel;

    private RecyclerView recView;

    private ChatAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messengerViewModel =
                new ViewModelProvider(this).get(MessengerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_messenger, container, false);

        checkNetwork();

        recView = root.findViewById(R.id.chatsRecView);
        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats"), Chat.class)
                        .build();
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter(options);
        recView.setAdapter(adapter);


        return root;
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this.getContext())){
            Intent in_intent = new Intent (this.getContext(), InternetErrorConnectionActivity.class);
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