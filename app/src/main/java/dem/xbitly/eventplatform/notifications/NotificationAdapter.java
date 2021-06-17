package dem.xbitly.eventplatform.notifications;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dem.xbitly.eventplatform.BottomSheetEventDialog;
import dem.xbitly.eventplatform.R;

public class NotificationAdapter extends FirebaseRecyclerAdapter<Notification, NotificationAdapter.viewholder>
{

    DatabaseReference ref, ref2;
    int event_number;
    String name, userID;
    int day, month, year, hour, minute;
    double longitude, latitude;
    boolean have_event_number=false;
    private final Context context;
    FragmentManager fragmentManager;

    int event_number_in_private_events;

    int countt;


    public NotificationAdapter(@NonNull FirebaseRecyclerOptions<Notification> options, Context context, FragmentManager fragmentManager) {
        super(options);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull NotificationAdapter.viewholder holder, int i, @NonNull @NotNull Notification model) {
        holder.from.setText( model.getFrom() + " invited you to the event: ");
        holder.event_name.setText(model.getEvent_name());

        ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserPrivateEvents");

        holder.event_name.setOnClickListener(view -> {
            String key = getRef(i).getKey();
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("invitations").child(key).child("event_number").get().addOnCompleteListener(task -> {
                        String eventID = task.getResult().getValue().toString();
                FirebaseDatabase dBase = FirebaseDatabase.getInstance();
                ref2 = dBase.getReference("PrivateEvents").child(eventID);
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String go = Objects.requireNonNull(snapshot.child("go").getValue()).toString();
                        String text = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        String count_bs = Integer.toString(go.split(",").length-1);
                        String time = Objects.requireNonNull(snapshot.child("time").getValue()).toString();
                        String date = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                        double latitude_d = Double.parseDouble(Objects.requireNonNull(snapshot.child("adress").child("latitude").getValue()).toString());
                        double longitude_d = Double.parseDouble(Objects.requireNonNull(snapshot.child("adress").child("longitude").getValue()).toString());

                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses;

                        try {
                            addresses = geocoder.getFromLocation(latitude_d, longitude_d, 1);

                            String address = addresses.get(0).getAddressLine(0);

                            BottomSheetEventDialog bottomSheetEventDialog = new BottomSheetEventDialog(eventID, text, address, count_bs, date, time, false, true);
                            bottomSheetEventDialog.show(fragmentManager, "Event info");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                    });
        });

        String key = getRef(i).getKey();
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invitations").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                event_number_in_private_events = Integer.parseInt(snapshot.child("event_number").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("invitations").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String[] date = snapshot.child("time").getValue().toString().split("-");
                holder.time_ago.setText(model.timeAgo(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])));



                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invitations").child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (Boolean.parseBoolean(snapshot.child("accepted").getValue().toString())){
                                    holder.ill_go_btn.setText("Refuse");
                                    holder.ill_go_btn.setBackgroundResource(R.drawable.go_btn_bg_success);
                                } else {
                                    holder.ill_go_btn.setText("I will go");
                                    holder.ill_go_btn.setBackgroundResource(R.drawable.go_btn_bg);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                holder.ill_go_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (holder.ill_go_btn.getText().toString().equals("Refuse")){
                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invitations").child(key)
                                    .child("accepted").setValue(true);
                            String key = getRef(i).getKey();
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("invitations").child(key).child("event_number").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String eventID = task.getResult().getValue().toString();
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserPrivateEvents")
                                                .child(eventID).setValue(null);
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserPrivateEvents")
                                                .child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    int count9 = Integer.parseInt(task.getResult().getValue().toString());
                                                    count9--;
                                                    FirebaseDatabase.getInstance().getReference("Users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserPrivateEvents")
                                                            .child("count").setValue(count9);
                                                }
                                            }
                                        });

                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats")
                                                .child(eventID).setValue(null);
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("count")
                                                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    int count_p = Integer.parseInt(task.getResult().getValue().toString());
                                                    count_p--;
                                                    FirebaseDatabase.getInstance().getReference("Users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("count")
                                                            .setValue(count_p);
                                                }
                                            }
                                        });

                                        FirebaseDatabase.getInstance().getReference("PrivateEvents").child(eventID).child("go")
                                                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    String go = task.getResult().getValue().toString();
                                                    FirebaseDatabase.getInstance().getReference("PrivateEvents").child(eventID).child("go")
                                                            .setValue(go.replace(","+FirebaseAuth.getInstance().getCurrentUser().getUid(), ""));
                                                }
                                            }
                                        });

                                        holder.ill_go_btn.setText("I will go");
                                        holder.ill_go_btn.setBackgroundResource(R.drawable.go_btn_bg);


                                    }
                                }
                            });
                        }else{
                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invitations").child(key)
                                    .child("accepted").setValue(false);
                            String key = getRef(i).getKey();
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("invitations").child(key).child("event_number").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String eventId = task.getResult().getValue().toString();
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("UserPrivateEvents").child(eventId).child("eventID").setValue(event_number_in_private_events);
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("UserPrivateEvents").child(eventId).child("privacy").setValue("yes");
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("UserPrivateEvents").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    int count_o = Integer.parseInt(task.getResult().getValue().toString());
                                                    count_o++;
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("UserPrivateEvents").child("count").setValue(count_o);
                                                }
                                            }
                                        });
                                        FirebaseDatabase.getInstance().getReference("PrivateEvents").child(eventId).child("name")
                                                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    String name = task.getResult().getValue().toString();
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats")
                                                            .child(eventId).child("name").setValue(name);
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child("chats")
                                                            .child(eventId).child("privacy").setValue("yes");
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats")
                                                            .child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                int count_j = Integer.parseInt(task.getResult().getValue().toString());
                                                                count_j++;
                                                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats")
                                                                        .child("count").setValue(count_j);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                }
                            });



                            holder.ill_go_btn.setBackgroundResource(R.drawable.go_btn_bg_success);
                            holder.ill_go_btn.setText("Refuse");


                        }


                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }





    @NonNull
    @NotNull
    @Override
    public NotificationAdapter.viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_notifications, parent, false);
        return new viewholder(view);
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView from, event_name, time_ago;
        Button ill_go_btn;
        public viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.from);
            event_name = itemView.findViewById(R.id.event_name);
            time_ago = itemView.findViewById(R.id.time_ago);
            ill_go_btn = itemView.findViewById(R.id.ill_go_btn);
        }
    }
}
