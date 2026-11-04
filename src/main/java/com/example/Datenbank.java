package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Öffnet die H2-Datenbank und legt die Tabellen an. */
public class Datenbank {

    public static final String URL = "jdbc:h2:file:./data/notenplaner";

    /** Öffnet eine Verbindung und legt fehlende Tabellen an. */
    public static Connection oeffnen(String url) throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        tabellenAnlegen(connection);
        return connection;
    }

    private static void tabellenAnlegen(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS lernender (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        vorname VARCHAR(100) NOT NULL,
                        nachname VARCHAR(100) NOT NULL
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS fach (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        bezeichnung VARCHAR(100) NOT NULL
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS note (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        wert DOUBLE NOT NULL,
                        datum DATE NOT NULL,
                        lernender_id INT NOT NULL REFERENCES lernender(id),
                        fach_id INT NOT NULL REFERENCES fach(id)
                    )""");
        }
    }
}
