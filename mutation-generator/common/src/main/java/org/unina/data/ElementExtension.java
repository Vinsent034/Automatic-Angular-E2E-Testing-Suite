package org.unina.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.unina.util.ComponentIndexer;
import org.unina.util.RandomSelector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementExtension {

    public static Element getSibling(Element element){
        return element.nextElementSibling() != null
             ? element.nextElementSibling()
             : element.previousElementSibling();
    }

    public static Element getAncestor(Element element){
        if (element.parent() == null) return null;
        Element candidate = element.parent().parent();
        if (candidate == null) return null;
        String name = candidate.tagName();
        if (!("html".equalsIgnoreCase(name) || "body".equalsIgnoreCase(name) || "head".equalsIgnoreCase(name) || "#root".equalsIgnoreCase(name))) {
            return candidate;
        }
        return null;
    }

    /**
     * Nearest ancestor whose tag is a custom component tag (a dashed name such as app-panel).
     * Fixed on 21/09/2026: the pattern used to start with '<' and was matched against
     * tagName(), which never contains '<', so the role epsilon could never be found in any
     * application. Mutants generated before the fix are unaffected (they have no epsilon).
     * Angular's own dashed elements (ng-container, ng-template, ng-content, router-outlet)
     * are not components that host content, so they are skipped.
     */
    public static Element getContainingComponent(Element element){
        Pattern componentTagPattern = Pattern.compile("^[a-z][a-z0-9]*(?:-[a-z0-9]+)+$");
        Element temp = element.parent();
        while (temp != null && !isComponentTag(componentTagPattern, temp.tagName())) {
            temp = temp.parent();
        }
        return temp;
    }

    private static boolean isComponentTag(Pattern componentTagPattern, String name) {
        return componentTagPattern.matcher(name).find()
                && !name.startsWith("ng-")
                && !name.equals("router-outlet");
    }

    public static Component getComponent(Element element) throws IOException {
        Pattern pattern = Pattern.compile("selector:\\s*['\"]([^'\"]+)['\"]");

        Document doc = element.ownerDocument();
        if (doc == null) return null;
        Path docPath = Paths.get(doc.baseUri());
        String tsFileName = docPath.getFileName().toString().replace(".component.html", ".component.ts");
        Path tsFile = docPath.resolveSibling(tsFileName);

        Matcher matcher = pattern.matcher(Files.readString(tsFile));
        if (!matcher.find()) return null;

        String selector = matcher.group(1);
        return ComponentIndexer.getInstance().getComponentBySelector(selector);
    }

    public static List<Document> moveToNewComponent(Element element, Document destinationDocument) {
        Elements allElements = destinationDocument.getAllElements();
        allElements.clone().removeIf(candidate -> !isValidTarget(candidate, element));
        if (allElements.isEmpty()) {
            return Collections.emptyList();
        }

        Element randomCandidate = RandomSelector.getInstance().GetRandomItemFromCollection(allElements);
        Elements randomCandidateChildren = randomCandidate.children();
        int randomInsertionIndex = 0;
        if (!randomCandidateChildren.isEmpty()) {
            randomInsertionIndex = RandomSelector.getInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();
        }

        Document modifiedSourceDocument = element.ownerDocument();
        element.remove();
        randomCandidate.insertChildren(randomInsertionIndex, element);

        List<Document> mutatedDocuments = new ArrayList<>();
        mutatedDocuments.add(modifiedSourceDocument);
        mutatedDocuments.add(randomCandidate.ownerDocument());

        return mutatedDocuments;
    }

    private static boolean isValidTarget(Element candidate, Element target) {
        String name = candidate.tagName().toLowerCase();

        return !name.equals("html") && !name.equals("head")
                && candidate != target
                && candidate != target.parent()
                && !candidate.parents().contains(target);
    }
}
