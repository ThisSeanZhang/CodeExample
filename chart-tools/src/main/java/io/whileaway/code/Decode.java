package io.whileaway.code;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Decode {
    public static void main(String[] args) {
        String decode = URLDecoder.decode("%E6%99%BA%E6%85%A7", StandardCharsets.UTF_8);
        System.out.println(decode);
    }
}
