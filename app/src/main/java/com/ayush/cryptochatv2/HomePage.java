package com.ayush.cryptochatv2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionSet;

public class HomePage extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getSupportFragmentManager().beginTransaction().add(R.id.home, new ChatFragment()).commit();
        initialize();
        bottomNavigation();
    }

    private void initialize() {
        bottomNavigation = findViewById(R.id.homeNav);
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setSharedElementEnterTransition(new Transition());
        chatFragment.setEnterTransition(null);
        chatFragment.setExitTransition(null);
        chatFragment.setSharedElementReturnTransition(new Transition());
    }

    private void bottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navChats) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home, new ChatFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                if(item.getItemId() == R.id.navExplore) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home, new ExploreFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                if(item.getItemId() == R.id.navSettings) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home, new SettingsFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigation.getSelectedItemId() == R.id.navChats) {
            System.exit(0);
            finish();
            return;
        }
        bottomNavigation.setSelectedItemId(R.id.navChats);
    }

    public static class Transition extends TransitionSet {
        Transition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).addTransition(new ChangeTransform());
        }
    }
}
