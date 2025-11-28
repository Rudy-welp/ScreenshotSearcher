package com.collegecode.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchService {

    /**
     * Searches for a query string within the text.
     * @param text The text to search in.
     * @param query The query string.
     * @return A list of starting indices where the query was found.
     */
    public List<Integer> search(String text, String query) {
        List<Integer> indices = new ArrayList<>();
        if (text == null || query == null || query.isEmpty()) {
            return indices;
        }

        // Case-insensitive search
        Pattern pattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            indices.add(matcher.start());
        }

        return indices;
    }
}
