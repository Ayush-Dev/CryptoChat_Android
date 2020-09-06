package com.ayush.cryptochatv2;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.cryptochatv2.pojo.Users;
import com.ayush.cryptochatv2.security.KeyExchange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    protected static String EMAIL, FULL_NAME, UID;
    protected static ArrayList<Integer> PRIVATE_KEY_PACKETS;
    private Button btSignIn, alertOK, alertCancel;
    private Dialog dialog;
    private EditText alertKey;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageButton btSignUp;
    private RelativeLayout loading;
    private String publicKey;
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
                    Objects.requireNonNull(etPassword.getEditText()).setText(null);
                    Objects.requireNonNull(etEmail.getEditText()).setText(null);
                }
//                startActivity(new Intent(LoginPage.this, HomePage.class));
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegistrationPage();
            }
        });

        alertOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String privateKey = alertKey.getText().toString();
                if(privateKey.isEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(LoginPage.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    return;
                }
                savePrivateKey(privateKey);
                publicKey = getPublicKey();
                FirebaseDatabase.getInstance().getReference("USERS").child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users user = snapshot.getValue(Users.class);
                                String serverKey = Objects.requireNonNull(user).getPublicKey();
                                EMAIL = user.getEmail();
                                FULL_NAME = user.getName();
                                UID = user.getUid();
                                if(publicKey.equals(serverKey)) {
                                    Intent intent = new Intent(LoginPage.this, HomePage.class);
                                    intent.putExtra("EMAIL", user.getEmail());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    startActivity(intent);
                                    LoginPage.this.finish();
                                }
                                else {
                                    Toast.makeText(LoginPage.this, "Authentication Error", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                dialog.dismiss();
            }
        });

        alertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(LoginPage.this, "Authentication Error", Toast.LENGTH_LONG).show();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == KeyEvent.KEYCODE_BACK;
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
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box_key_input);
        dialog.setCanceledOnTouchOutside(false);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertOK = dialog.findViewById(R.id.alertOK);
        alertCancel = dialog.findViewById(R.id.alertCancel);
        alertKey = dialog.findViewById(R.id.alertKey);
//        AdapterNetwork network = new AdapterNetwork(this);
    }

    private String getPublicKey() {
        String FILENAME = ".metadata.bin";
        String publicKey;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while((publicKey = bufferedReader.readLine()) != null)
                stringBuilder.append(publicKey);
            String privateKey = stringBuilder.toString();
            PRIVATE_KEY_PACKETS = KeyExchange.generatePrivatePackets(privateKey);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null) {
                try{
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayList<String> publicPackets = KeyExchange.generatePublicPackets(PRIVATE_KEY_PACKETS);
        publicKey = KeyExchange.generatePublicKey(publicPackets);
        return publicKey;
    }

    private void savePrivateKey(String privateKey) {
        String FILENAME = ".metadata.bin";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(FILENAME, MODE_PRIVATE);
            fileOutputStream.write(privateKey.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loginUser() {
        loading.setVisibility(View.VISIBLE);
        final String email = Objects.requireNonNull(etEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(etPassword.getEditText()).getText().toString();
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
                        publicKey = getPublicKey();
                        FirebaseDatabase.getInstance().getReference("USERS").child(user.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users user = snapshot.getValue(Users.class);
                                        String serverKey = Objects.requireNonNull(user).getPublicKey();
                                        EMAIL = email;
                                        FULL_NAME = user.getName();
                                        UID = user.getUid();
                                        if(publicKey.equals(serverKey)) {
                                            loading.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(LoginPage.this, HomePage.class);
                                            intent.putExtra("EMAIL", user.getEmail());
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_LONG).show();
                                            startActivity(intent);
                                            LoginPage.this.finish();
                                        }
                                        else {
                                            dialog.show();
                                            loading.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
