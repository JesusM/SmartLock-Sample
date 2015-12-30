package com.jesusm.smartlocksample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jesusm.smartlocksample.R;

public class MainActivity extends AppCompatActivity implements MainMenuFragment.MainMenuListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMenu();
        initUI();
    }

    private void initUI() {
        initToolbar();
    }

    private void initMenu() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                MainMenuFragment.newInstance()).commit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void goToGoogleSignInSample() {
        goToScreen(GoogleSignInFragment.newInstance(), "googleSignIn");
    }

    @Override
    public void goToOrdinarySample() {
        goToScreen(CommonSignInFragment.newInstance(), "ordinarySignIn");
    }

    private void goToScreen(Fragment destination, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, destination, tag).addToBackStack(tag).commit();
    }
}
