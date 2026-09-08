package org.unina.robula;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.robulaplus.RobulaPlus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Robula {
    /**
     * Generates a robust XPath for the given absolute XPath (or target element).
     *
     * @param absoluteXPath An existing absolute XPath to identify the target initially.
     * @param document      The Jsoup Document.
     * @return The generated robust XPath string.
     */
    public String getRobustXPath(String absoluteXPath, Document document) {
        Elements matches = Utils.eval(absoluteXPath, document);
        if (matches.isEmpty()) return null;

        Element targetElement = matches.first();
        return generateRobulaXPath(targetElement, document);
    }

    /**
     * Overload to work directly with an Element.
     */
    public String generateRobulaXPath(Element targetElement, Document document) {
        List<Element> ancestors = Utils.getAncestors(targetElement);

        LinkedList<String> p = new LinkedList<>();
        p.add("//*");
        List<String> temp = new ArrayList<>();
        List<String> visited = new ArrayList<>();

        while (!p.isEmpty()) {
            String w = p.removeFirst();
            if (visited.contains(w)) continue;
            visited.add(w);

            temp.clear();
            int N = Utils.getXPathLength(w);

            if (w.startsWith("//*")) {
                temp.addAll(Transformations.transf1(w, N, ancestors));
            } else {
                temp.addAll(Transformations.transf2(w, N, ancestors));
                temp.addAll(Transformations.transf3(w, N, ancestors));
            }
            if (N < ancestors.size()) {
                temp.addAll(Transformations.transf4(w));
            }

            for (String x : temp) {
                if (Utils.uniquelyLocate(x, targetElement, document)) {
                    return x;
                } else if (Utils.locate(x, targetElement, document)) {
                    p.add(x);
                }
            }
        }

        return ""; // Failed to find
    }

    public static void main(String[] args) {
        record element(String name, String xpath, String source){}
        List<element> elements = new ArrayList<>();
        // --- Scenario ALBUM-DETAIL (album fisso "Victorious" id 6uBm8oGd1fJNWpCsaURaPZ, autorale 2026-06-22) ---
        elements.add(new element("AlbumTitle", "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-album[1]/div[1]/as-media-summary[1]/div[1]/h2[1]", "album-detail.html"));
        elements.add(new element("PlayButton", "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-album[1]/div[1]/div[1]/as-play-button[1]", "album-detail.html"));
        elements.add(new element("TableHeader", "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-album[1]/div[1]/as-media-table-header[1]", "album-detail.html"));
        elements.add(new element("FirstTrackRow", "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-album[1]/div[1]/div[2]/as-album-track[1]", "album-detail.html"));

        // --- Scenario CINELIB: Catalog "search filters by title" (autorale 2026-06-29) ---
        elements.add(new element("SearchInput", "/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[1]/div[1]/input[1]", "cinelib-catalog-search.html"));
        elements.add(new element("Card", "/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]", "cinelib-catalog-search.html"));
        elements.add(new element("CardTitle", "/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]/div[1]/a[1]/h2[1]", "cinelib-catalog-search.html"));

        // --- Scenario CINELIB: Movie detail "/movie/3" Quiet Harbor (autorale 2026-07-09) ---
        elements.add(new element("DetailTitle", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/header[1]/div[2]/h1[1]", "cinelib-movie-detail.html"));
        elements.add(new element("CastRow", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]", "cinelib-movie-detail.html"));
        elements.add(new element("CastName", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]/span[2]", "cinelib-movie-detail.html"));

        // --- Scenario CINELIB: Movie form "/movie/new" (autorale 2026-07-09) ---
        elements.add(new element("FieldTitle", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[1]/input[1]", "cinelib-movie-form.html"));
        elements.add(new element("FieldGenre", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[2]/div[2]/select[1]", "cinelib-movie-form.html"));
        elements.add(new element("FormSubmit", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[7]/button[1]", "cinelib-movie-form.html"));

        // --- Scenario CINELIB: Reviews "/movie/3" (autorale 2026-07-09) ---
        elements.add(new element("ReviewsAverage", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/div[1]/span[1]", "cinelib-movie-detail.html"));
        elements.add(new element("ReviewAuthor", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/ul[1]/li[1]/div[1]/span[1]", "cinelib-movie-detail.html"));
        elements.add(new element("ReviewSubmit", "/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/form[1]/div[3]/button[1]", "cinelib-movie-detail.html"));

        // --- Scenario CINELIB: Stats "/stats" (autorale 2026-07-09) ---
        elements.add(new element("StatTotal", "/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/div[1]/div[1]/span[1]", "cinelib-stats.html"));
        elements.add(new element("TopTitle", "/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/section[2]/table[1]/tbody[1]/tr[1]/td[2]/a[1]", "cinelib-stats.html"));
        elements.add(new element("GenreCount", "/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/section[1]/div[1]/div[1]/span[2]", "cinelib-stats.html"));

        Robula robula = new Robula();
        for (element e: elements) {
            try {
                InputStream inputStream = RobulaPlus.class.getClassLoader().getResourceAsStream(e.source);

                if (inputStream == null) {
                    throw new IllegalArgumentException("File not found");
                }
                Document doc = Jsoup.parse(inputStream, "UTF-8", "");
                String abs = e.xpath;

                String xpath = robula.getRobustXPath(abs, doc);
                System.out.println("Generated XPath for " + e.name + ": " + xpath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
