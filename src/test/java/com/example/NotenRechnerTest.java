package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("NotenRechner")
class NotenRechnerTest {

    private final NotenRechner rechner = new NotenRechner();

    @ParameterizedTest(name = "{0} ist gültig")
    @ValueSource(doubles = {1.0, 4.0, 6.0})
    @DisplayName("istGueltig: Noten von 1.0 bis 6.0 sind gültig")
    void istGueltig_noteImBereich_gibtTrue(double note) {
        // Act
        boolean gueltig = rechner.istGueltig(note);

        // Assert
        assertTrue(gueltig);
    }

    @ParameterizedTest(name = "{0} ist ungültig")
    @ValueSource(doubles = {0.9, 6.1})
    @DisplayName("istGueltig: Noten unter 1.0 und über 6.0 sind ungültig")
    void istGueltig_noteAusserhalbBereich_gibtFalse(double note) {
        // Act
        boolean gueltig = rechner.istGueltig(note);

        // Assert
        assertFalse(gueltig);
    }

    @ParameterizedTest(name = "{0} ist genügend")
    @ValueSource(doubles = {4.0, 6.0})
    @DisplayName("istGenuegend: Noten ab 4.0 sind genügend")
    void istGenuegend_noteAbVier_gibtTrue(double note) {
        // Act
        boolean genuegend = rechner.istGenuegend(note);

        // Assert
        assertTrue(genuegend);
    }

    @ParameterizedTest(name = "{0} ist ungenügend")
    @ValueSource(doubles = {1.0, 3.9})
    @DisplayName("istGenuegend: Noten unter 4.0 sind ungenügend")
    void istGenuegend_noteUnterVier_gibtFalse(double note) {
        // Act
        boolean genuegend = rechner.istGenuegend(note);

        // Assert
        assertFalse(genuegend);
    }

    @ParameterizedTest(name = "{0} wird zu {1} gerundet")
    @CsvSource({
        "4.2, 4.0",
        "4.25, 4.5",
        "4.3, 4.5",
        "4.7, 4.5",
        "4.75, 5.0",
        "1.0, 1.0",
        "6.0, 6.0"
    })
    @DisplayName("rundeAufHalbe: rundet auf die nächste halbe Note")
    void rundeAufHalbe_note_gibtGerundeteNote(double note, double erwartet) {
        // Act
        double gerundet = rechner.rundeAufHalbe(note);

        // Assert
        assertEquals(erwartet, gerundet);
    }
}
