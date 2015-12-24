package com.company.entities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class TestTest {

    @org.junit.Test
    public void testRegular() {
          final Pattern ROZETKA_CATEGORY =  Pattern.compile(".*/c[0-9]*/.*");
        String href = "http://rozetka.com.ua/mobile-phones/apple/c80003/v069/";
        if (ROZETKA_CATEGORY.matcher(href).matches()) {
            System.out.println("YES");

        } else{
            System.out.println("NOO");
        }
    }


    @org.junit.Test
    public void testMap() {
        LinkedHashMap<String,Boolean> m = (LinkedHashMap) Collections.synchronizedMap(new LinkedHashMap<String,Boolean>(2000,0.75f,false));




    }
}