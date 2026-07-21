package com.codecanvas.userservice.util;

import java.util.Random;

public class OtpGenerator {

    private static final Random RANDOM = new Random();

    private OtpGenerator() {
    }

    public static String generateOtp() {

        return String.format("%06d", RANDOM.nextInt(1000000));

    }
}