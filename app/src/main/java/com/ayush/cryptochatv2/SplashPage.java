package com.ayush.cryptochatv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

public class SplashPage extends AppCompatActivity {

    private static final int DELAY = 3000;
    private Animation logoAnimation, headerAnimation, footerAnimation;
    private ImageView ivLogo, ivHeader, ivFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String firstTime = sharedPreferences.getString("firstTimeInstall", "");
        if(firstTime.equals("Yes")) {
            startActivity(new Intent(SplashPage.this, LoginPage.class));
            finish();
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstTimeInstall", "Yes");
            editor.apply();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            initialize();
            getWindow().setEnterTransition(null);
            getWindow().setExitTransition(null);
            ivLogo.setAnimation(logoAnimation);
            ivHeader.setAnimation(headerAnimation);
            ivFooter.setAnimation(footerAnimation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashPage.this, ivLogo, "transitionLogo");
                    Intent intent = new Intent(SplashPage.this, LoginPage.class);
                    startActivity(intent, optionsCompat.toBundle());
                    finish();
                }
            }, DELAY);
        }
    }

    private void initialize() {
        ivLogo = findViewById(R.id.ivLogo);
        ivHeader = findViewById(R.id.ivHeader);
        ivFooter = findViewById(R.id.ivFooter);
        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation);
        headerAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_header_animation);
        footerAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_footer_animation);
        new Fade().excludeTarget(android.R.id.navigationBarBackground, true);
    }
}
