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
  to a known category. Four operating modes: direct API, prompt dump, snippet ingest, dry run.
- **`MutationPorter`** (in `common`) — export and re-import of mutant sets as readable text
  files, so they can be inspected and edited outside the database.
- **Two purpose-built subject applications** (CineLib, FlowBoard), designed so that all eleven
  operators find the structural conditions to be instantiated.
- **[`mutazioni-generate/`](mutazioni-generate/)** — the complete set of **6,470 mutants**
  produced across four campaigns, exported in readable form and browsable directly here.

**Headline result** — the language-based generator produced valid mutants in 88.4% and 95.2%
of cases on the two subjects, against 79.4% and 41.5% for the static technique. More
importantly, the static generator's yield varies by nearly forty percentage points across
subjects while the language-based one stays within seven: measuring a generator on a single
application is not informative.

| Campaign | Subject | Technique | Mutants | Valid |
|---|---|---|---|---|
| [`cinelib-llm`](mutazioni-generate/cinelib-llm) | CineLib | LLM | 1,263 | 713 / 807 |
| [`cinelib-static`](mutazioni-generate/cinelib-static) | CineLib | static | 1,142 | 907 / 1,142 |
| [`flowboard-llm`](mutazioni-generate/flowboard-llm) | FlowBoard | LLM | 1,225 | 1,166 / 1,225 |
| [`flowboard-static`](mutazioni-generate/flowboard-static) | FlowBoard | static | 2,840 | 1,180 / 2,840 |

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
├── 📁 mutazioni-generate   // The 6,470 generated mutants, exported in readable form
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