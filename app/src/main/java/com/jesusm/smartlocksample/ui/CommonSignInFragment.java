package com.jesusm.smartlocksample.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.credentials.Credential;
import com.jesusm.smartlocksample.R;
import com.jesusm.smartlocksample.manager.SmartLockManager;
import com.jesusm.smartlocksample.manager.CredentialBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class CommonSignInFragment extends Fragment implements SmartLockManager.ProgressListener {

    private static final int PICK_IMAGE = 4;

    private Uri profileImage;
    private SmartLockManager smartLockManager;

    private View rootView;
    private AutoCompleteTextView emailTextView;
    private ProgressDialog progressDialog;
    private TextView passwordTextView;
    private ImageView profileImageView;
    private boolean loadingImage = false;
    private View signInButton;
    private View removeCredentialsButton;

    public CommonSignInFragment() {
        // Required empty public constructor
    }

    public static CommonSignInFragment newInstance() {
        return new CommonSignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smartLockManager = SmartLockManager.GetInstance();
        smartLockManager.build(getActivity(), savedInstanceState);
        smartLockManager.setProgressListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!loadingImage) {
            smartLockManager.loadCredentials(new SmartLockManager.LoadListener() {
                @Override
                public void credentialsLoaded(Credential credential) {
                    loadCredentialContent(credential);
                }

                @Override
                public void onError(@NonNull String errorMessage) {
                    showMessage(errorMessage);
                }

                @Override
                public void signInRequired() {
                    showMessage("Please, introduce your credentials and then click the sign in button");
                }

            }, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.common_sample_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.ordinary_sample);
        initUI();
        return rootView;
    }

    private void initUI() {
        rootView.findViewById(R.id.profile_picture_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingImage = true;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        signInButton = rootView.findViewById(R.id.common_sample_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCredentials();
            }
        });
        removeCredentialsButton = rootView.findViewById(R.id.common_sample_remove_button);
        removeCredentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartLockManager.deleteCredentials(new SmartLockManager.DeletionListener() {
                    @Override
                    public void credentialsDeleted() {
                        showMessage(getString(R.string.credentials_deleted_message));
                        clearCredentialUI();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showMessage(errorMessage);
                    }
                });
            }
        });
        emailTextView = (AutoCompleteTextView) rootView.findViewById(R.id.email);
        passwordTextView = (TextView) rootView.findViewById(R.id.password);
        profileImageView = (ImageView) rootView.findViewById(R.id.profileImageView);
    }

    private void saveCredentials() {
        CredentialBuilder credentialBuilder = new CredentialBuilder();
        if (profileImage != null) {
            credentialBuilder = credentialBuilder.withImage(profileImage);
        }
        if (!TextUtils.isEmpty(emailTextView.getText())) {
            credentialBuilder = credentialBuilder.withId(emailTextView.getText().toString());
        }

        if (!TextUtils.isEmpty(passwordTextView.getText())) {
            credentialBuilder = credentialBuilder.withPassword(passwordTextView.getText().toString());
        }
        smartLockManager.saveCredentials(credentialBuilder, new SmartLockManager.SaveListener() {
            @Override
            public void signInRequired() {
                showMessage(getString(R.string.google_sign_in_required));
            }

            @Override
            public void credentialsSaved(Credential credential) {
                loadCredentialContent(credential);
            }

            @Override
            public void onError(String errorMessage) {
                showMessage(errorMessage);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            saveProfileImage(data);
        } else if (!smartLockManager.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveProfileImage(Intent data) {
        if (data == null) {
            //Display an error
            return;
        }
        profileImage = data.getData();
        final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
        ContentResolver resolver = getActivity().getContentResolver();
        resolver.takePersistableUriPermission(profileImage, takeFlags);
        loadProfileImage();
    }

    private void loadProfileImage() {
        Picasso.with(getActivity()).setLoggingEnabled(true);
        Picasso.with(getActivity()).load(profileImage).into(profileImageView, new Callback() {
            @Override
            public void onSuccess() {
                profileImageView.setVisibility(View.VISIBLE);
                loadingImage = false;
            }

            @Override
            public void onError() {
                loadingImage = false;
            }
        });
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.progress_message));
        }
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    private void clearCredentialUI() {
        loadCredentialContent(null);
        signInButton.setVisibility(View.VISIBLE);
        removeCredentialsButton.setVisibility(View.INVISIBLE);
    }

    private void loadCredentialContent(Credential credential) {
        emailTextView.setText(credential.getId());
        passwordTextView.setText(credential.getPassword());

        profileImage = credential.getProfilePictureUri();
        loadProfileImage();
        signInButton.setVisibility(View.INVISIBLE);
        removeCredentialsButton.setVisibility(View.VISIBLE);
    }
}
