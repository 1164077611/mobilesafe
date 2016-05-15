package com.csx.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.csx.mobilesafe.db.dao.BlackNumberDao;

import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testAdd() {
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        // dao.add("110", 1);

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int mode = random.nextInt(3) + 1;
            if (i < 10) {
                dao.add("1341234567" + i, mode);
            } else {
                dao.add("135123456" + i, mode);
            }
        }
    }

}