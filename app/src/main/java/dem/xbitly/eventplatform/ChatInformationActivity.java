package dem.xbitly.eventplatform;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dem.xbitly.eventplatform.activities.ChatActivity;
import dem.xbitly.eventplatform.activities.MembersActivity;
import dem.xbitly.eventplatform.bottomsheet.BottomSheetEditDialog;
import dem.xbitly.eventplatform.bottomsheet.BottomSheetEventDialog;
import dem.xbitly.eventplatform.databinding.ActivityChatInformationBinding;
import dem.xbitly.eventplatform.databinding.ContentScrollingBinding;
import dem.xbitly.eventplatform.members.MembersAdapter;


public class ChatInformationActivity extends AppCompatActivity {

    private ActivityChatInformationBinding binding;

    private boolean e = true;
    private boolean a = true;
    private boolean f = true;
    private boolean vuv = true;
    private boolean u = true;

    private int eventId;
    private int chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backFromChatInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        eventId = getIntent().getIntExtra("eventID", -1);
        chatId  = getIntent().getIntExtra("chatID", -1);

        binding.addMembersBtn.setVisibility(View.GONE); // по умолчанию кнопка получения ссылки-инвайта недоступна

        int chatid = getIntent().getIntExtra("chatID", -1); //отлавливаем ошибку, если возник сбой при смене активностей
        if (chatid==-1)
            Toast.makeText(this, "Error, no chatID: " + Integer.toString(chatid), Toast.LENGTH_SHORT).show();
        else {
            e = true;

            if (eventId%2==0){ //получение информации для публичного евента
                a = true;
                FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(Integer.toString(eventId)).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (a){
                            String chatName = snapshot.getValue().toString();
                            binding.chatNameInfo.setText(chatName);
                            a = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                getMembersPublic(eventId); //отображение всех участников евента в recyclerview
            }else{ // получение информации для приватного евента
                a = true;
                FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(Integer.toString(eventId)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (a){
                            String chatName = snapshot.child("name").getValue().toString();
                            String userId=snapshot.child("userID").getValue().toString();
                            if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                binding.addMembersBtn.setVisibility(View.VISIBLE); //если данный пользователь - создатель евента, то даем ему доступ скопировать ссылку-инвайт
                            }
                            binding.chatNameInfo.setText(chatName);
                            a = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                getMembersPrivate(eventId); //отображение всех участников евента в recyclerview
            }

        }

        binding.addMembersBtn.setOnClickListener(new View.OnClickListener(){ //по клику на кнопку копирования вставляем ссылку-инвайт в clipboard
            @Override
            public void onClick(View v) {
                u=true;
                FirebaseDatabase.getInstance().getReference().child("Chats").child(Integer.toString(chatId)).child("invitationLink")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (u){
                                    String invitationLink = snapshot.getValue().toString();
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("InvitationLink", invitationLink);
                                    clipboard.setPrimaryClip(clip);
                                    FancyToast.makeText(getApplicationContext(), "Invitation link copied to clipboard", FancyToast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    void getMembersPublic(int eventId){
        //получение всех участников чата данного мероприятия
        FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(Integer.toString(eventId))
                .child("go").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String go = Objects.requireNonNull(snapshot.getValue()).toString();
                String[] id = go.split(",");
                binding.chatMembersNumber.setText(Integer.toString(id.length) + " members"); //заполнение поля с информацией о колве участников
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatInformationActivity.this);
                ContentScrollingBinding contentView = binding.content;
                contentView.membersRv.setLayoutManager(linearLayoutManager);
                MembersAdapter membersAdapter = new MembersAdapter(id);
                contentView.membersRv.setAdapter(membersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getMembersPrivate(int eventId){
        //получение всех участников чата данного мероприятия
        FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(Integer.toString(eventId))
                .child("go").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String go = Objects.requireNonNull(snapshot.getValue()).toString();
                String[] id = go.split(",");
                binding.chatMembersNumber.setText(Integer.toString(id.length-1) + " members"); //заполнение поля с информацией о колве участников
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatInformationActivity.this);
                ContentScrollingBinding contentView = binding.content;
                contentView.membersRv.setLayoutManager(linearLayoutManager);
                MembersAdapter membersAdapter = new MembersAdapter(id);
                contentView.membersRv.setAdapter(membersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}