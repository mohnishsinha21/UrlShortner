package org.example;

import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;

import java.nio.charset.StandardCharsets;

public class URLShortenerUsingMurmurHash3 {

    // Define the base-57 character set
    private static final String BASE57 = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz123456789";
    private static final int BASE = BASE57.length();

    public static void main(String[] args) {
        String longUrl = "https://example.com/this-is-a-very-long-url";
        String shortUrl = shortenURL(longUrl);
        System.out.println("Long URL: " + longUrl);
        System.out.println("Short URL: " + shortUrl);

        // Benchmark
        long start = System.currentTimeMillis();
        int n = 1_000_000;
        for (int i = 0; i < n; i++) {
            shortenURL("https://example.com/url" + i);
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Time taken to generate %d short URLs: %d ms", n, (end - start)));
    }

    public static String shortenURL(String url) {
        byte[] hash = getMurmurHash(url);
        return encodeBase57(hash);
    }

    private static byte[] getMurmurHash(String url) {
        long hash = Hashing.murmur3_128().hashString(url, StandardCharsets.UTF_8).asLong();
        return Longs.toByteArray(hash);
    }

    private static String encodeBase57(byte[] input) {
        StringBuilder encoded = new StringBuilder();
        long value = 0;

        for (byte b : input) {
            value = (value << 8) | (b & 0xFF);
        }

        // Convert to base-57
        while (value > 0) {
            encoded.append(BASE57.charAt((int) (value % BASE)));
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