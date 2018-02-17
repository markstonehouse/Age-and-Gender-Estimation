package com.example.mark.estimation;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Mark on 15/02/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MainMenuUILoads {

    @Rule
    public IntentsTestRule<MainMenu> mActivityRule = new IntentsTestRule<>(
            MainMenu.class);

    @Test
    public void checkUIHasLoaded() {
        onView(withId(R.id.btn_captureImage)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_importImage)).check(matches(isDisplayed()));
        onView(withId(R.id.image_appLogo)).check(matches(isDisplayed()));
        onView(withId(R.id.text_appTitle)).check(matches(isDisplayed()));
    }
}
