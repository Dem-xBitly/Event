package dem.xbitly.eventplatform.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import dem.xbitly.eventplatform.comments.Comment;
import dem.xbitly.eventplatform.comments.CommentAdapter;
import dem.xbitly.eventplatform.databinding.ActivityCommentBinding;
import dem.xbitly.eventplatform.network.NetworkManager;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding binding;
    private FirebaseDatabase dBase;
    private DatabaseReference ref;
    boolean r = true;
    boolean e = true;
    boolean u = true;
    boolean i = true;
    private ArrayList<Comment> comments = new ArrayList<>();
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNetwork();



        binding.refreshComments.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        binding.refreshComments.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkNetwork();
                binding.refreshComments.setRefreshing(true);
                loadComments();
            }
        });

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
                            count = 0;
                            try {
                                count = Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString());
                            } catch (Exception e) {
                                ref.child("count").setValue(0);
                            }

                            i = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (i){
                                        ref.child("count").setValue(String.valueOf(count + 1));
                                        ref.child(String.valueOf(count + 1)).child("autor").setValue(userID);
                                        ref.child(String.valueOf(count + 1)).child("text").setValue(text);
                                        ref.child(String.valueOf(count + 1)).child("time").setValue(formatForTime.format(date));
                                        ref.child(String.valueOf(count + 1)).child("date").setValue(formatForDate.format(date)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    loadComments();
                                                }
                                            }
                                        });
                                        ref.child(String.valueOf(count+1)).child("image").setValue(snapshot.child("profile_image").getValue().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            binding.commentEdit.setText("");
                            r = false;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        loadComments();


        binding.backFromCommentsBtn.setOnClickListener(view -> onBackPressed());

    }

    public void loadComments(){
        comments.clear();
        u = true;
        e = true;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (u){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        if (!snapshot1.getKey().equals("count")) {
                                comments.add(new Comment(snapshot1.child("autor").getValue().toString(), snapshot1.child("text").getValue().toString(),
                                        snapshot1.child("date").getValue().toString() + " " + snapshot1.child("time").getValue().toString(), snapshot1.child("image").getValue().toString()));
                        }
                    }
                    binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    CommentAdapter commentAdapter = new CommentAdapter(comments, dBase, ref, getApplicationContext());
                    binding.commentsRecycler.setAdapter(commentAdapter);
                    binding.refreshComments.setRefreshing(false);
                    u = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkNetwork(){
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            if (!connected) {
                Intent in_intent = new Intent (CommentActivity.this, InternetErrorConnectionActivity.class);
                startActivity(in_intent);
            }
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
    }
}