package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Liest, speichert und löscht Noten in der H2-Datenbank. */
public class JdbcNotenRepository implements NotenRepository {

    private static final String SPALTEN = "id, wert, datum, lernender_id, fach_id";

    private final Connection connection;

    public JdbcNotenRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Note save(Note note) {
        String sql = "INSERT INTO note (wert, datum, lernender_id, fach_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, note.wert());
            statement.setObject(2, note.datum());
            statement.setInt(3, note.lernenderId());
            statement.setInt(4, note.fachId());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return new Note(keys.getInt(1), note.wert(), note.datum(), note.lernenderId(), note.fachId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Note konnte nicht gespeichert werden", e);
        }
    }

    @Override
    public Optional<Note> findById(int id) {
        List<Note> noten = suchen("SELECT " + SPALTEN + " FROM note WHERE id = ?", id);
        return noten.stream().findFirst();
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM note WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Note konnte nicht gelöscht werden", e);
        }
    }

    @Override
    public List<Note> findByLernenderId(int lernenderId) {
        return suchen("SELECT " + SPALTEN + " FROM note WHERE lernender_id = ? ORDER BY id", lernenderId);
    }

    @Override
    public List<Note> findByFachId(int fachId) {
        return suchen("SELECT " + SPALTEN + " FROM note WHERE fach_id = ? ORDER BY id", fachId);
    }

    /** Führt eine Abfrage mit einem Parameter aus und gibt die gefundenen Noten zurück. */
    private List<Note> suchen(String sql, int parameter) {
        List<Note> noten = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    noten.add(lesen(resultSet));
                }
            }
            return noten;
        } catch (SQLException e) {
            throw new RuntimeException("Noten konnten nicht gelesen werden", e);
        }
    }

    /** Erstellt eine Note aus der aktuellen Zeile. */
    private Note lesen(ResultSet resultSet) throws SQLException {
        return new Note(
                resultSet.getInt("id"),
                resultSet.getDouble("wert"),
                resultSet.getObject("datum", LocalDate.class),
                resultSet.getInt("lernender_id"),
                resultSet.getInt("fach_id"));
    }
}
