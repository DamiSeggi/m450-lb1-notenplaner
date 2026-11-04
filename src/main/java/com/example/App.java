package com.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/** Startet den Notenplaner und zeigt die gespeicherten Noten an. */
public class App {

    public static void main(String[] args) throws SQLException {
        try (Connection connection = Datenbank.oeffnen(Datenbank.URL)) {
            LernenderRepository lernenderRepository = new JdbcLernenderRepository(connection);
            FachRepository fachRepository = new JdbcFachRepository(connection);
            NotenRepository notenRepository = new JdbcNotenRepository(connection);
            NotenRechner notenRechner = new NotenRechner();
            NotenService notenService = new NotenService(
                    notenRepository, lernenderRepository, fachRepository, notenRechner);

            if (lernenderRepository.findAll().isEmpty()) {
                beispieldatenAnlegen(lernenderRepository, fachRepository, notenService);
            }
            notenAnzeigen(lernenderRepository, fachRepository, notenService, notenRechner);
        }
    }

    /** Legt Lernende, Fächer und Noten als Beispieldaten an. */
    private static void beispieldatenAnlegen(LernenderRepository lernenderRepository,
                                             FachRepository fachRepository,
                                             NotenService notenService) {
        Lernender anna = lernenderRepository.save(new Lernender(0, "Anna", "Muster"));
        Lernender ben = lernenderRepository.save(new Lernender(0, "Ben", "Beispiel"));
        Fach mathe = fachRepository.save(new Fach(0, "Mathematik"));
        Fach deutsch = fachRepository.save(new Fach(0, "Deutsch"));

        notenService.noteErfassen(anna.id(), mathe.id(), 5.5, LocalDate.of(2026, 9, 15));
        notenService.noteErfassen(anna.id(), deutsch.id(), 4.5, LocalDate.of(2026, 9, 22));
        notenService.noteErfassen(ben.id(), mathe.id(), 3.5, LocalDate.of(2026, 9, 15));
        notenService.noteErfassen(ben.id(), deutsch.id(), 5.0, LocalDate.of(2026, 9, 22));
    }

    /** Gibt alle Lernenden mit ihren Noten aus. */
    private static void notenAnzeigen(LernenderRepository lernenderRepository,
                                      FachRepository fachRepository,
                                      NotenService notenService,
                                      NotenRechner notenRechner) {
        for (Lernender lernender : lernenderRepository.findAll()) {
            System.out.println(lernender.vorname() + " " + lernender.nachname());
            for (Note note : notenService.notenVonLernendem(lernender.id())) {
                String fach = fachRepository.findById(note.fachId()).orElseThrow().bezeichnung();
                String bewertung = notenRechner.istGenuegend(note.wert()) ? "genügend" : "ungenügend";
                System.out.println("  " + fach + ": " + note.wert() + " (" + note.datum() + ", " + bewertung + ")");
            }
        }
    }
}
