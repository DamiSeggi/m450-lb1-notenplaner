package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatistikService")
class StatistikServiceTest {

    private static final int FACH_ID = 2;
    private static final LocalDate DATUM = LocalDate.of(2026, 11, 4);

    @Mock
    private NotenRepository notenRepository;

    @InjectMocks
    private StatistikService statistikService;

    /** Erstellt eine Note im getesteten Fach. */
    private static Note note(double wert) {
        return new Note(0, wert, DATUM, 1, FACH_ID);
    }

    @Test
    @DisplayName("durchschnittProFach: eine Note ergibt genau diese Note")
    void durchschnittProFach_eineNote_gibtDieseNoteZurueck() {
        // Arrange
        when(notenRepository.findByFachId(FACH_ID)).thenReturn(List.of(note(4.5)));

        // Act
        double durchschnitt = statistikService.durchschnittProFach(FACH_ID);

        // Assert
        assertEquals(4.5, durchschnitt);
    }

    @Test
    @DisplayName("durchschnittProFach: mehrere Noten ergeben den Mittelwert")
    void durchschnittProFach_mehrereNoten_gibtMittelwertZurueck() {
        // Arrange
        when(notenRepository.findByFachId(FACH_ID)).thenReturn(List.of(note(4.0), note(5.0), note(6.0)));

        // Act
        double durchschnitt = statistikService.durchschnittProFach(FACH_ID);

        // Assert
        assertEquals(5.0, durchschnitt);
    }

    @Test
    @DisplayName("durchschnittProFach: Fach ohne Noten wirft Exception")
    void durchschnittProFach_keineNoten_wirftException() {
        // Arrange
        when(notenRepository.findByFachId(FACH_ID)).thenReturn(List.of());

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> statistikService.durchschnittProFach(FACH_ID));

        // Assert
        assertEquals("Keine Noten für Fach 2", exception.getMessage());
    }
}
