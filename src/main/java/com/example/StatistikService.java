package com.example;

import java.util.List;

/** Berechnet Statistiken zu Noten. */
public class StatistikService {

    private final NotenRepository notenRepository;

    public StatistikService(NotenRepository notenRepository) {
        this.notenRepository = notenRepository;
    }

    /** Gibt den Notendurchschnitt eines Fachs zurück. */
    public double durchschnittProFach(int fachId) {
        List<Note> noten = notenRepository.findByFachId(fachId);
        double summe = 0;
        for (Note note : noten) {
            summe += note.wert();
        }
        return summe / noten.size();
    }
}
