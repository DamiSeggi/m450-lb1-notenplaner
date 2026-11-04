package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotenService")
class NotenServiceTest {

    private static final LocalDate DATUM = LocalDate.of(2026, 11, 4);
    private static final Lernender ANNA = new Lernender(1, "Anna", "Muster");
    private static final Fach MATHE = new Fach(2, "Mathematik");

    @Mock
    private NotenRepository notenRepository;

    @Mock
    private LernenderRepository lernenderRepository;

    @Mock
    private FachRepository fachRepository;

    @Mock
    private NotenRechner notenRechner;

    @InjectMocks
    private NotenService notenService;

    @Test
    @DisplayName("noteErfassen: gültige Note wird gespeichert und zurückgegeben")
    void noteErfassen_allesGueltig_speichertUndGibtNoteZurueck() {
        // Arrange
        Note neueNote = new Note(0, 5.5, DATUM, 1, 2);
        Note gespeicherteNote = new Note(10, 5.5, DATUM, 1, 2);
        when(lernenderRepository.findById(1)).thenReturn(Optional.of(ANNA));
        when(fachRepository.findById(2)).thenReturn(Optional.of(MATHE));
        when(notenRechner.istGueltig(5.5)).thenReturn(true);
        when(notenRepository.save(neueNote)).thenReturn(gespeicherteNote);

        // Act
        Note ergebnis = notenService.noteErfassen(1, 2, 5.5, DATUM);

        // Assert
        assertEquals(gespeicherteNote, ergebnis);
        verify(notenRepository).save(neueNote);
    }

    @Test
    @DisplayName("noteErfassen: unbekannter Lernender wirft Exception, nichts wird gespeichert")
    void noteErfassen_lernenderFehlt_wirftException() {
        // Arrange
        when(lernenderRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> notenService.noteErfassen(1, 2, 5.5, DATUM));

        // Assert
        assertEquals("Lernender 1 existiert nicht", exception.getMessage());
        verify(notenRepository, never()).save(any());
    }

    @Test
    @DisplayName("noteErfassen: unbekanntes Fach wirft Exception, nichts wird gespeichert")
    void noteErfassen_fachFehlt_wirftException() {
        // Arrange
        when(lernenderRepository.findById(1)).thenReturn(Optional.of(ANNA));
        when(fachRepository.findById(2)).thenReturn(Optional.empty());

        // Act
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> notenService.noteErfassen(1, 2, 5.5, DATUM));

        // Assert
        assertEquals("Fach 2 existiert nicht", exception.getMessage());
        verify(notenRepository, never()).save(any());
    }

    @Test
    @DisplayName("noteErfassen: ungültige Note wirft Exception, nichts wird gespeichert")
    void noteErfassen_noteUngueltig_wirftException() {
        // Arrange
        when(lernenderRepository.findById(1)).thenReturn(Optional.of(ANNA));
        when(fachRepository.findById(2)).thenReturn(Optional.of(MATHE));
        when(notenRechner.istGueltig(6.5)).thenReturn(false);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notenService.noteErfassen(1, 2, 6.5, DATUM));

        // Assert
        assertEquals("Ungültige Note: 6.5", exception.getMessage());
        verify(notenRepository, never()).save(any());
    }

    @Test
    @DisplayName("noteLoeschen: vorhandene Note wird gelöscht")
    void noteLoeschen_noteExistiert_loeschtNote() {
        // Arrange
        when(notenRepository.findById(10)).thenReturn(Optional.of(new Note(10, 5.5, DATUM, 1, 2)));

        // Act
        notenService.noteLoeschen(10);

        // Assert
        verify(notenRepository).deleteById(10);
    }

    @Test
    @DisplayName("noteLoeschen: unbekannte Note wirft Exception, nichts wird gelöscht")
    void noteLoeschen_noteFehlt_wirftException() {
        // Arrange
        when(notenRepository.findById(10)).thenReturn(Optional.empty());

        // Act
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> notenService.noteLoeschen(10));

        // Assert
        assertEquals("Note 10 existiert nicht", exception.getMessage());
        verify(notenRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("notenVonLernendem: gibt die Noten des Lernenden zurück")
    void notenVonLernendem_lernenderHatNoten_gibtNotenZurueck() {
        // Arrange
        List<Note> noten = List.of(
                new Note(10, 5.5, DATUM, 1, 2),
                new Note(11, 4.0, DATUM, 1, 3));
        when(notenRepository.findByLernenderId(1)).thenReturn(noten);

        // Act
        List<Note> ergebnis = notenService.notenVonLernendem(1);

        // Assert
        assertEquals(noten, ergebnis);
    }

    @Test
    @DisplayName("notenVonLernendem: Lernender ohne Noten ergibt leere Liste")
    void notenVonLernendem_keineNoten_gibtLeereListeZurueck() {
        // Arrange
        when(notenRepository.findByLernenderId(1)).thenReturn(List.of());

        // Act
        List<Note> ergebnis = notenService.notenVonLernendem(1);

        // Assert
        assertTrue(ergebnis.isEmpty());
    }
}
