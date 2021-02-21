package dem.xbitly.eventplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button to_sign_in;
    private Button to_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        to_sign_in = findViewById(R.id.to_sign_in_btn);
        to_sign_up = findViewById(R.id.to_sign_up_btn);

        to_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in_intent = new Intent (StartActivity.this, LoginActivity.class);
                startActivity(in_intent);
            }
        });

        to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent up_intent = new Intent (StartActivity.this, RegisterActivity.class);
                startActivity(up_intent);
            }
        });
    }
}