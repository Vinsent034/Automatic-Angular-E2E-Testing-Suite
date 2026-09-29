"""Genera le classi di test di robustezza di CineLib (5 scenari x 7 strategie = 35).

Stesso schema di genera_classi_cookbook.py e genera_classi_flowboard.py. I locatori stanno in
cinelib-v2/locatori/locatori.json: { elemento: { strategia: locatore } }; XPath nudo oppure con
prefisso id= / css= / link= / linkText= / name= / xpath=. Si generano solo le classi delle strategie
per cui tutti e tre gli elementi dello scenario hanno un locatore. Gli scenari riproducono quelli
delle classi di luglio (copia in cinelib-v2/classi-v1-backup/).
Uso: python cinelib-v2/genera_classi_cinelib.py
"""
import json
import os

SUITE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LOCATORI = os.path.join(SUITE, "cinelib-v2", "locatori", "locatori.json")
PKG_DIR = os.path.join(SUITE, "ext-test-classes", "src", "main", "java", "org", "ext", "cinelib")

STRATEGIE = {  # chiave nel JSON -> (nome della classe, valore di getLocator())
    "Absolute": ("AbsoluteXPathTest", "ABSOLUTE_LOCATOR"),
    "Relative": ("RelativeXPathTest", "RELATIVE_LOCATOR"),
    "Robula": ("RobulaXPathTest", "ROBULA_LOCATOR"),
    "RobulaPlus": ("RobulaPlusXPathTest", "ROBULA_PLUS_LOCATOR"),
    "Selenium": ("SeleniumXPathTest", "SELENIUM_LOCATOR"),
    "Katalon": ("KatalonXPathTest", "KATALON_LOCATOR"),
    "HookBased": ("HookXPathTest", "HOOK_LOCATOR"),
}

# Corpo di ogni scenario: {E1}, {E2}, {E3} diventano espressioni By.
SCENARI = {
    "catalogsearch": (["CS_Search", "CS_Card", "CS_CardTitle"], """
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated({E1}));
        s.clear(); s.sendKeys("quiet"); Thread.sleep(500);
        assertEquals(1, driver.findElements({E2}).size());
        assertEquals("Quiet Harbor", driver.findElement({E3}).getText());"""),
    "moviedetail": (["MD_Title", "MD_CastRow2", "MD_CastName2"], """
        driver.get(baseUrl + "movie/3");
        assertEquals("Quiet Harbor", wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).getText());
        assertEquals(1, driver.findElements({E2}).size());
        assertEquals("Sam Whitfield", driver.findElement({E3}).getText());"""),
    "movieform": (["MF_Title", "MF_Genre", "MF_Submit"], """
        driver.get(baseUrl + "movie/new");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).isDisplayed());
        assertTrue(driver.findElement({E2}).isDisplayed());
        assertEquals("Create movie", driver.findElement({E3}).getText());"""),
    "reviews": (["RV_Average", "RV_Author1", "RV_Submit"], """
        driver.get(baseUrl + "movie/3");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).isDisplayed());
        assertEquals("Dario", driver.findElement({E2}).getText());
        assertEquals("Post review", driver.findElement({E3}).getText());"""),
    "stats": (["ST_Total", "ST_TopTitle1", "ST_GenreCount1"], """
        driver.get(baseUrl + "stats");
        assertEquals("8", wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).getText());
        assertEquals("Quiet Harbor", driver.findElement({E2}).getText());
        assertEquals("2", driver.findElement({E3}).getText());"""),
}

TEMPLATE = """package org.ext.cinelib.{pkg};

import org.ext.cinelib.CineLibBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class {cls} extends CineLibBaseTest {{
    @Override
    public String getLocator() {{ return "{tag}"; }}

    @Test
    public void test() throws Exception {{{body}
    }}
}}
"""


def java_str(s):
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"') + '"'


def by(loc):
    for prefix, method in (("id=", "id"), ("css=", "cssSelector"), ("link=", "linkText"),
                           ("linkText=", "linkText"), ("name=", "name"), ("xpath=", "xpath")):
        if loc.startswith(prefix):
            return f"By.{method}({java_str(loc[len(prefix):])})"
    return f"By.xpath({java_str(loc)})"


def main():
    loc = json.load(open(LOCATORI, encoding="utf-8"))
    scritte = 0
    for pkg, (elementi, corpo) in SCENARI.items():
        os.makedirs(os.path.join(PKG_DIR, pkg), exist_ok=True)
        for chiave, (cls, tag) in STRATEGIE.items():
            valori = [loc.get(e, {}).get(chiave) for e in elementi]
            if not all(valori):
                continue
            body = corpo
            for i, v in enumerate(valori, 1):
                body = body.replace("{E%d}" % i, by(v))
            with open(os.path.join(PKG_DIR, pkg, cls + ".java"), "w", encoding="utf-8", newline="\n") as f:
                f.write(TEMPLATE.format(pkg=pkg, cls=cls, tag=tag, body=body))
            scritte += 1
    print(f"classi scritte: {scritte} / {len(SCENARI) * len(STRATEGIE)}")


if __name__ == "__main__":
    main()
