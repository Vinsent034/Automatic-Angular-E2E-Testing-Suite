package org.unina.robula;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class Transformations {
    /**
     * transf1: Replaces the * in the initial //* with the tag name of the element L.get(N).
     */
    public static List<String> transf1(String w, int N, List<Element> L) {
        List<String> results = new ArrayList<>();
        // The element corresponding to the head of 'w' is at index N-1 in L.
        if (N - 1 < L.size()) {
            Element ancestor = L.get(N - 1);
            String tagName = ancestor.tagName();

            // Replace first occurrence of "*" with tagName
            // w usually starts with //*, so we replace that specific pattern
            String newXpath = w.replaceFirst("//\\*", "//" + tagName);
            results.add(newXpath);
        }
        return results;
    }

    /**
     * transf2: Adds predicates (attributes) of the element L.get(N) to the first node in w.
     */
    public static List<String> transf2(String w, int N, List<Element> L) {
        List<String> results = new ArrayList<>();

        if (N - 1 >= L.size()) return results;
        Element currentElement = L.get(N - 1);
        String currentTag = currentElement.tagName();
        String baseStart = "//" + currentTag;

        // Iterate over all attributes
        for (Attribute attr : currentElement.attributes()) {
            String key = attr.getKey();
            String value = attr.getValue();

            // Skip extremely long attributes (heuristic) or style attributes often not unique/stable.
            // Also skip x-test* attributes: they are the researcher's own ground-truth markers for the
            // mutation-testing experiment (guaranteed preserved by every generated mutant), not a locator
            // a real engineer would target — using them would make this strategy trivially 100% robust
            // by construction rather than by genuine algorithmic merit.
            if (key.equals("style") || key.startsWith("x-test") || value.length() > 50) continue;

            String predicate = String.format("[@%s='%s']", key, value);

            // Inject predicate: //tag -> //tag[@k='v']
            if (w.startsWith(baseStart)) {
                String remainder = w.substring(baseStart.length());
                results.add(baseStart + predicate + remainder);
            }
        }
        return results;
    }

    /**
     * transf3: Adds the position of the element L.get(N) to the first node in w.
     * E.g., //tr/td -> //tr[2]/td
     */
    public static List<String> transf3(String w, int N, List<Element> L) {
        List<String> results = new ArrayList<>();

        if (N - 1 >= L.size()) return results;
        Element currentElement = L.get(N - 1);
        String currentTag = currentElement.tagName();

        // Calculate XPath position (1-based index among siblings of SAME tag)
        int pos = 1;
        Element prev = currentElement.previousElementSibling();
        while (prev != null) {
            if (prev.tagName().equals(currentTag)) {
                pos++;
            }
            prev = prev.previousElementSibling();
        }

        String baseStart = "//" + currentTag;
        String positionPredicate = "[" + pos + "]";

        if (w.startsWith(baseStart)) {
            String remainder = w.substring(baseStart.length());
            results.add(baseStart + positionPredicate + remainder);
        }

        return results;
    }

    /**
     * transf4: Adds //* at the top of w.
     * E.g., //tr/td -> //* /tr/td
     */
    public static List<String> transf4(String w) {
        List<String> results = new ArrayList<>();
        if (w.startsWith("//")) {
            String content = w.substring(2);
            // According to paper syntax: //* /rest
            results.add("//*/" + content);
        }
        return results;
    }
}
