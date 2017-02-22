package lb.census.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philippeschottey on 13/02/2017.
 */
public class CategoryFilter {

    public static List<CategoryFilter> parse(String filter) {
        String[] splitted = filter.split(",|;| ");
        List<CategoryFilter> parsed = new ArrayList(splitted.length);
        for (String el : splitted) {
            String cleaned = StringUtils.deleteWhitespace(el);
            CategoryFilter categoryFilter = null;
            if (cleaned.charAt(0) == '!') {
                categoryFilter = new CategoryFilter(cleaned.substring(1), true);
            } else {
                categoryFilter = new CategoryFilter(cleaned);
            }
            parsed.add(categoryFilter);
        }
        return parsed;
    }

    private String category;
    private boolean exclude = false;

    public CategoryFilter(String category, boolean exclude) {
        this.category = category;
        this.exclude = exclude;
    }

    public CategoryFilter(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public boolean isExclude() {
        return exclude;
    }
}
