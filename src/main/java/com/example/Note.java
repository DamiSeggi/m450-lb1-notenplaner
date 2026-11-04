package com.example;

import java.time.LocalDate;

/** Note eines Lernenden in einem Fach. */
public record Note(int id, double wert, LocalDate datum, int lernenderId, int fachId) {
}
