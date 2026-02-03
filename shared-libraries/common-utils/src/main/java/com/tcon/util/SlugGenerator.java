package com.tcon.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs from strings
 */
public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    private SlugGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate a URL-friendly slug from a string
     *
     * @param input the input string
     * @return the slug
     */
    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Generate a unique slug by appending a number if necessary
     *
     * @param input the input string
     * @param existingSlugs list of existing slugs to check against
     * @return a unique slug
     */
    public static String generateUniqueSlug(String input, java.util.Set<String> existingSlugs) {
        String baseSlug = generateSlug(input);
        String slug = baseSlug;
        int counter = 1;

        while (existingSlugs.contains(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}
