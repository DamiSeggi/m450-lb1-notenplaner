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

    private static final LocalDate DATUM = LocalDate.of(2026, 11, 4);

    @Mock
    private NotenRepository notenRepository;

    @InjectMocks
    private StatistikService statistikService;

    @Test
    @DisplayName("durchschnittProFach: eine Note ergibt genau diese Note")
    void durchschnittProFach_eineNote_gibtDieseNoteZurueck() {
        // Arrange
        when(notenRepository.findByFachId(2)).thenReturn(List.of(new Note(1, 4.5, DATUM, 1, 2)));

        // Act
        double durchschnitt = statistikService.durchschnittProFach(2);

        // Assert
        assertEquals(4.5, durchschnitt);
    }

    @Test
    @DisplayName("durchschnittProFach: mehrere Noten ergeben den Mittelwert")
    void durchschnittProFach_mehrereNoten_gibtMittelwertZurueck() {
        // Arrange
        when(notenRepository.findByFachId(2)).thenReturn(List.of(
                new Note(1, 4.0, DATUM, 1, 2),
                new Note(2, 5.0, DATUM, 2, 2),
                new Note(3, 6.0, DATUM, 3, 2)));

        // Act
        double durchschnitt = statistikService.durchschnittProFach(2);

        // Assert
        assertEquals(5.0, durchschnitt);
    }

    @Test
    @DisplayName("durchschnittProFach: Fach ohne Noten wirft Exception")
    void durchschnittProFach_keineNoten_wirftException() {
        // Arrange
        when(notenRepository.findByFachId(2)).thenReturn(List.of());

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> statistikService.durchschnittProFach(2));

        // Assert
        assertEquals("Keine Noten für Fach 2", exception.getMessage());
    }
}
