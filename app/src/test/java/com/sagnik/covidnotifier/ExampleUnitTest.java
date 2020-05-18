package com.sagnik.covidnotifier;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public static class A {
        public Long num;
    }
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        A a = new Gson().fromJson("{\"num\": 2}", A.class);
        System.out.println("Parsed num: "+a.num);
    }
}