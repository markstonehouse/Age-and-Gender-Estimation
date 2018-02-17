package com.example.mark.estimation;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.util.Log;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Mark on 15/02/2018.
 */

@RunWith(AndroidJUnit4.class)
public class CaptureImageTest {

    @Rule
    public IntentsTestRule<MainMenu> mActivityRule = new IntentsTestRule<>(
            MainMenu.class);

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule grantStoragePermissionRue =
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Before
    public void init() {
        Log.d("Testing: ", "Initialised.");
    }

    @Test
    public void checkUIHasLoaded() {
        onView(withId(R.id.btn_captureImage)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_importImage)).check(matches(isDisplayed()));
        onView(withId(R.id.image_appLogo)).check(matches(isDisplayed()));
        onView(withId(R.id.text_appTitle)).check(matches(isDisplayed()));
    }

//    @Before
//    public void grantCameraPermission() {
//        grantPermissionRule.apply()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//
//
//            Log.d("Testing: ", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.M == true");
//        }
//    }

    @Test
    public void clickCaptureImageButton() {
        onView(withId(R.id.btn_captureImage)).perform(click());
    }

    @Test
    public void clickImportImageButton() {
        onView(withId(R.id.btn_importImage)).perform(click());

    }
}
