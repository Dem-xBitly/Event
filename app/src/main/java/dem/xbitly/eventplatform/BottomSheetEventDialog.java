package dem.xbitly.eventplatform;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shashank.sony.fancytoastlib.FancyToast;

import dem.xbitly.eventplatform.activities.CommentActivity;
import dem.xbitly.eventplatform.activities.CreateReviewActivity;
import dem.xbitly.eventplatform.activities.MainActivity;

public class BottomSheetEventDialog extends BottomSheetDialogFragment {

    private final String id, name, address, count_people, date, time;
    private boolean userIsGo, eventIsPrivate;

    public BottomSheetEventDialog(String id, String name, String address, String count_people, String date, String time, boolean userIsGo, boolean eventIsPrivate){

        this.id = id;
        this.name = name;
        this.address = address;
        this.count_people = count_people;
        this.date = date;
        this.time = time;
        this.userIsGo = userIsGo;
        this.eventIsPrivate = eventIsPrivate;

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
        TextView event_private = v.findViewById(R.id.text_private);
        RelativeLayout buttonAddReview = v.findViewById(R.id.btn_add_review);
        RelativeLayout buttonRefuse = v.findViewById(R.id.btn_refuse);

        if(eventIsPrivate){
            event_private.setVisibility(View.VISIBLE);
        } else {
            event_private.setVisibility(View.GONE);
        }

        address.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Address", address.getText().toString());
            clipboard.setPrimaryClip(clip);
            FancyToast.makeText(v.getContext(),"Address copied",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        });

        buttonClose.setOnClickListener(view -> {
            dismiss();
        });

        buttonRefuse.setOnClickListener(view -> {
            //отмена эвента
        });


        if (userIsGo) {
            buttonAddReview.setVisibility(View.VISIBLE);
            buttonAddReview.setOnClickListener(view -> {
                Intent intent = new Intent(v.getContext(), CreateReviewActivity.class);
                if(this.name.length() > 9){
                    intent.putExtra("nameEvent", this.name.substring(0, 7)+"...");
                } else {
                    intent.putExtra("nameEvent", this.name);
                }
                intent.putExtra("eventID", this.id);
                v.getContext().startActivity(intent);
            });
        } else {
            buttonAddReview.setVisibility(View.GONE);
        }

        name.setText(this.name);
        address.setText(this.address);
        count_people.setText(this.count_people);
        date.setText(this.date);
        time.setText(this.time);

        return v;
    }
}
