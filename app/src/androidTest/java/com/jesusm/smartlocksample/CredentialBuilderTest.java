package com.jesusm.smartlocksample;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.gms.auth.api.credentials.Credential;
import com.jesusm.smartlocksample.manager.CredentialBuilder;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CredentialBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void nullIdBuild() {
        new CredentialBuilder()
                .withPassword("pass")
                .withImage(Uri.parse("content://image"))
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyBuild() {
        new CredentialBuilder().build();
    }

    @Test
    public void completeBuild() {
        Credential credential = new CredentialBuilder()
                .withId("manzanocaminojesus@gmail.com")
                .withPassword("pass")
                .withImage(Uri.parse("content://image"))
                .build();
        Assert.assertNotNull(credential);
    }

    @Test
    public void completeBuildAndReturnValues() {
        Credential credential = new CredentialBuilder()
                .withId("manzanocaminojesus@gmail.com")
                .withPassword("pass")
                .withImage(Uri.parse("content://image"))
                .build();
        Assert.assertNotNull(credential);
        Assert.assertNotNull(credential.getId());
        Assert.assertNotNull(credential.getPassword());
        Assert.assertNotNull(credential.getProfilePictureUri());
    }

    @Test
    public void onlyIdBuild() {
        Credential credential = new CredentialBuilder()
                .withId("manzanocaminojesus@gmail.com")
                .build();
        Assert.assertNotNull(credential);
    }

}
