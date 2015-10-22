package com.eric.zmappii;

import android.test.InstrumentationTestCase;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by eric on 15/7/3.
 */
public class Test extends InstrumentationTestCase {
    private static final String TAG = "TEST";
    public void getCurDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, year + "-" + month + "-" + day);
    }
}
