package com.ayush.cryptochatv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.ayush.cryptochatv2.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPage extends AppCompatActivity {

    //    private Animation animationSignIn, animationSignUp;
    private Button btSignUp;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageButton btSignIn;
    private RelativeLayout loading;
    private String uid;
    private TextInputLayout etFullName, etEmail, etPassword, etConfirmPassword;
    private TextView header, footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        initialize();
        setUI();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginPage();
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    etConfirmPassword.setError(null);
                    etPassword.setError(null);
                    etEmail.setError(null);
                    etFullName.setError(null);
                    registerNewUser();
                    etConfirmPassword.getEditText().setText(null);
                    etPassword.getEditText().setText(null);
                    etEmail.getEditText().setText(null);
                    etFullName.getEditText().setText(null);
                }
            }
        });
    }

    private void initialize() {
        etFullName = findViewById(R.id.regFullName);
        etEmail = findViewById(R.id.regEmail);
        etPassword = findViewById(R.id.regPassword);
        etConfirmPassword = findViewById(R.id.regConfirmPassword);
        btSignIn = findViewById(R.id.regSignIn);
        btSignUp = findViewById(R.id.regSignUp);
        header = findViewById(R.id.regHeader);
        footer = findViewById(R.id.regFooter);
        loading = findViewById(R.id.loading);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        etFullName.clearFocus();
    }

    private void moveToLoginPage() {
        Pair[] pairs = new Pair[6];
        pairs[0] = new Pair<View, String>(header, "header");
        pairs[1] = new Pair<View, String>(etEmail, "email");
        pairs[2] = new Pair<View, String>(etPassword, "password");
        pairs[3] = new Pair<View, String>(btSignUp, "button1");
        pairs[4] = new Pair<View, String>(btSignIn, "button2");
        pairs[5] = new Pair<View, String>(footer, "footer");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationPage.this, pairs);
        Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
        startActivity(intent, options.toBundle());
    }

    private void registerNewUser() {
        loading.setVisibility(View.VISIBLE);
        final String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        final String name = etFullName.getEditText().getText().toString();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(auth.getCurrentUser() == null) {
                                loading.setVisibility(View.INVISIBLE);
                                Log.d("createNewUser: ", "USER IS NULL");
                                return;
                            }
                            uid = auth.getCurrentUser().getUid();
                            databaseReference.child("REQUESTS").child(uid).child("empty").setValue("empty");
                            databaseReference.child("CONTACTS").child(uid).child("empty").setValue("empty");
                            databaseReference.child("CHATS").child(uid).child("empty").setValue("empty");
                            Users newUser = new Users(name, email, uid);
                            databaseReference.child("USERS").child(uid).setValue(newUser);
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegistrationPage.this,"Registered, " +
                                                        "Check email for verification.", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                databaseReference.child("REQUESTS").child(uid).removeValue();
                                                databaseReference.child("CONTACTS").child(uid).removeValue();
                                                databaseReference.child("CHATS").child(uid).removeValue();
                                                if(task.getException() != null) {
                                                    loading.setVisibility(View.INVISIBLE);
                                                    Log.d("REGISTRATION", task.getException().getMessage());
                                                }
                                            }
                                            loading.setVisibility(View.INVISIBLE);
                                        }
                                    });
                        }
                        else {
                            loading.setVisibility(View.INVISIBLE);
                            try {
                                if(task.getException() != null) throw task.getException();
                            }
                            catch(FirebaseAuthWeakPasswordException ex) {
                                Toast.makeText(RegistrationPage.this, "Password is too Weak", Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthUserCollisionException ex) {
                                Toast.makeText(RegistrationPage.this, "Email already registered", Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException ex) {
                                Toast.makeText(RegistrationPage.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception ex){
                                Log.d("REGISTRATION", ex.getMessage());
                            }
                        }

                    }
                });
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

    private boolean validateEmail(String email) {
        String EMAILREG = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(EMAILREG);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    private boolean validateInput() {
        boolean flag = true;
        if(etConfirmPassword.getEditText().getText().toString().isEmpty()) {
            etConfirmPassword.setError("Empty Fields");
            flag = false;
        }
        if(etPassword.getEditText().getText().toString().isEmpty()) {
            etPassword.setError("Empty Fields");
            flag = false;
        }
        if(etEmail.getEditText().getText().toString().isEmpty()) {
            etEmail.setError("Empty Fields");
            flag = false;
        }
        else if(validateEmail(etEmail.getEditText().getText().toString())){
            etEmail.setError("Invalid Email");
            flag = false;
        }
        if(etFullName.getEditText().getText().toString().isEmpty()) {
            etFullName.setError("Empty Fields");
            flag = false;
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        moveToLoginPage();
        finish();
    }
}
