package dem.xbitly.eventplatform.tape;

import android.annotation.SuppressLint;
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

import dem.xbitly.eventplatform.R;

public class TapeAdapter extends RecyclerView.Adapter<TapeHolder> {

    private int countElements;
    private final RecyclerView rv;
    private FirebaseDatabase dBase;
    private DatabaseReference ref;
    private String[] idsReview;
    private final ArrayList<Review> review = new ArrayList<>();

    public TapeAdapter(RecyclerView rv, int count) {
        this.rv = rv;
        this.countElements = count;
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
                        String s = Objects.requireNonNull(snapshot2.child("text").getValue()).toString();
                        String t = Objects.requireNonNull(snapshot2.child("time").getValue()).toString();
                        String d = Objects.requireNonNull(snapshot2.child("date").getValue()).toString();
                        String a = Objects.requireNonNull(snapshot2.child("autor").getValue()).toString();
                        ref = dBase.getReference("Users").child(a);
                        Review reviews = new Review(s);
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
                        holder.getText().setText(review.get(position).getText());
                        holder.getTimeAndData().setText(review.get(position).getDate() + " " + review.get(position).getTime());
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