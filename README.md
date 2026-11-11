# m450-lb1-notenplaner

Notenplaner für das Modul 450 „Applikationen testen“ (LB1). Lernende, Fächer und Noten werden in einer H2-Datenbank gespeichert. Getestet wird mit JUnit 5, Mockito und PIT.

- **Autoren:** Damian Segginger, Elias Oschwald, Jason Zeitz
- **Klasse:** INA24B

## Aufbau

| Klasse | Aufgabe |
|---|---|
| `Lernender`, `Fach`, `Note` | Datenklassen (Records) |
| `LernenderRepository`, `FachRepository`, `NotenRepository` | Interfaces für den Datenzugriff |
| `JdbcLernenderRepository`, `JdbcFachRepository`, `JdbcNotenRepository` | Datenzugriff über JDBC auf H2 |
| `Datenbank` | Öffnet die H2-Datenbank und legt die Tabellen an |
| `NotenRechner` | Prüft und rundet Noten (Teil 2) |
| `NotenService` | Erfasst, löscht und liefert Noten (Teil 1) |
| `StatistikService` | Notendurchschnitt pro Fach, per TDD entwickelt (Teil 4) |
| `App` | Startet das Programm und zeigt Beispieldaten an |

## Befehle

| Was | Befehl |
|---|---|
| Tests ausführen | `mvn test` |
| Mutation-Testing | `mvn clean test-compile pitest:mutationCoverage` |
| PIT-Report | `target/pit-reports/index.html` |
| App starten | `mvn compile exec:java -Dexec.mainClass=com.example.App` |

## Pipeline

`.github/workflows/tests.yml` läuft bei jedem Push und Pull Request auf `main`:

1. `mvn -B test`
2. `mvn -B test-compile pitest:mutationCoverage`
3. Der PIT-Report wird als Artefakt `pit-report` hochgeladen.

## Testfall-Design

- **Black-Box:** Positiv- und Negativtests, Äquivalenzklassen und Grenzwertanalyse. Beispiel `istGueltig` (gültig von 1.0 bis 6.0):

  | Wert | -1.0 | 0.0 | 0.9 | 1.0 | 1.1 | 3.5 | 5.9 | 6.0 | 6.1 | 7.0 | NaN |
  |---|---|---|---|---|---|---|---|---|---|---|---|
  | Testfall | negativ | zu tief | min − 1 | min | min + 1 | typisch | max − 1 | max | max + 1 | zu hoch | keine Zahl |
  | Erwartet | false | false | false | true | true | true | true | true | false | false | false |

- **White-Box:** Zeilenabdeckung 100 %. Bei `noteErfassen` sind alle 4 Pfade getestet: Lernender fehlt, Fach fehlt, Note ungültig, Erfolg.
- **Mocking:** Im `NotenServiceTest` und `StatistikServiceTest` sind alle Abhängigkeiten gemockt. Die Datenbank wird nie kontaktiert.

## TDD (StatistikService)

| Zyklus | Test | Umsetzung |
|---|---|---|
| 1 | Eine Note ergibt genau diese Note | Wert der ersten Note zurückgeben |
| 2 | 4.0, 5.0 und 6.0 ergeben 5.0 | Summe / Anzahl, danach als Stream vereinfacht |
| 3 | Fach ohne Noten wirft `IllegalStateException` | `orElseThrow` statt `NaN` |

Jeder Schritt (RED, GREEN, REFACTOR) ist ein eigener Commit. In GitHub Actions ist der Wechsel von rot zu grün sichtbar.

## Mutation-Testing (PIT)

Mutiert werden `NotenService` und `NotenRechner`. Ergebnis: **18 von 18 Mutanten getötet (100 %)**, Zeilenabdeckung 100 %.

Aktive Mutatoren (PIT-Standard):

| Mutator | Was er ändert | Beispiel im Code | Getötet durch |
|---|---|---|---|
| CONDITIONALS_BOUNDARY | Grenze einer Bedingung | `note >= 4.0` wird `note > 4.0` (`istGenuegend`) | Test mit 4.0 erwartet `true` |
| NEGATE_CONDITIONALS | kehrt eine Bedingung um | `if (...isEmpty())` wird `if (!...isEmpty())` (`noteErfassen`) | Test „Lernender fehlt“ erwartet eine Exception |
| MATH | tauscht Rechenzeichen | `note * 2` wird `note / 2` (`rundeAufHalbe`) | Rundungstests, z. B. 4.3 → 4.5 |
| VOID_METHOD_CALLS | entfernt einen void-Aufruf | `deleteById(noteId)` fällt weg (`noteLoeschen`) | `verify(notenRepository).deleteById(10)` |
| TRUE_RETURNS | gibt immer `true` zurück | `istGueltig` liefert immer `true` | Test mit 0.9 erwartet `false` |
| NULL_RETURNS | gibt `null` zurück | `noteErfassen` liefert `null` | `assertEquals(gespeicherteNote, ergebnis)` |
| EMPTY_RETURNS | gibt eine leere Liste zurück | `notenVonLernendem` liefert `[]` | Test mit zwei Noten vergleicht die Liste |
| PRIMITIVE_RETURNS | gibt `0` zurück | `rundeAufHalbe` liefert `0.0` | Rundungstests |
| FALSE_RETURNS, INCREMENTS, INVERT_NEGS | aktiv, erzeugen in diesem Code keine Mutanten | – | – |
