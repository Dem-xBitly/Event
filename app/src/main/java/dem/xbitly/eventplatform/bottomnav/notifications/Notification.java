package dem.xbitly.eventplatform.bottomnav.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Notification {
    String from;
    int event_number;
    String event_name;
    String date;

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


    public String getEvent_name(){
        return event_name;
    }

}
