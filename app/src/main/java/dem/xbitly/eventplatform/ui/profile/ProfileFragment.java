package dem.xbitly.eventplatform.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.StartActivity;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;

    private ImageButton logout_btn;
    private ImageButton settings_btn;

    private TextView profile_name;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        logout_btn = root.findViewById(R.id.logout_btn);
        settings_btn = root.findViewById(R.id.settings_btn);
        profile_name = root.findViewById(R.id.profile_name);
//        profile_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent (getContext(), StartActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}