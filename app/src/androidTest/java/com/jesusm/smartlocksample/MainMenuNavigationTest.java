package com.jesusm.smartlocksample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.jesusm.smartlocksample.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainMenuNavigationTest {
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
    @Test
    public void main_menu_open_google_signin_sample() {
        navigateToGoogleSample();

        // Check that the fragment has been inflated was changed.
        onView(withId(R.id.statusText)).check(matches(isDisplayed()));
    }

    private void navigateToGoogleSample() {
        onView(withId(R.id.googleSignInButton)).perform(click());
    }

    @Test
    public void main_menu_open_common_sample() {
        navigateToOrdinarySample();

        // Check that the fragment has been inflated was changed.
        onView(withId(R.id.email_login_form)).check(matches(isDisplayed()));
    }

    private void navigateToOrdinarySample() {
        onView(withId(R.id.ordinarySampleButton)).perform(click());
    }
}
