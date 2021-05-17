package dem.xbitly.eventplatform.bottomnav.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification {
    String from;
    int event_number;
    String event_name;
    String date;
    String time_ago;

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

    public String timeAgo(int year, int month, int day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(new Date());
        String[] date_str = date.split("-");

        int year_now = Integer.parseInt(date_str[0]);
        int month_now = Integer.parseInt(date_str[1]);
        int day_now = Integer.parseInt(date_str[2]);

        if (year_now == year){
            if (month_now == month){
                if (day_now == day){
                    time_ago = "today";
                }else{
                    time_ago = Integer.toString(year_now - year) + " days ago";
                }
            }else{
                if (month_now - month == 1){
                    time_ago = "1 month ago";
                }else{
                    time_ago = Integer.toString(month_now - month) + " months ago";
                }
            }
        }else{
            if (year_now - year == 1){
                time_ago = "1 year ago";
            }else{
                time_ago = Integer.toString(year_now - year) + " years ago";
            }
        }

        return time_ago;
    }

    public void setFrom(String from){
        this.from = from;
    }


    public String getEvent_name(){
        return event_name;
    }

}
