package dem.xbitly.eventplatform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetEventDialog extends BottomSheetDialogFragment {

    private final String name, address, count_people, date, time;

    public BottomSheetEventDialog(String name, String address, String count_people, String date, String time){

        this.name = name;
        this.address = address;
        this.count_people = count_people;
        this.date = date;
        this.time = time;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        ImageButton buttonClose = v.findViewById(R.id.btn_close);
        TextView name = v.findViewById(R.id.name);
        TextView address = v.findViewById(R.id.place);
        TextView count_people = v.findViewById(R.id.count_people);
        TextView date = v.findViewById(R.id.date);
        TextView time = v.findViewById(R.id.time);

        buttonClose.setOnClickListener(view -> {
            dismiss();
        });

        name.setText(this.name);
        address.setText(this.address);
        count_people.setText(this.count_people);
        date.setText(this.date);
        time.setText(this.time);

        return v;
    }
}
