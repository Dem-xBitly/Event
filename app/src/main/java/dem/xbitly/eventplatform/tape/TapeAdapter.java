package dem.xbitly.eventplatform.tape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import dem.xbitly.eventplatform.CommentActivity;
import dem.xbitly.eventplatform.R;

public class TapeAdapter extends RecyclerView.Adapter<TapeHolder> {

    private final int countElements;
    private final ArrayList<String> reviewsID = new ArrayList<>();
    private final ArrayList<String> invitesID = new ArrayList<>();
    private final String userID;
    private final Context context;
    DatabaseReference ref, refLike;

    public TapeAdapter(String[] sR, String[] sI, String userID, Context context) {
        this.countElements = sR.length + sI.length;

        this.reviewsID.addAll(Arrays.asList(sR));
        this.invitesID.addAll(Arrays.asList(sI));
        this.userID = userID;
        this.context = context;
    }

    @Override
    public int getItemViewType(final int position) {
        int x = reviewsID.size()/invitesID.size();
        int y = invitesID.size()/reviewsID.size();
        if(x > 1){
            if (position%x == 0){
                return R.layout.item_invite;
            } else {
                return R.layout.item_review;
            }
        } else if (reviewsID.size() == invitesID.size()){
            if (position%2 == 0){
                return R.layout.item_invite;
            } else {
                return R.layout.item_review;
            }
        } else if (y > 1){
            if (position%y == 0){
                return R.layout.item_review;
            } else {
                return R.layout.item_invite;
            }
        } else {
            if(position < reviewsID.size()){
                return R.layout.item_review;
            } else {
                return R.layout.item_invite;
            }
        }
    }

    @NonNull
    @Override
    public TapeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == R.layout.item_invite){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite, parent, false);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        }
        return new TapeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TapeHolder holder, final int position) {

        int x = reviewsID.size()/invitesID.size();
        int y = invitesID.size()/reviewsID.size();

        if(x > 1){
            if (position%x == 0){
                loadInvite(holder, position/x);
            } else {
                loadReview(holder, position-(position/x));
            }
        } else if (reviewsID.size() == invitesID.size()){ // это работает
            if (position%2 == 0){
                loadInvite(holder, position-(position/2));
            } else {
                loadReview(holder, position/2);
            }
        } else if (y > 1){ //тут баг
            if (position%y == 0){
                loadReview(holder, position/y);
            } else {
                loadInvite(holder, position-(position/y));
            }
        } else {
            if(position < reviewsID.size()){
                loadReview(holder, position);
            } else {
                loadInvite(holder, position-reviewsID.size());
            }
        }

        //тут мб будут баги, но я пока не поймал, если что испрвляется не сложно
    }

    private void loadInvite(@NonNull final TapeHolder holder, int i){

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();

        ref = dBase.getReference("Invite").child(invitesID.get(i));
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                holder.getTimeAndData().setText(Objects.requireNonNull(snapshot2.child("date").getValue()).toString() + " " + Objects.requireNonNull(snapshot2.child("time").getValue()).toString());
                String str = Objects.requireNonNull(snapshot2.child("text").getValue()).toString();
                holder.getText().setText(str);

                ref = dBase.getReference("Users").child(Objects.requireNonNull(snapshot2.child("userID").getValue()).toString());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot3) {
                        holder.getUsername().setText(Objects.requireNonNull(snapshot3.child("name").getValue()).toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.getButtonGo().setOnClickListener(view -> {
                    //обработка кнопки willgo
                });

                holder.getButtonShare().setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, str);
                    Intent chosenIntent = Intent.createChooser(intent, "Send review");
                    context.startActivity(chosenIntent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadReview(@NonNull final TapeHolder holder, int i){

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();

        ref = dBase.getReference("Reviews").child(reviewsID.get(i));
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                holder.getTimeAndData().setText(Objects.requireNonNull(snapshot2.child("date").getValue()).toString() + " " + Objects.requireNonNull(snapshot2.child("time").getValue()).toString());
                String str = Objects.requireNonNull(snapshot2.child("text").getValue()).toString();
                holder.getText().setText(str);

                String like = Objects.requireNonNull(snapshot2.child("like").getValue()).toString();

                ref = dBase.getReference("Users").child(Objects.requireNonNull(snapshot2.child("autor").getValue()).toString());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot3) {
                        holder.getUsername().setText(Objects.requireNonNull(snapshot3.child("name").getValue()).toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String[] split = like.split(",");
                holder.getLike().setText("Like: " + (split.length-1));

                if(like.contains(userID)){
                    holder.getButtonLike().setImageResource(R.drawable.ic_like_pressed);
                } else {
                    holder.getButtonLike().setImageResource(R.drawable.ic_like);
                }

                holder.getButtonLike().setOnClickListener(view -> {

                    refLike = dBase.getReference("Reviews").child(reviewsID.get(i));

                    if(like.contains(userID)){

                        refLike.child("like").setValue(like.replace(","+userID, ""));

                    } else {

                        refLike.child("like").setValue(like+","+userID);

                    }

                });

                holder.getButtonComment().setOnClickListener(view -> {
                    Intent intent = new Intent(context, CommentActivity.class);
                    if(str.length() > 9){
                        intent.putExtra("name", str.substring(0, 7)+"...");
                    } else {
                        intent.putExtra("name", str);
                    }
                    intent.putExtra("id", reviewsID.get(i));
                    context.startActivity(intent);
                });

                holder.getButtonShare().setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, str);
                    Intent chosenIntent = Intent.createChooser(intent, "Send review");
                    context.startActivity(chosenIntent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return countElements;
    }
}