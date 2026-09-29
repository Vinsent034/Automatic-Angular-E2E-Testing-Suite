# Automatic Angular E2E Testing Suite
A comprehensive automation tool designed to create and test mutations for HTML files within Angular repositories. This project streamlines the process of End-to-End (E2E) testing by generating targeted mutations and validating them against your application.

---

## About this fork

This repository extends the original suite developed by S. Liberti with a **second mutant
generator based on Large Language Models**, and with the experimental campaigns that assess it.
It is the software companion of the thesis *Generazione di mutanti per applicazioni web
template-based mediante modelli linguistici di grandi dimensioni* (V. Di Carluccio,
Università degli Studi di Napoli Federico II, A.A. 2025–2026).

**What was added**

- **`llm-generator`** — mutant generation delegated to a language model, anchored to the
  taxonomy of 11 mutation operators × 5 structural roles, so that every mutant produced maps
  to a known category. Operating modes: direct API, prompt dump, response ingest, dry run;
  operators f, g, h (pure moves) are generated mechanically. Duplicates are discarded at ingest.
- **`MutationPorter`** (in `common`) — export and re-import of mutant sets as readable text
  files, so they can be inspected and edited outside the database.
- **[`applicazioni-soggetto/`](applicazioni-soggetto)** — the three purpose-built Angular subject
  applications (CineLib, FlowBoard, CookBook), designed so that all eleven operators and all five
  roles can be exercised.
- **[`mutazioni-generate/`](mutazioni-generate)** — the **8,978 mutants** used in the six
  campaigns, exported in readable form and browsable directly here.
- **[`RIPRODURRE-LE-CAMPAGNE.md`](RIPRODURRE-LE-CAMPAGNE.md)** — step-by-step guide (in Italian)
  to regenerate the mutants, rerun the tests with the seven locator strategies and recompute the
  measures. The prompts and the model responses used in the thesis are kept in each campaign
  folder, so the LLM mutants can be rebuilt without querying the model again.

**Headline result** — on all three subjects the language-based generator produced a higher
share of valid mutants than the static one (88.0–95.6% against 72.9–82.3%, an advantage of
11.7 to 18.1 percentage points), and far fewer uninformative test runs (1.3–2.8% against
10.2–24.1%). The robustness ranking is stable at its extremes: absolute locators are always the
most fragile, ROBULA+ the most robust among the strategies that do not use test attributes.

| Campaign | Subject | Technique | Mutants | Valid | Campaign folder |
|---|---|---|---|---|---|
| [`cinelib-llm`](mutazioni-generate/cinelib-llm) | CineLib | LLM | 944 | 831 | `cinelib-v2-run/` |
| [`cinelib-static`](mutazioni-generate/cinelib-static) | CineLib | static | 944 | 688 | `cinelib-v2-static-run/` |
| [`flowboard-llm`](mutazioni-generate/flowboard-llm) | FlowBoard | LLM | 1,559 | 1,491 | `flowboard-v2-run/` |
| [`flowboard-static`](mutazioni-generate/flowboard-static) | FlowBoard | static | 2,148 | 1,664 | `flowboard-v2-static-run/` |
| [`cookbook-llm`](mutazioni-generate/cookbook-llm) | CookBook | LLM | 1,929 | 1,813 | `cookbook-run/` |
| [`cookbook-static`](mutazioni-generate/cookbook-static) | CookBook | static | 1,454 | 1,197 | `cookbook-static-run/` |

Static counts exclude duplicate mutants (see `mutazioni-generate/README.md`). Folders such as
`cinelib-static-run/`, `flowboard-run/`, `flowboard-static-run/` and `output/` hold earlier
campaigns (July 2026, previous version of the subjects) and are kept for history only; they are
not used in the thesis results.

The original documentation below still applies to the shared parts of the tool.

---

## Project Structure
The project relies on a modular architecture:
```txt
(root)
├── 📁 custom-locators      // Module for creating locators not present in Katalon/Selenium
├── 📁 hook-injector        // Module to inject custom hooks into the application under test
├── 📁 mutation-generator
|   ├── 📁 common           // Shared logic and utilities
|   ├── 📁 llm-generator    // Mutation generator via LLM prompts
|   └── 📁 static-generator // Mutation generator using static analysis
├── 📁 mutation-tester      // Module to execute automatic tests on generated mutations
├── 📁 applicazioni-soggetto // The three subject applications (CineLib, FlowBoard, CookBook)
├── 📁 mutazioni-generate   // The 8,978 mutants of the six campaigns, in readable form
├── generator-config.json   // Main configuration file for the suite
└── pom.xml                 // Main Maven module file
```

