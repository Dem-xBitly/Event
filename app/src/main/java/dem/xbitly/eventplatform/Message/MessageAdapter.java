package dem.xbitly.eventplatform.Message;

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
import dem.xbitly.eventplatform.chat.Chat;
import dem.xbitly.eventplatform.chat.ChatAdapter;

public class MessageAdapter extends FirebaseRecyclerAdapter<Message, MessageAdapter.viewHolder> {
    public MessageAdapter(@NonNull @NotNull FirebaseRecyclerOptions<Message> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull @NotNull MessageAdapter.viewHolder viewHolder, int i, @NonNull @NotNull Message msg) {
        viewHolder.text.setText(msg.getText());
        viewHolder.from.setText(msg.getFrom());
    }

    @NonNull
    @NotNull
    @Override
    public MessageAdapter.viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageAdapter.viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView text, from;
        public viewHolder(@NonNull View itemView){
            super(itemView);
            text = itemView.findViewById(R.id.message_text);
            from = itemView.findViewById(R.id.message_author);
        }
    }
}
