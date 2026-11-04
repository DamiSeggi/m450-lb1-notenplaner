package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("NotenRechner")
class NotenRechnerTest {

    private final NotenRechner rechner = new NotenRechner();

    @ParameterizedTest(name = "{0} -> {1} ({2})")
    @CsvSource({
        "0.0, false, Äquivalenzklasse zu tief",
        "0.9, false, min - 1",
        "1.0, true, min",
        "1.1, true, min + 1",
        "3.5, true, typischer Wert",
        "5.9, true, max - 1",
        "6.0, true, max",
        "6.1, false, max + 1",
        "7.0, false, Äquivalenzklasse zu hoch",
        "NaN, false, keine Zahl"
    })
    @DisplayName("istGueltig: gültig von 1.0 bis 6.0")
    void istGueltig_grenzwerte(double note, boolean erwartet, String testfall) {
        // Act
        boolean gueltig = rechner.istGueltig(note);

        // Assert
        assertEquals(erwartet, gueltig);
    }

    @ParameterizedTest(name = "{0} -> {1} ({2})")
    @CsvSource({
        "1.0, false, min ungenügend",
        "2.5, false, typischer Wert ungenügend",
        "3.9, false, Grenze - 0.1",
        "4.0, true, Grenze",
        "4.1, true, Grenze + 0.1",
        "5.0, true, typischer Wert genügend",
        "6.0, true, max genügend"
    })
    @DisplayName("istGenuegend: genügend ab 4.0")
    void istGenuegend_grenzwerte(double note, boolean erwartet, String testfall) {
        // Act
        boolean genuegend = rechner.istGenuegend(note);

        // Assert
        assertEquals(erwartet, genuegend);
    }

    @ParameterizedTest(name = "{0} -> {1} ({2})")
    @CsvSource({
        "1.0, 1.0, min",
        "4.2, 4.0, typischer Wert abrunden",
        "4.24, 4.0, Rundungsgrenze .25 - 0.01",
        "4.25, 4.5, Rundungsgrenze .25",
        "4.26, 4.5, Rundungsgrenze .25 + 0.01",
        "4.3, 4.5, typischer Wert aufrunden",
        "4.5, 4.5, bereits halbe Note",
        "4.74, 4.5, Rundungsgrenze .75 - 0.01",
        "4.75, 5.0, Rundungsgrenze .75",
        "4.76, 5.0, Rundungsgrenze .75 + 0.01",
        "6.0, 6.0, max"
    })
    @DisplayName("rundeAufHalbe: rundet auf die nächste halbe Note")
    void rundeAufHalbe_grenzwerte(double note, double erwartet, String testfall) {
        // Act
        double gerundet = rechner.rundeAufHalbe(note);

        // Assert
        assertEquals(erwartet, gerundet);
    }
}
