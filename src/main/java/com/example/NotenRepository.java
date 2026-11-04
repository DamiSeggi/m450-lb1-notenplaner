package com.example;

import java.util.List;
import java.util.Optional;

/** Datenzugriff für Noten. */
public interface NotenRepository {

    /** Speichert eine Note und gibt sie mit neuer id zurück. */
    Note save(Note note);

    /** Sucht eine Note über die id. */
    Optional<Note> findById(int id);

    /** Löscht die Note mit dieser id. */
    void deleteById(int id);

    /** Gibt alle Noten eines Lernenden zurück. */
    List<Note> findByLernenderId(int lernenderId);

    /** Gibt alle Noten eines Fachs zurück. */
    List<Note> findByFachId(int fachId);
}
