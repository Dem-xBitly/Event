package dem.xbitly.eventplatform.bottomnav.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.users.User;

public class NotificationAdapter extends FirebaseRecyclerAdapter<Notification, NotificationAdapter.viewholder>
{

    public NotificationAdapter(@NonNull FirebaseRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull NotificationAdapter.viewholder holder, int i, @NonNull @NotNull Notification model) {
        holder.from.setText( model.getFrom() + " invited you to the event: ");
        holder.event_name.setText(model.getEvent_name());
    }

    @NonNull
    @NotNull
    @Override
    public NotificationAdapter.viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_notifications, parent, false);
        return new viewholder(view);
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView from, event_name;
        public viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.from);
            event_name = itemView.findViewById(R.id.event_name);
        }
    }
}
