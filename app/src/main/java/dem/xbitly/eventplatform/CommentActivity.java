package dem.xbitly.eventplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dem.xbitly.eventplatform.databinding.ActivityCommentBinding;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}