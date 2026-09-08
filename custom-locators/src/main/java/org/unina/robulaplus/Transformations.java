package org.unina.robulaplus;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import static org.unina.robulaplus.Utils.*;

public class Transformations {

    /**
     * Robula+ Transformation: <b>convertStar</b>
     * <p>
     * Replaces the generic wildcard {@code *} at the head of the XPath with the actual tag name of the element.
     * This specialization restricts the locator to a specific element type.
     * </p>
     * <p>
     * <b>Precondition:</b> The current XPath head must start with {@code *}.
     * </p>
     * <p>
     * <i>Example:</i> {@code //*[@id='foo']} &rarr; {@code //div[@id='foo']}
     * </p>
     *
     * @param xp The current XPath candidate.
     * @param e  The DOM element corresponding to the current head of the XPath.
     * @return A new specialized {@link XPath} object, or {@code null} if the precondition is not met.
     */
    public static XPath transfConvertStar(XPath xp, Element e) {
        if (!xp.getHead().startsWith("*")) return null;

        XPath newXp = new XPath(xp);
        String currentHead = newXp.getHead();
        String predicates = currentHead.length() > 1 ? currentHead.substring(1) : "";
        newXp.replaceHead(e.tagName() + predicates);
        return newXp;
    }

    /**
     * Robula+ Transformation: <b>addID</b>
     * <p>
     * Refines the XPath by adding a predicate based on the element's {@code id} attribute.
     * IDs are generally considered the most robust locators.
     * </p>
     * <p>
     * <b>Precondition:</b> The current head of the XPath must not contain any existing predicates.
     * </p>
     *
     * @param xp The current XPath candidate.
     * @param e  The DOM element corresponding to the current head of the XPath.
     * @return A new {@link XPath} with the ID predicate appended, or {@code null} if the element has no ID or predicates already exist.
     */
    public static XPath transfAddID(XPath xp, Element e) {
        if (hasPredicates(xp.getHead()) || !e.hasAttr("id")) return null;

        XPath newXp = new XPath(xp);
        String currentHead = newXp.getHead();
        newXp.replaceHead(currentHead + "[@id='" + e.id() + "']");
        return newXp;
    }

    /**
     * Robula+ Transformation: <b>addText</b>
     * <p>
     * Refines the XPath by adding a predicate based on the element's text content.
     * </p>
     * <p>
     * <b>Heuristics:</b>
     * <ul>
     * <li>Uses {@code ownText()} to avoid matching text belonging to child nodes.</li>
     * <li>Ignores text that is empty or excessively long (> 20 chars) to maintain robustness.</li>
     * </ul>
     * </p>
     * <p>
     * <b>Precondition:</b> The current head of the XPath must not contain any existing predicates.
     * </p>
     *
     * @param xp The current XPath candidate.
     * @param e  The DOM element corresponding to the current head of the XPath.
     * @return A new {@link XPath} with the text predicate, or {@code null} if the text is unsuitable or predicates exist.
     */
    public static XPath transfAddText(XPath xp, Element e) {
        if (hasPredicates(xp.getHead()) || !e.hasText()) return null;

        String text = e.ownText().trim();
        if (text.isEmpty() || text.length() > 20) return null;

        XPath newXp = new XPath(xp);
        String safeText = text.replace("'", "\'");
        newXp.replaceHead(newXp.getHead() + "[contains(text(),'" + safeText + "')]");
        return newXp;
    }

    /**
     * Robula+ Transformation: <b>addAttribute</b>
     * <p>
     * Generates a list of new candidate XPaths by adding a predicate for each valid attribute found on the element.
     * Unlike {@code transfAddID}, this method creates multiple branches in the search tree (one for each attribute).
     * </p>
     * <p>
     * <b>Heuristics:</b> Skips the 'id' attribute (handled separately), 'style' attributes (unstable),
     * and values that are too long (> 50 chars).
     * </p>
     * <p>
     * <b>Precondition:</b> The current head of the XPath must not contain any existing predicates.
     * </p>
     *
     * @param xp The current XPath candidate.
     * @param e  The DOM element corresponding to the current head of the XPath.
     * @return A list of new {@link XPath} candidates. Returns an empty list if predicates already exist.
     */
    public static List<XPath> transfAddAttribute(XPath xp, Element e) {
        List<XPath> candidates = new ArrayList<>();
        if (hasPredicates(xp.getHead())) return candidates;

        for (Attribute attr : e.attributes()) {
            String key = attr.getKey();
            String val = attr.getValue();
            if (key.equals("id")) continue;
            // x-test*: the researcher's own ground-truth markers (guaranteed preserved by every generated
            // mutant), not a locator a real engineer would target — see robula/Transformations.java for the
            // full rationale.
            if (key.startsWith("style") || key.startsWith("x-test") || val.length() > 50) continue;

            XPath newXp = new XPath(xp);
            newXp.replaceHead(newXp.getHead() + "[@" + key + "='" + val.replace("'", "\\'") + "']");
            candidates.add(newXp);
        }
        return candidates;
    }