## Getting started
### Prerequisites
- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- Target project: a front-end project based on [AngularJS](https://angularjs.org/).

### Releases
You can run this tool by either building it from source or by using the pre-compiled binaries.
If you want to skip the build process, you can download the ready-to-use .jar files directly from the Releases section of this repository. Once downloaded, place the `.jar` files in the project root and proceed directly to configuration and execution.

### Configuration
To start using the tool, configure the `generator-config.json` file.

**Configuration parameters**
- `seed`: (Optional) A seed used to initialize the [RandomSelector](https://github.com/sim-liberti/Automatic-Angular-E2E-Testing-Suite/blob/master/mutation-generator/common/src/main/java/org/unina/util/RandomSelector.java) to ensure reproducible results. Leave blank for random execution.
- `repositoryRootPath`: The absolute path to the Angular project you wish to mutate.
- `npmRunCommand`: The command you use to run the Angular application (eg: `npm run dev`)
- `mutations`: An array of objects defining the mutation rules.
  - `name`: The name of the mutation.
  - `file_path`: The absolute path of the file where the tag to mutate is located.
  - `target_matcher`: Object used to locate the tag to mutate inside the file specified above
    - `type`: The type of matcher to be used. You can choose between `class`,`text`,`id`,`attribute`.
    - `key`: The key of the attribute of the target element. Only necessary if you choose the `attribute` type.
    - `value`: The value of the class, text, id or attribute of the target element.

> **NOTE:** All the shell commands in the following guide are written to be executed from the project root, referred to as (root).

### Generate Mutations
With the configuration in place, you need to compile the generator module and then execute it. If you downloaded the pre-compiled .jar file, skip to step 2.

**Step 1: Build the Module**

Run the following Maven command to build specifically the static-generator module and its dependencies:
```bash
mvn clean install -pl :static-generator -am
```
After a successful build, the compiled .jar file will be created at `(root)/mutation-generator/static-generator/target/static-generator-1.0.0-jar-with-dependencies.jar`. Copy the created file to the project root.

**Step 2: Run the Generator**

After downloading or compiling the .jar file, execute it with:
```bash
java -jar static-generator.jar
```
_Note: if you compiled the binary, use `static-generator-1.0.0-jar-with-dependencies.jar`_

A `mutations.db` file will be generated at the project root. This database stores every mutation, including its name, type, ID, and the associated file path.

### Test the application
With the configuration and the generated mutations in place, you need to compile the tester module. If you downloaded the pre-compiled .jar, skip to step 2.

**Prerequisites:**
- **Compilation:** all test classes, including base classes and dependencies, must be compiled
- **Framework:** tests must be written using **JUnit**
- **Dependencies:** ensure all required classes are present in the build path

**Step 1: Build the Module**

Run the following Maven command to build specifically the static-generator module and its dependencies:
```bash
mvn clean install -pl :mutation-tester -am
```
After a successful build, the compiled .jar file will be created at `(root)/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar`. Copy the created file to the project root.

**Step 2: Run the Tester**

After downloading or compiling the .jar file, execute it with:
```bash
java -jar mutation-tester.jar -td "path/to/your/compiled/test/classes"
```
_Note: if you compiled the binary, use `mutation-tester-1.0.0-jar-with-dependencies.jar`_

**Test Results:** 

Upon completion, two files are generated in the output folder:
- `stats.csv`: Results grouped by test class name (fragility, obsolescence, and skipped tests). 
- `batches.csv`: A detailed log of every test execution, including results and error messages.

### Secondary modules
For advanced usage regarding custom locators or hook injection, please refer to the specific module documentation:
- [Custom Locators Documentation](custom-locators/README.md)
- [Hook Injector Documentation](hook-injector/README.md)

## Tests on the Angular-Spotify application
To learn how to set up the [Angular-Spotify](https://github.com/trungvose/angular-spotify) application and simulate my results in the test-suite folder, refer to the appropriate [readme file](test-suite/AnuglarSpotifyTests.md).