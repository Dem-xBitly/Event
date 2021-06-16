package dem.xbitly.eventplatform.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dem.xbitly.eventplatform.comments.CommentAdapter;
import dem.xbitly.eventplatform.databinding.ActivityCommentBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding binding;
    private FirebaseDatabase dBase;
    private DatabaseReference ref;
    boolean r = true;
    boolean e = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNetwork();

        dBase = FirebaseDatabase.getInstance();
        ref = dBase.getReference("Reviews/"+getIntent().getSerializableExtra("id").toString()+"/comments");

        binding.nameTxt.setText(getIntent().getSerializableExtra("name").toString());

        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        binding.commentBtnSend.setOnClickListener(view -> {

            r = true;

            String text = binding.commentEdit.getText().toString();
            Date date = new Date();
            SimpleDateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat formatForTime = new SimpleDateFormat("hh:mm");
            if(text.isEmpty()){
                FancyToast.makeText(getApplicationContext(),"Fields cannot be empty",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            } else {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(r) {
                            int count = 0;
                            try {
                                count = Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString());
                            } catch (Exception e) {
                                ref.child("count").setValue(0);
                            }

                            ref.child("count").setValue(String.valueOf(count + 1));
                            ref.child(String.valueOf(count + 1)).child("autor").setValue(userID);
                            ref.child(String.valueOf(count + 1)).child("text").setValue(text);
                            ref.child(String.valueOf(count + 1)).child("time").setValue(formatForTime.format(date));
                            ref.child(String.valueOf(count + 1)).child("date").setValue(formatForDate.format(date));
                            binding.commentEdit.setText("");
                            binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                            CommentAdapter commentAdapter = new CommentAdapter((count+1), dBase, ref);
                            binding.commentsRecycler.setAdapter(commentAdapter);
                            r = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        if(e) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                    int count = 0;
                    try {
                        count = Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString());
                    } catch (Exception e) {
                        ref.child("count").setValue(0);
                    }
                    CommentAdapter commentAdapter = new CommentAdapter(count, dBase, ref);
                    binding.commentsRecycler.setAdapter(commentAdapter);
                    e = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        binding.backFromCommentsBtn.setOnClickListener(view -> onBackPressed());

    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this)){
            Intent in_intent = new Intent (CommentActivity.this, InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}