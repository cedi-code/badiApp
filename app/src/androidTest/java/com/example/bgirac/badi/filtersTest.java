package com.example.bgirac.badi;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class filtersTest {

    @Rule
    public ActivityTestRule<filters> mActivityRule = new ActivityTestRule<>(filters.class);

    @Test
    public void addKantonsliste() {
        filters f = mActivityRule.getActivity();
        ListView lv = new ListView(f.getApplicationContext());
        f.addKantonsliste(lv);

        assertEquals("Bern", lv.getItemAtPosition(1).toString());
    }
}