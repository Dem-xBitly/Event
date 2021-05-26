package dem.xbitly.eventplatform.bottomnav.messanger;

import android.content.Intent;
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

import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.activities.InternetErrorConnectionActivity;
import dem.xbitly.eventplatform.network.NetworkManager;

public class MessengerFragment extends Fragment {

    private MessengerViewModel messengerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messengerViewModel =
                new ViewModelProvider(this).get(MessengerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_messenger, container, false);

        checkNetwork();

        final TextView textView = root.findViewById(R.id.text_messenger);
        messengerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    public void checkNetwork(){
        if(!NetworkManager.isNetworkAvailable(this.getContext())){
            Intent in_intent = new Intent (this.getContext(), InternetErrorConnectionActivity.class);
            startActivity(in_intent);
        }
    }
}