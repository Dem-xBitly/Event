package dem.xbitly.eventplatform.tape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import dem.xbitly.eventplatform.CommentActivity;
import dem.xbitly.eventplatform.R;

public class TapeAdapter extends RecyclerView.Adapter<TapeHolder> {

    private final int countElements;
    private FirebaseDatabase dBase;
    private DatabaseReference ref, refLike;
    private String[] idsReview;
    private final ArrayList<Review> review = new ArrayList<>();
    private final Context context;
    private final String userID;

    public TapeAdapter(int count, Context context, String userID) {
        this.countElements = count;
        this.context = context;
        this.userID = userID;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_review;
    }

    @NonNull
    @Override
    public TapeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new TapeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TapeHolder holder, final int position) {
        dBase = FirebaseDatabase.getInstance();
        ref = dBase.getReference("Users");
        ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsReview = Objects.requireNonNull(snapshot.child("myReviews").getValue()).toString().split(",");
                String s = idsReview[position];
                ref = dBase.getReference("Reviews").child(s);
                ref.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        String s1 = Objects.requireNonNull(snapshot2.child("text").getValue()).toString();
                        String t = Objects.requireNonNull(snapshot2.child("time").getValue()).toString();
                        String d = Objects.requireNonNull(snapshot2.child("date").getValue()).toString();
                        String a = Objects.requireNonNull(snapshot2.child("autor").getValue()).toString();
                        String like = Objects.requireNonNull(snapshot2.child("like").getValue()).toString();
                        ref = dBase.getReference("Users").child(a);
                        Review reviews = new Review(s1);
                        reviews.setTime(t);
                        reviews.setDate(d);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                holder.getUsername().setText(Objects.requireNonNull(snapshot3.child("name").getValue()).toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        review.add(reviews);
                        String str = review.get(position).getText();
                        holder.getText().setText(str);
                        String[] split = like.split(",");
                        holder.getTimeAndData().setText(review.get(position).getDate() + " " + review.get(position).getTime());
                        holder.getLike().setText("Like: " + (split.length-1));

                        if(like.contains(userID)){
                            holder.getButtonLike().setImageResource(R.drawable.ic_like_pressed);
                        } else {
                            holder.getButtonLike().setImageResource(R.drawable.ic_like);
                        }

                        holder.getButtonLike().setOnClickListener(view -> {

                            refLike = dBase.getReference("Reviews").child(s);

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
                            intent.putExtra("id", s);
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return countElements;
    }
}