package com.example;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/** Erfasst, löscht und liefert Noten. */
public class NotenService {

    private final NotenRepository notenRepository;
    private final LernenderRepository lernenderRepository;
    private final FachRepository fachRepository;
    private final NotenRechner notenRechner;

    public NotenService(NotenRepository notenRepository,
                        LernenderRepository lernenderRepository,
                        FachRepository fachRepository,
                        NotenRechner notenRechner) {
        this.notenRepository = notenRepository;
        this.lernenderRepository = lernenderRepository;
        this.fachRepository = fachRepository;
        this.notenRechner = notenRechner;
    }

    /** Prüft Lernenden, Fach und Wert und speichert die Note. */
    public Note noteErfassen(int lernenderId, int fachId, double wert, LocalDate datum) {
        if (lernenderRepository.findById(lernenderId).isEmpty()) {
            throw new NoSuchElementException("Lernender " + lernenderId + " existiert nicht");
        }
        if (fachRepository.findById(fachId).isEmpty()) {
            throw new NoSuchElementException("Fach " + fachId + " existiert nicht");
        }
        if (!notenRechner.istGueltig(wert)) {
            throw new IllegalArgumentException("Ungültige Note: " + wert);
        }
        return notenRepository.save(new Note(0, wert, datum, lernenderId, fachId));
    }

    /** Löscht die Note mit dieser id. */
    public void noteLoeschen(int noteId) {
        if (notenRepository.findById(noteId).isEmpty()) {
            throw new NoSuchElementException("Note " + noteId + " existiert nicht");
        }
        notenRepository.deleteById(noteId);
    }

    /** Gibt alle Noten eines Lernenden zurück. */
    public List<Note> notenVonLernendem(int lernenderId) {
        return notenRepository.findByLernenderId(lernenderId);
    }
}
