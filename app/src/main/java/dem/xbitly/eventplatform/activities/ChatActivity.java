package dem.xbitly.eventplatform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dem.xbitly.eventplatform.BottomSheetEventDialog;
import dem.xbitly.eventplatform.Message.Message;
import dem.xbitly.eventplatform.Message.MessageAdapter;
import dem.xbitly.eventplatform.databinding.ActivityChatBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    private RecyclerView recView;

    private MessageAdapter adapter;

    private ArrayList<String> messages = new ArrayList<>();

    private int count;

    private FirebaseRecyclerOptions<Message> options;

    private boolean privacy;
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

        if (!getIntent().getBooleanExtra("privacy", true)){
            privacy = false;
        }else{
            privacy = true;
        }


        binding.messageBtnSend.setOnClickListener(v -> {
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
                                        String time = new SimpleDateFormat("HH:mm").format(new Date());
                                        HashMap<String, String> messageInfo = new HashMap<>();
                                        messageInfo.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        messageInfo.put("from", task2.getResult().getValue().toString());
                                        messageInfo.put("text", binding.messageEdit.getText().toString());
                                        messageInfo.put("time", time);
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
        });

        binding.eventInfoBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getApplicationContext(), v);
            popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "About event");
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 0:
                            FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0)))
                                    .child("event_number").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        int event_number = Integer.parseInt(task.getResult().getValue().toString());
                                        HashMap<String, String> eventInfo = new HashMap<>();


                                        if (privacy) {
                                            FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0)))
                                                    .child("members").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        String count = task.getResult().getValue().toString();
                                                        eventInfo.put("count", count);

                                                        FirebaseDatabase.getInstance().getReference("PrivateEvents").child(Integer.toString(event_number))
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                                        eventInfo.put("name", snapshot.child("name").getValue().toString());
                                                                        eventInfo.put("time", snapshot.child("time").getValue().toString());
                                                                        eventInfo.put("date", snapshot.child("date").getValue().toString());
                                                                        eventInfo.put("longitude", snapshot.child("adress").child("longitude").getValue().toString());
                                                                        eventInfo.put("latitude", snapshot.child("adress").child("latitude").getValue().toString());
                                                                        double latitude_d = Double.parseDouble(eventInfo.get("latitude"));
                                                                        double longitude_d = Double.parseDouble(eventInfo.get("longitude"));
                                                                        Geocoder geocoder;
                                                                        List<Address> addresses;
                                                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                                                        try {
                                                                            addresses = geocoder.getFromLocation(latitude_d, longitude_d, 1);

                                                                            String address = addresses.get(0).getAddressLine(0);
                                                                            String city = addresses.get(0).getLocality();
                                                                            String state = addresses.get(0).getAdminArea();
                                                                            String country = addresses.get(0).getCountryName();

                                                                            BottomSheetEventDialog bottomSheetEventDialog = new BottomSheetEventDialog(Integer.toString(event_number), eventInfo.get("name"),
                                                                                    address + ";" + city + ";" + state, eventInfo.get("count"), eventInfo.get("date"), eventInfo.get("time"), false, false);
                                                                            bottomSheetEventDialog.show(getSupportFragmentManager(), "Event info");
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                        }else{
                                            FirebaseDatabase.getInstance().getReference("Chats").child(Integer.toString(getIntent().getIntExtra("chatID", 0)))
                                                    .child("members").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        String count = task.getResult().getValue().toString();
                                                        eventInfo.put("count", count);

                                                        FirebaseDatabase.getInstance().getReference("PublicEvents").child(Integer.toString(event_number))
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                                        eventInfo.put("name", snapshot.child("name").getValue().toString());
                                                                        eventInfo.put("time", snapshot.child("time").getValue().toString());
                                                                        eventInfo.put("date", snapshot.child("date").getValue().toString());
                                                                        eventInfo.put("longitude", snapshot.child("adress").child("longitude").getValue().toString());
                                                                        eventInfo.put("latitude", snapshot.child("adress").child("latitude").getValue().toString());
                                                                        double latitude_d = Double.parseDouble(eventInfo.get("latitude"));
                                                                        double longitude_d = Double.parseDouble(eventInfo.get("longitude"));
                                                                        Geocoder geocoder;
                                                                        List<Address> addresses;
                                                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                                                        try {
                                                                            addresses = geocoder.getFromLocation(latitude_d, longitude_d, 1);

                                                                            String address = addresses.get(0).getAddressLine(0);
                                                                            String city = addresses.get(0).getLocality();
                                                                            String state = addresses.get(0).getAdminArea();
                                                                            String country = addresses.get(0).getCountryName();

                                                                            BottomSheetEventDialog bottomSheetEventDialog = new BottomSheetEventDialog(Integer.toString(event_number), eventInfo.get("name"),
                                                                                    address + ";" + city + ";" + state, eventInfo.get("count"), eventInfo.get("date"), eventInfo.get("time"), true, false);
                                                                            bottomSheetEventDialog.show(getSupportFragmentManager(), "Event info");
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                        }

                                    }
                                }
                            });
                            break;
                    }


                    return false;
                }
            });
        });

        binding.backFromChatBtn.setOnClickListener(view -> {
            onBackPressed();
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