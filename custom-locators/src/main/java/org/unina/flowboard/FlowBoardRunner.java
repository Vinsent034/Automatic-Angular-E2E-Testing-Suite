package org.unina.flowboard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.unina.robula.Robula;
import org.unina.robulaplus.RobulaPlus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;

/**
 * FlowBoard baseline: computes Robula and Robula+ robust XPaths for the 18 target elements.
 * Reads a TSV (name \t absoluteXPath \t sourceHtmlFile) and the source DOM dumps from resources.
 * Does NOT touch the shared Robula/RobulaPlus main() entry lists.
 * Run: java -cp "target/classes;<deps>" org.unina.flowboard.FlowBoardRunner <path-to-flowboard-elements.tsv>
 */
public class FlowBoardRunner {
    public static void main(String[] args) throws Exception {
        String tsv = args.length > 0 ? args[0] : "flowboard-elements.tsv";
        Robula robula = new Robula();
        RobulaPlus robulaPlus = new RobulaPlus();
        System.out.println("Name\tRobula\tRobulaPlus");
        try (BufferedReader br = new BufferedReader(new FileReader(tsv, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\t", -1);
                String name = p[0], abs = p[1], source = p[2];
                InputStream in = RobulaPlus.class.getClassLoader().getResourceAsStream(source);
                if (in == null) { System.out.println(name + "\tSOURCE-NOT-FOUND(" + source + ")\t-"); continue; }
                Document doc = Jsoup.parse(in, "UTF-8", "");
                String rob, robp;
                try { rob = robula.getRobustXPath(abs, doc); } catch (Exception e) { rob = "ERR:" + e.getMessage(); }
                try { robp = robulaPlus.getRobustXPath(abs, doc); } catch (Exception e) { robp = "ERR:" + e.getMessage(); }
                System.out.println(name + "\t" + rob + "\t" + robp);
            }
        }
    }
}
