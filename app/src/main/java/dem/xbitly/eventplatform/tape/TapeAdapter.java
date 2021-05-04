package dem.xbitly.eventplatform.tape;

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

import dem.xbitly.eventplatform.R;

public class TapeAdapter extends RecyclerView.Adapter<TapeHolder> {

    private int countElements = 1;
    private final RecyclerView rv;
    private FirebaseDatabase dBase;
    private DatabaseReference ref;
    private String[] idsReview;
    private final ArrayList<Review> review = new ArrayList<>();

    public TapeAdapter(RecyclerView rv) {
        this.rv = rv;
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
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsReview = snapshot.child("myReviews").getValue().toString().split(",");

                // тут кароче как-то что-то не работает, но я не знаю как, исправлю утром

                for (String s : idsReview) {
                    ref = dBase.getReference("Reviews").child(s);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            String s = snapshot2.child("text").getValue().toString();
                            review.add(new Review(s));
                            holder.getText().setText(review.get(position).getText());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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