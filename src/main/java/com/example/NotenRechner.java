package com.example;

/** Prüft und rundet Noten. */
public class NotenRechner {

    /** Gibt true zurück, wenn die Note zwischen 1.0 und 6.0 liegt. */
    public boolean istGueltig(double note) {
        return note >= 1.0 && note <= 6.0;
    }

    /** Gibt true zurück, wenn die Note mindestens 4.0 ist. */
    public boolean istGenuegend(double note) {
        return note >= 4.0;
    }

    /** Rundet die Note auf die nächste halbe Note. */
    public double rundeAufHalbe(double note) {
        return Math.round(note * 2) / 2.0;
    }
}
