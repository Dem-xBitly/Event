package dem.xbitly.eventplatform.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import dem.xbitly.eventplatform.R;


public class UserAdapter extends FirebaseRecyclerAdapter <User, UserAdapter.myviewholder>
{

    ArrayList <String> users_ids = new ArrayList<>(); //id-шники тех пользователей, которые отмечены для приглашения

    public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull User model)
    {
       holder.name.setText(model.getName());




       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

                   final String user_id = getRef(position).getKey();
                   if (holder.itemView.findViewById(R.id.check_user).getVisibility() == View.VISIBLE){ //если пользователь отменил приглашение(удалил галочку)
                       holder.itemView.findViewById(R.id.check_user).setVisibility(View.INVISIBLE);
                       users_ids.remove(user_id);
                   }else { //если пользователь приглашает (поставил галочку)
                       users_ids.add(user_id);
                       holder.itemView.findViewById(R.id.check_user).setVisibility(View.VISIBLE);
                   }
           }
       });

    }

    public ArrayList<String> getUsers_ids(){
        return users_ids;
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
