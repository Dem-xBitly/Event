package dem.xbitly.eventplatform.bottomnav.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.notifications.Notification;
import dem.xbitly.eventplatform.notifications.NotificationAdapter;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel messengerViewModel;

    private NotificationAdapter adapter;

    private RecyclerView recView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        messengerViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        recView = root.findViewById(R.id.notifications_recView);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<Notification> options =
                new FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invitations"), Notification.class)
                .build();



        adapter = new NotificationAdapter(options);
        recView.setAdapter(adapter);




        return root;
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