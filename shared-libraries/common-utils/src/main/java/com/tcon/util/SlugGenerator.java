package com.tcon.util;


import org.apache.commons.lang3.StringUtils;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugGenerator {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    /**
     * Generate URL-friendly slug from text
     */
    public static String generateSlug(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }

        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Generate unique slug with timestamp
     */
    public static String generateUniqueSlug(String input) {
        String baseSlug = generateSlug(input);
        return baseSlug + "-" + System.currentTimeMillis();
    }
}
