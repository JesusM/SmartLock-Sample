package com.jesusm.smartlocksample.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jesusm.smartlocksample.R;

public class MainMenuFragment extends Fragment {

    private MainMenuListener listener;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainMenuListener) {
            listener = (MainMenuListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_menu_fragment, container, false);
        rootView.findViewById(R.id.googleSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.goToGoogleSignInSample();
                }
            }
        });
        rootView.findViewById(R.id.ordinarySampleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.goToOrdinarySample();
                }
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        return rootView;
    }

    public interface MainMenuListener {
        void goToGoogleSignInSample();

        void goToOrdinarySample();
    }
}
