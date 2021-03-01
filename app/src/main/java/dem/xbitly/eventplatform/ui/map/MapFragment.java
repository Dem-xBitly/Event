package dem.xbitly.eventplatform.ui.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import dem.xbitly.eventplatform.PublicEventActivity;
import dem.xbitly.eventplatform.R;

public class MapFragment extends Fragment {

    private MapViewModel dashboardViewModel;

    private ImageButton create_event;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        create_event = root.findViewById(R.id.create_event_btn);
        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // set the custom dialog layout
                final View customLayout = getLayoutInflater().inflate(R.layout.create_event_dialog, null);
                customLayout.setBackgroundResource(R.drawable.create_event_dialog_shape);
                builder.setView(customLayout);

                Dialog dialog = new Dialog(getActivity());

                dialog.setContentView(customLayout);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.create_event_dialog_shape);

                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                Button public_btn = customLayout.findViewById(R.id.create_event_public);
                Button private_btn = customLayout.findViewById(R.id.create_event_private);

                public_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PublicEventActivity.class));
                    }
                });
            }
        });

        return root;
    }
}