    /**
     * Robula+ Transformation: <b>addAttributeSet</b>
     * <p>
     * Generates candidate XPaths using combinations of multiple attributes (Power Set).
     * This allows for locators like {@code //div[@class='foo' and @name='bar']}.
     * </p>
     * <p>
     * <b>Performance Note:</b> Limits the input to the first 5 valid attributes to prevent exponential explosion
     * (O(2^n)). Only generates subsets with a cardinality > 1.
     * </p>
     * <p>
     * <b>Precondition:</b> The current head of the XPath must not contain any existing predicates.
     * </p>
     *
     * @param xp The current XPath candidate.
     * @param e  The DOM element corresponding to the current head of the XPath.
     * @return A list of new {@link XPath} candidates based on attribute combinations.
     */
    public static List<XPath> transfAddAttributeSet(XPath xp, Element e) {
        List<XPath> candidates = new ArrayList<>();

        if (hasPredicates(xp.getHead())) return candidates;

        List<Attribute> validAttrs = new ArrayList<>();
        for (Attribute attr : e.attributes()) {
            String key = attr.getKey();
            if (key.equals("id") || key.startsWith("style") || key.startsWith("x-test")) continue;
            validAttrs.add(attr);
        }

        if (validAttrs.size() > 5) {
            validAttrs = validAttrs.subList(0, 5);
        }

        List<List<Attribute>> powerSet = Utils.generatePowerSet(validAttrs);

        for (List<Attribute> subset : powerSet) {
            if (subset.size() < 2) continue;

            XPath newXp = new XPath(xp);
            StringBuilder predicate = new StringBuilder("[");
            for (int i = 0; i < subset.size(); i++) {
                Attribute attr = subset.get(i);
                predicate.append("@")
                        .append(attr.getKey())
                        .append("='")
                        .append(attr.getValue().replace("'", "\\'"))
                        .append("'");

                if (i < subset.size() - 1) {
                    predicate.append(" and ");
                }
            }
            predicate.append("]");

            newXp.replaceHead(newXp.getHead() + predicate.toString());
            candidates.add(newXp);
        }

        return candidates;
    }

    /**
     * Robula+ Transformation: <b>addPosition</b>
     * <p>
     * Refines the XPath by adding the element's position index among its siblings (e.g., {@code [2]}).
     * This is often used as a fallback when no unique attributes are available.
     * </p>
     * <p>
     * <b>Note:</b> Adjusts the 0-based index from Jsoup to the 1-based index required by XPath.
     * </p>
     * <p>
     * <b>Precondition:</b> The current head of the XPath must not already contain a position predicate.
     * </p>
     *
     * @param xp The current XPath candidate.
     * @param e  The DOM element corresponding to the current head of the XPath.
     * @return A new {@link XPath} with the position predicate, or {@code null} if one already exists.
     */
    public static XPath transfAddPosition(XPath xp, Element e) {
        if (hasPositionPredicate(xp.getHead())) return null;

        XPath newXp = new XPath(xp);
        int pos = e.elementSiblingIndex() + 1;
        newXp.replaceHead(newXp.getHead() + "[" + pos + "]");
        return newXp;
    }

    /**
     * Robula+ Transformation: <b>addLevel</b>
     * <p>
     * Expands the scope of the XPath by prepending a wildcard ancestor {@code //*} to the path.
     * This effectively shifts the "head" of the XPath up one level in the DOM tree, allowing
     * subsequent transformations to operate on the parent element.
     * </p>
     * <i>Example:</i> {@code //tr/td} &rarr; {@code //*\/tr/td}
     * </p>
     * <p>
     * <b>Precondition:</b> The current length of the XPath (in nodes) must be less than the total depth of the ancestor list.
     * </p>
     *
     * @param xp        The current XPath candidate.
     * @param ancestors The list of ancestor elements used to bound the expansion.
     * @return A new {@link XPath} with a prepended wildcard, or {@code null} if the top of the DOM tree is reached.
     */
    public static XPath transfAddLevel(XPath xp, List<Element> ancestors) {
        if (xp.getLength() >= ancestors.size()) return null;

        XPath newXp = new XPath(xp);
        newXp.prepend("*");
        return newXp;
    }
}
