package com.ayush.cryptochatv2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private Button btSignIn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageButton btSignUp;
    private RelativeLayout loading;
    private TextInputLayout etEmail, etPassword;
    private TextView header, footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initialize();
        setUI();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    loginUser();
                    etEmail.setError(null);
                    etPassword.setError(null);
                    etPassword.getEditText().setText(null);
                    etEmail.getEditText().setText(null);
                }
                startActivity(new Intent(LoginPage.this, HomePage.class));
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegistrationPage();
            }
        });
    }

    private void initialize() {
        btSignIn = findViewById(R.id.signInButton);
        btSignUp = findViewById(R.id.signUpButton);
        header = findViewById(R.id.loginHeader);
        footer = findViewById(R.id.loginFooter);
        etEmail = findViewById(R.id.loginEmail);
        etPassword = findViewById(R.id.loginPassword);
        loading = findViewById(R.id.loading);
        auth = FirebaseAuth.getInstance();
//        AdapterNetwork network = new AdapterNetwork(this);
    }

    private void loginUser() {
        loading.setVisibility(View.VISIBLE);
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    user = auth.getCurrentUser();
                    if (user == null) {
                        Log.d("LOGIN", "USER IS NULL");
                        loading.setVisibility(View.INVISIBLE);
                        return;
                    }
                    if(user.isEmailVerified()) {
                        loading.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(LoginPage.this, HomePage.class);
                        intent.putExtra("EMAIL", user.getEmail());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        LoginPage.this.finish();
                    }
                    else {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginPage.this, "Please Verify Email before Login ", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    loading.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginPage.this, "Authentication Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void moveToRegistrationPage() {
        Pair[] pairs = new Pair[6];
        pairs[0] = new Pair<View, String>(header, "header");
        pairs[1] = new Pair<View, String>(etEmail, "email");
        pairs[2] = new Pair<View, String>(etPassword, "password");
        pairs[3] = new Pair<View, String>(btSignIn, "button1");
        pairs[4] = new Pair<View, String>(btSignUp, "button2");
        pairs[5] = new Pair<View, String>(footer, "footer");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, pairs);
        Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
        startActivity(intent, options.toBundle());
    }

    private void setUI() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loading.setVisibility(View.INVISIBLE);
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
    }

    private boolean validateInput() {
        boolean flag = true;
        if(etPassword.getEditText() != null && etPassword.getEditText().getText().toString().isEmpty()){
            etPassword.setError("Empty Fields");
            flag = false;
        }
        if(etEmail.getEditText() != null && etEmail.getEditText().getText().toString().isEmpty()){
            etEmail.setError("Empty Fields");
            flag = false;
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
