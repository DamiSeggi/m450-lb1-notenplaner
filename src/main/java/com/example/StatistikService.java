package com.example;

/** Berechnet Statistiken zu Noten. */
public class StatistikService {

    private final NotenRepository notenRepository;

    public StatistikService(NotenRepository notenRepository) {
        this.notenRepository = notenRepository;
    }

    /** Gibt den Notendurchschnitt eines Fachs zurück. */
    public double durchschnittProFach(int fachId) {
        return 0.0;
    }
}
