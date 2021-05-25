package dem.xbitly.eventplatform.bottomnav.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import dem.xbitly.eventplatform.tape.TapeAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private RecyclerView rv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();
        DatabaseReference ref = dBase.getReference("Reviews");
        DatabaseReference ref2 = dBase.getReference("Invite");

        rv = root.findViewById(R.id.home_posts_recycler);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> s = new ArrayList<>();

                for (int i = 0; i < Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString()); i++) {
                    s.add((i+1)+"");
                }

                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> s1 = new ArrayList<>();

                        for (int i = 0; i < Integer.parseInt(Objects.requireNonNull(snapshot.child("count").getValue()).toString()); i++) {
                            s1.add((i+1)+"");
                        }

                        String[] ss;
                        String[] ss1;
                        ss = s.toArray(new String[0]);
                        ss1 = s1.toArray(new String[0]);

                        rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
                        TapeAdapter tapeAdapter = new TapeAdapter(ss, ss1, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), root.getContext());
                        rv.setHasFixedSize(true);
                        rv.setAdapter(tapeAdapter);


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

        return root;
    }
}