package dem.xbitly.eventplatform.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

import dem.xbitly.eventplatform.R;


public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.myviewholder>
{

    ArrayList<User> userList = new ArrayList<>();

    public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull User model)
    {
       holder.name.setText(model.getName());
       userList.add(model);
    }

    public ArrayList <User> getData(){
        return userList;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person,parent,false);
       return new myviewholder(view);
    }



    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView name;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
            name=itemView.findViewById(R.id.username);
        }
    }
}
