package com.ayush.cryptochatv2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private Animation animationEditAccount, animationChangePassword, animationHelp;
    private FirebaseAuth auth;
    private RelativeLayout layoutEditAccount, layoutChangePassword, layoutHelp, layoutLogout;
    private TextView fullName, email;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialization();
        setUI();

        layoutEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditAccount.startAnimation(animationEditAccount);
                startActivity(new Intent(view.getContext(), EditAccountPage.class),
                        ActivityOptions.makeCustomAnimation(view.getContext(),
                                R.anim.activity_enter_animation,
                                R.anim.activity_exit_animation).toBundle());
            }
        });

        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutChangePassword.startAnimation(animationChangePassword);
                startActivity(new Intent(view.getContext(), ChangePasswordPage.class),
                        ActivityOptions.makeCustomAnimation(view.getContext(),
                                R.anim.activity_enter_animation,
                                R.anim.activity_exit_animation).toBundle());
            }
        });

        layoutHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutHelp.startAnimation(animationHelp);
                startActivity(new Intent(view.getContext(), HelpPage.class),
                        ActivityOptions.makeCustomAnimation(view.getContext(),
                                R.anim.activity_enter_animation,
                                R.anim.activity_exit_animation).toBundle());
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(view.getContext(), LoginPage.class),
                        ActivityOptions.makeCustomAnimation(view.getContext(),
                                R.anim.activity_enter_animation,
                                R.anim.activity_exit_animation).toBundle());
                if(getActivity() != null) getActivity().finish();
            }
        });
        return view;
    }

    private void initialization() {
        fullName = view.findViewById(R.id.settingFullName);
        email = view.findViewById(R.id.settingEmail);
        layoutEditAccount = view.findViewById(R.id.settingsEditAccount);
        layoutChangePassword = view.findViewById(R.id.settingsChangePassword);
        layoutHelp = view.findViewById(R.id.settingsHelp);
        layoutLogout = view.findViewById(R.id.settingsLogout);
        auth = FirebaseAuth.getInstance();
        animationEditAccount = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_animation);
        animationChangePassword = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_animation);
        animationHelp = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_animation);
    }

    private void setUI() {
        fullName.setText(LoginPage.FULL_NAME);
        email.setText(LoginPage.EMAIL);
    }
}
