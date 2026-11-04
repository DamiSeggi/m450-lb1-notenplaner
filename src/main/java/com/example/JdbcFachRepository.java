package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Liest und speichert Fächer in der H2-Datenbank. */
public class JdbcFachRepository implements FachRepository {

    private final Connection connection;

    public JdbcFachRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Fach> findById(int id) {
        String sql = "SELECT id, bezeichnung FROM fach WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(lesen(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fach konnte nicht gelesen werden", e);
        }
    }

    @Override
    public List<Fach> findAll() {
        String sql = "SELECT id, bezeichnung FROM fach ORDER BY id";
        List<Fach> faecher = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                faecher.add(lesen(resultSet));
            }
            return faecher;
        } catch (SQLException e) {
            throw new RuntimeException("Fächer konnten nicht gelesen werden", e);
        }
    }

    @Override
    public Fach save(Fach fach) {
        String sql = "INSERT INTO fach (bezeichnung) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fach.bezeichnung());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return new Fach(keys.getInt(1), fach.bezeichnung());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fach konnte nicht gespeichert werden", e);
        }
    }

    /** Erstellt ein Fach aus der aktuellen Zeile. */
    private Fach lesen(ResultSet resultSet) throws SQLException {
        return new Fach(resultSet.getInt("id"), resultSet.getString("bezeichnung"));
    }
}
