package dem.xbitly.eventplatform.bottomnav.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.users.User;

public class NotificationAdapter extends FirebaseRecyclerAdapter<Notification, NotificationAdapter.viewholder>
{

    DatabaseReference ref;
    int event_number;
    String name, userID;
    int day, month, year, hour, minute;
    double longitude, latitude;
    boolean have_event_number=false;

    int event_number_in_private_events;


    public NotificationAdapter(@NonNull FirebaseRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull NotificationAdapter.viewholder holder, int i, @NonNull @NotNull Notification model) {
        holder.from.setText( model.getFrom() + " invited you to the event: ");
        holder.event_name.setText(model.getEvent_name());

        ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserPrivateEvents");



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
                                    holder.ill_go_btn.setText("Accepted");
                                    holder.ill_go_btn.setClickable(false);
                                }else{
                                    holder.ill_go_btn.setText("I'll go!");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                holder.ill_go_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ref.addValueEventListener(new ValueEventListener() {
                            boolean a = true;
                            boolean b = false;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    event_number = Integer.parseInt(snapshot.child("count").getValue().toString()) + 1;
                                    if (!b){
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("UserPrivateEvents").child(Integer.toString(event_number)).setValue(event_number_in_private_events);
                                        b = true;
                                    }

                                    ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserPrivateEvents");
                                    if (a) {
                                        snapshot.getRef().child("count").setValue(event_number);
                                        a = false;
                                    }

                                } catch (Exception e) {
                                    event_number = 1;
                                    ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UsPrivateEvents");
                                    if (!b){
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("UserPrivateEvents").child(Integer.toString(event_number)).setValue(event_number_in_private_events);
                                        b = true;
                                    }
                                    if (a) {
                                        snapshot.getRef().child("count").setValue(event_number);
                                        a = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invitations").child(key)
                                .child("accepted").setValue(true);


                        holder.ill_go_btn.setText("Accepted");
                        holder.ill_go_btn.setClickable(false);
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
