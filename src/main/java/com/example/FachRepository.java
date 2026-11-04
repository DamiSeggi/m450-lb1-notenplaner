package com.example;

import java.util.List;
import java.util.Optional;

/** Datenzugriff für Fächer. */
public interface FachRepository {

    /** Sucht ein Fach über die id. */
    Optional<Fach> findById(int id);

    /** Gibt alle Fächer zurück. */
    List<Fach> findAll();

    /** Speichert ein Fach und gibt es mit neuer id zurück. */
    Fach save(Fach fach);
}
