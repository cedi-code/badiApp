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
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addBadisToList() {
        MainActivity ma = mActivityRule.getActivity();
        //ma.addBadisToList();
        ListView lv = (ListView) ma.findViewById(R.id.badiliste) ;

        //der String welcher im ersten ListenObjekt gespeichert ist sollte wie folgt sein:
        assertEquals("Aarberg-Schwimmbecken", lv.getItemAtPosition(0).toString());

        //Es sollten mehr als 100 Badis in der Liste sein:
        assertTrue(lv.getAdapter().getCount() >= 100);
    }
}