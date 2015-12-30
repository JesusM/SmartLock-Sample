package com.jesusm.smartlocksample.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.credentials.Credential;
import com.jesusm.smartlocksample.R;
import com.jesusm.smartlocksample.manager.SmartLockManager;
import com.squareup.picasso.Picasso;

public class GoogleSignInFragment extends Fragment implements SmartLockManager.ProgressListener {

    private SmartLockManager smartLockManager;
    private ProgressDialog progressDialog;
    private TextView statusText;
    private ImageView profileImageView;
    private View rootView;
    private View signInButton;
    private View removeCredentialsButton;

    public GoogleSignInFragment() {
    }

    public static GoogleSignInFragment newInstance() {
        return new GoogleSignInFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smartLockManager = SmartLockManager.GetInstance();
        smartLockManager.build(getActivity(), savedInstanceState);
        smartLockManager.setProgressListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.google_signin_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.sample_with_google_sign_in);
        initUI();
        return rootView;
    }

    private void initUI() {
        statusText = (TextView) rootView.findViewById(R.id.statusText);
        profileImageView = (ImageView) rootView.findViewById(R.id.profileImageView);
        initSignInButton();
        initSignOutButton();
    }

    private void initSignInButton() {
        signInButton = rootView.findViewById(R.id.google_sample_signIn_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartLockManager.saveWithGoogleAccount(new SmartLockManager.SaveWithGoogleSignInListener() {
                    @Override
                    public void credentialsSaved(Credential credential) {
                        fillUIWithCredential(credential);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showMessage(errorMessage);
                    }

                    @Override
                    public void signInRequired() {
                        showMessage(getString(R.string.google_sign_in_required));
                    }
                });
            }
        });
    }

    private void initSignOutButton() {
        removeCredentialsButton = rootView.findViewById(R.id.google_sample_remove_credentials);
        removeCredentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartLockManager.deleteCredentials(new SmartLockManager.DeletionListener() {
                    @Override
                    public void credentialsDeleted() {
                        showMessage(getString(R.string.credentials_deleted_message));
                        clearUI();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showMessage(errorMessage);
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        smartLockManager.loadCredentials(new SmartLockManager.LoadListener() {
            @Override
            public void credentialsLoaded(Credential credential) {
                fillUIWithCredential(credential);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showMessage(errorMessage);
            }

            @Override
            public void signInRequired() {
                showMessage(getString(R.string.google_sign_in_required));
            }
        }, false);
    }

    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
        }
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    private void clearUI() {
        statusText.setText(getString(R.string.google_sign_in_status_initial_text));
        profileImageView.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        removeCredentialsButton.setVisibility(View.INVISIBLE);
    }

    private void fillUIWithCredential(Credential credential) {
        statusText.setText("");
        if (!TextUtils.isEmpty(credential.getId())) {
            statusText.setText("Name: " + credential.getId());
        }
        if (credential.getProfilePictureUri() != null) {
            profileImageView.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(credential.getProfilePictureUri()).into(profileImageView);
        }
        signInButton.setVisibility(View.INVISIBLE);
        removeCredentialsButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        smartLockManager.onActivityResult(requestCode, resultCode, data);
    }
}
