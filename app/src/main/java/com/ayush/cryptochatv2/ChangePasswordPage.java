package com.ayush.cryptochatv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ChangePasswordPage extends AppCompatActivity {

    private ImageButton passwordClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_page);
        initialize();

        passwordClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialize() {
        passwordClose = findViewById(R.id.passwordClose);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter_animation, R.anim.activity_exit_animation);
        finish();
    }
}
