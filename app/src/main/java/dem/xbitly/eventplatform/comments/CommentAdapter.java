package dem.xbitly.eventplatform.comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dem.xbitly.eventplatform.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private ArrayList<Comment> comments;
    private final FirebaseDatabase dBase;
    private DatabaseReference ref, ref1;
    private boolean e=true;
    private Context context;

    public CommentAdapter(ArrayList<Comment> comments, FirebaseDatabase dBase, DatabaseReference ref, Context context) {
        this.comments = comments;
        this.dBase = dBase;
        this.ref = ref;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_comment;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.username.setText(comments.get(position).getUsername());
        holder.text.setText(comments.get(position).getText());
        holder.timeAndData.setText(comments.get(position).getDate());
        Glide.with(context).load(comments.get(position).getImageURL()).into(holder.photo);
//        getProfileImage(holder, comments.get(position).getUsername());
    }

    private void getProfileImage(CommentHolder holder, String uid){
        e = true;
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("profile_image")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (e){
                            String ref = snapshot.getValue().toString();
                            if (!ref.equals("")){
                                StorageReference ref2 = FirebaseStorage.getInstance().getReference().child(ref);
                                ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(context).load(uri).into(holder.photo);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("profile_image", "ERROR: "+e.getMessage());
                                    }
                                });
                            }
                            e = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        TextView text, username, timeAndData;
        CircleImageView photo;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            username = itemView.findViewById(R.id.username);
            timeAndData = itemView.findViewById(R.id.time_and_data);
            photo = itemView.findViewById(R.id.profile_image);
        }
    }

}