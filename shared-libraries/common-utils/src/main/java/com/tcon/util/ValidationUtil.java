package com.tcon.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs
 */
public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    /**
     * Generate slug from string
     * Example: "Hello World!" -> "hello-world"
     */
    public static String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Generate unique slug with ID
     * Example: "Hello World", "123" -> "hello-world-123"
     */
    public static String generateUniqueSlug(String input, String id) {
        String baseSlug = generateSlug(input);
        return baseSlug + "-" + id;
    }

    /**
     * Generate slug with max length
     */
    public static String generateSlug(String input, int maxLength) {
        String slug = generateSlug(input);
        if (slug.length() > maxLength) {
            return slug.substring(0, maxLength);
        }
        return slug;
    }

    private SlugGenerator() {
        throw new IllegalStateException("Utility class");
    }
}
