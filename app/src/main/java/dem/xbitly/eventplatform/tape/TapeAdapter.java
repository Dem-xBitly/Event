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

import dem.xbitly.eventplatform.activities.CommentActivity;
import dem.xbitly.eventplatform.R;

public class TapeAdapter extends RecyclerView.Adapter<TapeHolder> {

    private final int countElements;
    private final ArrayList<String> reviewsID = new ArrayList<>();
    private final ArrayList<String> invitesID = new ArrayList<>();
    private final String userID;
    private final Context context;
    private int countI = 0;
    private int countR = 0;
    DatabaseReference ref, ref2, refLike;

    public TapeAdapter(String[] sR, String[] sI, String userID, Context context) {
        this.countElements = sR.length + sI.length;
        this.reviewsID.addAll(Arrays.asList(sR));
        this.invitesID.addAll(Arrays.asList(sI));
        this.userID = userID;
        this.context = context;
    }

    @Override
    public int getItemViewType(final int position) {
        int x;
        int y;
        try {
            x = reviewsID.size()/invitesID.size();
            y = invitesID.size()/reviewsID.size();
        } catch (Exception ArithmeticException) {
            x = 0;
            y = 0;
        }


        if(x > 1){
            if ((position+1)%(x+1) == 0){
                return R.layout.item_invite;
            } else {
                return R.layout.item_review;
            }
        } else if (reviewsID.size() == invitesID.size()){
            if (position%2 == 0){
                return R.layout.item_review;
            } else {
                return R.layout.item_invite;
            }
        } else if (y > 1){
            if ((position+1)%(y+1) == 0){
                return R.layout.item_review;
            } else {
                return R.layout.item_invite;
            }
        } else {
            if (position < reviewsID.size()){
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

        int x;
        int y;
        try {
            x = reviewsID.size()/invitesID.size();
            y = invitesID.size()/reviewsID.size();
        } catch (Exception ArithmeticException) {
            x = 0;
            y = 0;
        }

        if(x > 1) {
            if ((position+1)%(x+1) == 0) {
                loadInvite(holder);
            } else {
                loadReview(holder);
            }
        } else if (reviewsID.size() == invitesID.size()) {
            if (position%2 == 0) {
                loadReview(holder);
            } else {
                loadInvite(holder);
            }
        } else if (y > 1) {
            if ((position+1)%(y+1) == 0) {
                loadReview(holder);
            } else {
                loadInvite(holder);
            }
        } else {
            if (position < reviewsID.size()){
                loadReview(holder);
            } else {
                loadInvite(holder);
            }
        }
    }

    private void loadInvite(@NonNull final TapeHolder holder){

        int i = getCountI();
        setCountI(i+1);

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();

        ref = dBase.getReference("Invite").child(invitesID.get(i));
        ref2 = dBase.getReference("Invite");
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

                ref2 = dBase.getReference("PublicEvents").child(Objects.requireNonNull(snapshot2.child("eventID").getValue()).toString());
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String go = "";
                        int ee = 0;
                        try {
                            go = Objects.requireNonNull(snapshot.child("go").getValue()).toString();
                            ee = Integer.parseInt(Objects.requireNonNull(snapshot.child("max_amount").getValue()).toString());
                        } catch (Exception e) {
                            ref2.child("go").setValue("");
                        }
                        if(go.contains(userID)){
                            holder.getButtonGo().setText("Go!!!");
                        }
                        if(ee != 0){
                            holder.getCountUsers().setText((go.split(",").length-1)+"/"+ee);
                        }
                        String finalGo = go;
                        holder.getButtonGo().setOnClickListener(view1 -> {
                            ref2 = dBase.getReference("PublicEvents").child(Objects.requireNonNull(snapshot2.child("eventID").getValue()).toString());
                            if(!finalGo.contains(userID)) {
                                holder.getButtonGo().setText("Go!!!");
                                ref2.child("go").setValue(finalGo + "," + userID);
                            } else {
                                holder.getButtonGo().setText("I will go");
                                ref2.child("go").setValue(finalGo.replace(","+userID, ""));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
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

    private void loadReview(@NonNull final TapeHolder holder){

        int i = getCountR();
        setCountR(i+1);

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

    public void setCountR(int r){
        this.countR = r;
    }

    public void setCountI(int i){
        this.countI = i;
    }

    public int getCountI(){
        return this.countI;
    }

    public int getCountR(){
        return this.countR;
    }

    @Override
    public int getItemCount() {
        return countElements;
    }
}