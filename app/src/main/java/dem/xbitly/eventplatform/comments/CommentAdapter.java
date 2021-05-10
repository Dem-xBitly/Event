package dem.xbitly.eventplatform.comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dem.xbitly.eventplatform.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {

    private int countElements;

    public CommentAdapter() {
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_comment;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return countElements;
    }
}