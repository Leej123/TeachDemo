package com.vejoe.techdemo;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void splitDigit() {
        String str = "左移1020CM";
        str = str.toLowerCase();
        int index = str.indexOf("厘米");
        if (index < 0) {
            index = str.indexOf("cm");
        }
        if (index < 0) {
            index = str.indexOf("米");
        }
        if (index < 0) {
            index = str.indexOf("m");
        }
        System.out.println("index:" + index);
        if (index < 0) return;

        int endIndex = index;
        int startIndex = -1;
        for (int i = index - 1; i >= 0; i --) {
            String s = str.substring(i, endIndex);
            if (!s.matches("[0-9]")) {
                startIndex = i + 1;
                break;
            }
            endIndex = i;
        }

        if (startIndex != -1) {
            String digit = str.substring(startIndex, index);
            System.out.println("digit:" + digit);
        }
    }
}