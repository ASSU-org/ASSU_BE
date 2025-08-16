package com.assu.server.global.util;

import java.util.Random;

public class RandomNumberUtil {
    public static String generateSixDigit() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 100000~999999
        return String.valueOf(number);
    }
}
