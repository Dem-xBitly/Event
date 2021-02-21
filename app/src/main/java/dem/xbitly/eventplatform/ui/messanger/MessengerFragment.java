package dem.xbitly.eventplatform.ui.messanger;

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
import dem.xbitly.eventplatform.ui.home.HomeViewModel;

public class MessengerFragment extends Fragment {

    private MessengerViewModel messengerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messengerViewModel =
                new ViewModelProvider(this).get(MessengerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_messenger, container, false);
        final TextView textView = root.findViewById(R.id.text_messenger);
        messengerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}