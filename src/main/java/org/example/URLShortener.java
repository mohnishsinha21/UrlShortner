package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class URLShortener {
    // Define the base-57 character set
    private static final String BASE57 = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz123456789";
    private static final int BASE = BASE57.length();

    public static void main(String[] args) {
        String longUrl = "https://example.com/this-is-a-very-long-url";
        String shortUrl = shortenURL(longUrl);
        System.out.println("Long URL: " + longUrl);
        System.out.println("Short URL: " + shortUrl);
    }

    public static String shortenURL(String url) {
        byte[] hash = getSHA256Hash(url);
        return encodeBase57(Arrays.copyOfRange(hash, 0, 6)); // Use the first 6 bytes for an 8-char short URL
    }

    private static byte[] getSHA256Hash(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(url.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeBase57(byte[] input) {
        StringBuilder encoded = new StringBuilder();
        int value = 0;

        for (byte b : input) {
            value = (value << 8) | (b & 0xFF);
        }

        // Convert to base-57
        while (value > 0) {
            encoded.append(BASE57.charAt(value % BASE));
            value /= BASE;
        }

        // Ensure the encoded string is 8 characters long
        while (encoded.length() < 8) {
            encoded.append(BASE57.charAt(0));
        }

        // If the encoded string is longer than 8 characters, truncate it
        if (encoded.length() > 8) {
            encoded.setLength(8);
        }

        return encoded.reverse().toString();
    }
}

