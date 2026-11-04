package com.example;

import java.util.List;
import java.util.Optional;

/** Datenzugriff für Lernende. */
public interface LernenderRepository {

    /** Sucht einen Lernenden über die id. */
    Optional<Lernender> findById(int id);

    /** Gibt alle Lernenden zurück. */
    List<Lernender> findAll();

    /** Speichert einen Lernenden und gibt ihn mit neuer id zurück. */
    Lernender save(Lernender lernender);
}
