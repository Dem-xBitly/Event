package dem.xbitly.eventplatform.tape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import dem.xbitly.eventplatform.BottomSheetEventDialog;
import dem.xbitly.eventplatform.activities.CommentActivity;
import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.activities.MainActivity;

public class TapeAdapter extends RecyclerView.Adapter<TapeHolder> {

    private final int countElements;
    private final ArrayList<String> reviewsID = new ArrayList<>();
    private final ArrayList<String> invitesID = new ArrayList<>();
    private final String userID;
    private final Context context;
    FragmentManager fragmentManager;
    DatabaseReference ref, ref2, refLike;

    private int count;

    public TapeAdapter(String[] sR, String[] sI, String userID, Context context, FragmentManager fragmentManager) {
        this.countElements = sR.length + sI.length;
        this.reviewsID.addAll(Arrays.asList(sR));
        this.invitesID.addAll(Arrays.asList(sI));
        this.userID = userID;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getItemViewType(final int position) {

        int sizeR = reviewsID.size();
        int sizeI = invitesID.size();

        if (sizeI >= sizeR && sizeI != 0 && sizeR != 0) {
            int kf = sizeI / sizeR;
            if ((position+1)%(kf+1) == 0 && reviewsID.contains(Integer.toString((position+1)/(kf+1)))) {
                return R.layout.item_review;
            } else {
                return R.layout.item_invite;
            }
        }else if(sizeI < sizeR && sizeI != 0){
            int kf = sizeR / sizeI;
            if ((position+1)%(kf+1) == 0 && invitesID.contains(Integer.toString((position+1)/(kf+1)))) {
                return R.layout.item_invite;
            } else {
                return R.layout.item_review;
            }
        }else if(sizeI != 0){
            return R.layout.item_invite;
        }else if(sizeR != 0){
            return R.layout.item_review;
        } else {
            return 0;
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

        int sizeR = reviewsID.size();
        int sizeI = invitesID.size();

        if (sizeI >= sizeR && sizeI != 0 && sizeR != 0) {
            int kf = sizeI / sizeR;
            if ((position+1)%(kf+1) == 0 && reviewsID.contains(Integer.toString((position+1)/(kf+1)))) {
                loadReview(holder, (position+1)/(kf+1) - 1);
            } else {
                loadInvite(holder, (position+1) - (position+1)/(kf+1) - 1);
            }
        }else if(sizeI < sizeR && sizeI != 0){
            int kf = sizeR / sizeI;
            if ((position+1)%(kf+1) == 0 && invitesID.contains(Integer.toString((position+1)/(kf+1)))) {
                loadInvite(holder, (position+1)/(kf+1) - 1);
            } else {
                loadReview(holder, (position+1) - (position+1)/(kf+1) - 1);
            }
        }else if(sizeI != 0){
            loadInvite(holder, position);
        }else if(sizeR != 0){
            loadReview(holder, position);
        }

    }

    private void loadInvite(@NonNull final TapeHolder holder, final int position){

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();

        ref = dBase.getReference("Invite").child(invitesID.get(position));
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
                String eventID = snapshot2.child("eventID").getValue().toString();
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
                            dBase.getReference("Invite").child(invitesID.get(position)).child("eventID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String eventID = task.getResult().getValue().toString();
                                        FirebaseDatabase.getInstance().getReference("PublicEvents").child(eventID).child("chatID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    String chatID = task.getResult().getValue().toString();
                                                    FirebaseDatabase.getInstance().getReference("Chats").child(chatID).child("members").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                int members_count = Integer.parseInt(task.getResult().getValue().toString());
                                                                members_count++;
                                                                FirebaseDatabase.getInstance().getReference("Chats").child(chatID).child("members").child(Integer.toString(members_count))
                                                                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats")
                                                                        .child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                                        if (task.isSuccessful()){
                                                                            count = Integer.parseInt(task.getResult().getValue().toString());
                                                                            count++;
                                                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats")
                                                                                    .child("count").setValue(count);
                                                                            FirebaseDatabase.getInstance().getReference("PublicEvents").child(eventID).child("name").get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        String name = task.getResult().getValue().toString();
                                                                                        HashMap<String, String> chatInfo = new HashMap<>();
                                                                                        chatInfo.put("chatID", chatID);
                                                                                        chatInfo.put("name", name);
                                                                                        chatInfo.put("privacy", "no");
                                                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                .child("Chats").child("chats").child(Integer.toString(count)).setValue(chatInfo);
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
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

                holder.getButtonMenu().setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(context, view);
                    if (Objects.requireNonNull(snapshot2.child("userID").getValue()).toString().equals(userID)) {
                        popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "About event");
                        popup.getMenu().add(Menu.NONE, 1, Menu.NONE, "Edit");
                        SpannableString s = new SpannableString("Delete");
                        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                        popup.getMenu().add(Menu.NONE, 2, Menu.NONE, s);
                    } else {
                        popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "About event");
                    }
                    popup.show();
                    popup.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case 0: //About event
                                //сюда надо передовать нормальные значния, полученные из firebase
                                BottomSheetEventDialog bottomSheetEventDialog = new BottomSheetEventDialog("test", "Moscow", "11/20", "20.12.2021", "10:00");
                                bottomSheetEventDialog.show(fragmentManager, "Event info");
                                break;
                            case 1: //Edit
                                //code
                                break;
                            case 2: //Delete
                                //code
                                break;
                        }
                        return true;
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadReview(@NonNull final TapeHolder holder, final int position){

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();

        ref = dBase.getReference("Reviews").child(reviewsID.get(position));
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                holder.getTimeAndData().setText(Objects.requireNonNull(snapshot2.child("date").getValue()).toString() + " " + Objects.requireNonNull(snapshot2.child("time").getValue()).toString());
                String str = Objects.requireNonNull(snapshot2.child("text").getValue()).toString();
                holder.getText().setText(str);

                String like = Objects.requireNonNull(snapshot2.child("like").getValue()).toString();

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

                String[] split = like.split(",");
                holder.getLike().setText("Like: "  + (split.length-1));

                if(like.contains(userID)){
                    holder.getButtonLike().setImageResource(R.drawable.ic_like_pressed);
                } else {
                    holder.getButtonLike().setImageResource(R.drawable.ic_like);
                }

                holder.getButtonLike().setOnClickListener(view -> {

                    refLike = dBase.getReference("Reviews").child(reviewsID.get(position));

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
                    intent.putExtra("id", reviewsID.get(position));
                    context.startActivity(intent);
                });

                holder.getButtonShare().setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, str);
                    Intent chosenIntent = Intent.createChooser(intent, "Send review");
                    context.startActivity(chosenIntent);
                });

                holder.getButtonMenu().setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(context, view);
                    if (Objects.requireNonNull(snapshot2.child("userID").getValue()).toString().equals(userID)) {
                        popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "About event");
                        popup.getMenu().add(Menu.NONE, 1, Menu.NONE, "Edit");
                        SpannableString s = new SpannableString("Delete");
                        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                        popup.getMenu().add(Menu.NONE, 2, Menu.NONE, s);
                    } else {
                        popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "About event");
                    }
                    popup.show();
                    popup.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case 0: //About event
                                //сюда надо передовать нормальные значния, полученные из firebase
                                BottomSheetEventDialog bottomSheetEventDialog = new BottomSheetEventDialog("test", "Moscow", "11/20", "20.12.2021", "10:00");
                                bottomSheetEventDialog.show(fragmentManager, "Event info");
                                break;
                            case 1: //Edit
                                //code
                                break;
                            case 2: //Delete
                                //code
                                break;
                        }
                        return true;
                    });
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