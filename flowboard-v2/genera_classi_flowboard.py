"""Genera le classi di test di robustezza di FlowBoard v2 (6 scenari x 7 strategie).

I locatori stanno in flowboard-v2/locatori/locatori.json: { elemento: { strategia: locatore } }.
Formato dei locatori: XPath nudo, oppure con prefisso id= / css= / link= / name= / xpath=
(quelli prodotti da Katalon e Selenium IDE). Si generano solo le classi delle strategie per cui
tutti e tre gli elementi dello scenario hanno un locatore.
Uso: python flowboard-v2/genera_classi_flowboard.py
"""
import json
import os

SUITE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LOCATORI = os.path.join(SUITE, "flowboard-v2", "locatori", "locatori.json")
PKG_DIR = os.path.join(SUITE, "ext-test-classes", "src", "main", "java", "org", "ext", "flowboard")

STRATEGIE = {  # chiave nel JSON -> nome della classe
    "Absolute": "AbsoluteXPathTest",
    "Relative": "RelativeXPathTest",
    "Robula": "RobulaXPathTest",
    "RobulaPlus": "RobulaPlusXPathTest",
    "Selenium": "SeleniumXPathTest",
    "Katalon": "KatalonXPathTest",
    "HookBased": "HookXPathTest",
}

# Corpo di ogni scenario: {E1}, {E2}, {E3} diventano espressioni By.
SCENARI = {
    "s1": (["S1_Search", "S1_Card", "S1_CardTitle"], """
        driver.get(baseUrl);
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated({E1}));
        s.clear(); s.sendKeys("login"); Thread.sleep(700);
        driver.findElement({E2});
        assertEquals("Design login", driver.findElement({E3}).getText());"""),
    "s2": (["S2_ColumnCount", "S2_CardMove", "S2_ColumnInprogress"], """
        driver.get(baseUrl);
        assertEquals("4", wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).getText());
        driver.findElement({E2});
        driver.findElement({E3});"""),
    "s3": (["S3_FormTitle", "S3_FormPriority", "S3_FormSubmit"], """
        driver.get(baseUrl + "card/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated({E1}));
        driver.findElement({E2});
        assertEquals("Save card", driver.findElement({E3}).getText());"""),
    "s4": (["S4_DetailTitle", "S4_DetailAssignee", "S4_DetailDelete"], """
        driver.get(baseUrl + "card/3");
        assertEquals("Design login", wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).getText());
        assertEquals("Marco", driver.findElement({E2}).getText());
        driver.findElement({E3});"""),
    "s5": (["S5_StatTotal", "S5_StatBacklog", "S5_StatRow3"], """
        driver.get(baseUrl + "stats");
        assertEquals("10", wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).getText());
        assertEquals("4", driver.findElement({E2}).getText());
        driver.findElement({E3});"""),
    "s6": (["S6_NavStats", "S6_TotalBadge", "S6_FooterVersion"], """
        driver.get(baseUrl);
        assertEquals("Stats", wait.until(ExpectedConditions.visibilityOfElementLocated({E1})).getText());
        assertEquals("10", driver.findElement({E2}).getText());
        assertEquals("v1.0", driver.findElement({E3}).getText());"""),
}

TEMPLATE = """package org.ext.flowboard.{pkg};

import org.ext.flowboard.FlowBoardBaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.assertEquals;

public class {cls} extends FlowBoardBaseTest {{
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
        for chiave, cls in STRATEGIE.items():
            valori = [loc.get(e, {}).get(chiave) for e in elementi]
            if not all(valori):
                continue
            body = corpo
            for i, v in enumerate(valori, 1):
                body = body.replace("{E%d}" % i, by(v))
            with open(os.path.join(PKG_DIR, pkg, cls + ".java"), "w", encoding="utf-8", newline="\n") as f:
                f.write(TEMPLATE.format(pkg=pkg, cls=cls, body=body))
            scritte += 1
    print(f"classi scritte: {scritte} / {len(SCENARI) * len(STRATEGIE)}")


if __name__ == "__main__":
    main()
