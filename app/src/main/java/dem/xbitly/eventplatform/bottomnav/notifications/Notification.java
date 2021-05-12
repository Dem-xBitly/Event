package dem.xbitly.eventplatform.bottomnav.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Notification {
    String from;
    int event_number;
    String event_name;

    public Notification (String from, int event_number, String event_name){
        this.from = from;
        this.event_number = event_number;
        this.event_name = event_name;
    }

    public Notification(){

    }

    public String getFrom(){

        return from;
    }

    public void setFrom(String from){
        this.from = from;
    }

//    public String getEvent_number(){
//
//        FirebaseDatabase.getInstance().getReference("PrivateEvents").child(Integer.toString(event_number)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                event_name = snapshot.child("name").getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//        return event_name;
//    }

    public String getEvent_name(){
        return event_name;
    }

}
