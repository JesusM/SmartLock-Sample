package com.jesusm.smartlocksample;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.gms.auth.api.credentials.Credential;
import com.jesusm.smartlocksample.manager.CredentialBuilder;
import com.jesusm.smartlocksample.manager.SmartLockManager;
import com.jesusm.smartlocksample.ui.MainActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CredentialManagerTest {

    public static final String CREDENTIAL_ID = "manzanocaminojesus@gmail.com";
    public static final String IMAGE_URI = "content://a";
    public static final String PASSWORD = "password";

    private static final String GMS_PACKAGE = "com.google.android.gms";

    private static final String CLASS_LIST_VIEW = "android.widget.ListView";
    private static final long UI_TIMEOUT = 2500;

    /**
     * A JUnit {@link Rule @Rule} to launch your activity under test. This is a replacement
     * for {@link ActivityInstrumentationTestCase2}.
     * <p/>
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link Before @Before} method.
     * <p/>
     * {@link ActivityTestRule} will create and launch of the activity for you and also expose
     * the activity under test. To get a reference to the activity you can use
     * the {@link ActivityTestRule#getActivity()} method.
     */
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(
            MainActivity.class);
    private SmartLockManager smartLockManager;
    private UiDevice device;

    @Before
    public void setup() throws InterruptedException {
        // Get the device instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        smartLockManager = SmartLockManager.GetInstance();
        resetData();

        final CountDownLatch latch = new CountDownLatch(1);
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                smartLockManager.build(activityTestRule.getActivity(),
                        new SmartLockManager.BuildClientListener() {
                            @Override
                            public void clientBuilt() {
                                latch.countDown();
                            }
                        });
            }
        });

        Assert.assertEquals(latch.await(8, TimeUnit.SECONDS), true);
    }

    @Test
    public void loadInitUserAcceptsTest() throws InterruptedException {
        loadInitTest(true);
    }

    @Test
    public void loadInitUserDeniesTest() throws InterruptedException {
        loadInitTest(false);
    }

    private void loadInitTest(boolean userAcceptsSmartLock) {
        saveCredentialsTest(userAcceptsSmartLock);
        smartLockManager.loadCredentials(new SmartLockManager.LoadListener() {
            @Override
            public void credentialsLoaded(Credential credential) {
                assertCredentialData(credential);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                Assert.assertNull(smartLockManager.getCredential());
            }

            @Override
            public void signInRequired() {
                Assert.assertNull(smartLockManager.getCredential());
            }
        });
    }

    @Test
    public void saveCredentialsUserAcceptsTest() throws InterruptedException {
        saveCredentialsTest(true);
    }

    @Test
    public void saveCredentialsUserDeniesTest() throws InterruptedException {
        saveCredentialsTest(false);
    }

    private void saveCredentialsTest(boolean userAccept) {
        resetData();
        CredentialBuilder credentialBuilder = createMockCredentials();
        smartLockManager.saveCredentials(credentialBuilder, new SmartLockManager.SaveListener() {
            @Override
            public void signInRequired() {
                Assert.assertNull(smartLockManager.getCredential());
            }

            @Override
            public void credentialsSaved(Credential credential) {
                Assert.assertNotNull(smartLockManager.getCredential());
            }

            @Override
            public void onError(String errorMessage) {
                Assert.assertNull(smartLockManager.getCredential());
            }

        });
        waitForSmartLockInteraction(userAccept);
    }

    @Test
    public void deleteCredentialsTest() {
        smartLockManager.deleteCredentials(new SmartLockManager.DeletionListener() {
            @Override
            public void credentialsDeleted() {
                Assert.assertNull(smartLockManager.getCredential());
            }

            @Override
            public void onError(String errorMessage) {
                Assert.assertTrue(!errorMessage.isEmpty());
            }

        });
    }

    @Test
    public void loadInitGoogleSignInUserAcceptsTest() throws InterruptedException {
        loadInitGoogleSignInTest(true);
    }

    @Test
    public void loadInitGoogleSignInUserDeniesTest() throws InterruptedException {
        loadInitGoogleSignInTest(false);
    }

    private void loadInitGoogleSignInTest(boolean userAccepts) {
        resetData();
        smartLockManager.saveWithGoogleAccount(new SmartLockManager.SaveWithGoogleSignInListener() {

            @Override
            public void credentialsSaved(Credential credential) {
                assertCredentialData(credential);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                Assert.assertNull(smartLockManager.getCredential());
            }

            @Override
            public void signInRequired() {
                Assert.assertNull(smartLockManager.getCredential());
            }
        });
        waitForSmartLockInteraction(userAccepts);
    }

    private void resetData() {
        smartLockManager.deleteCredentials(null);
    }

    private CredentialBuilder createMockCredentials() {
        return new CredentialBuilder().withId(CREDENTIAL_ID)
                .withImage(Uri.parse(IMAGE_URI))
                .withPassword(PASSWORD);
    }

    private void waitForSmartLockInteraction(boolean accept) {
        BySelector firstAccountSelector = By.clazz(CLASS_LIST_VIEW);
        // Wait for account picker (may not appear)
        if (device.wait(Until.hasObject(firstAccountSelector), UI_TIMEOUT)) {
            // Click first account
            device.findObjects(firstAccountSelector).get(0).getChildren().get(0).click();
        }

        // The Play Services Smart Lock screen "save password" button.
        BySelector buttonSelector;
        if (accept) {
            buttonSelector = By.res(GMS_PACKAGE, "credential_save_confirm");
        } else {
            buttonSelector = By.res(GMS_PACKAGE, "credential_save_reject");
        }

        // Accept consent screen and click OK button (this also may not appear)
        if (device.wait(Until.hasObject(buttonSelector), UI_TIMEOUT)) {
            device.findObject(buttonSelector).click();
        }
    }

    private void assertCredentialData(Credential credential) {
        Assert.assertNotNull(credential);
        Assert.assertEquals(credential.getId(), CREDENTIAL_ID);
        Assert.assertEquals(credential.getPassword(), PASSWORD);
        Assert.assertEquals(credential.getProfilePictureUri(), Uri.parse(IMAGE_URI));
    }
}
