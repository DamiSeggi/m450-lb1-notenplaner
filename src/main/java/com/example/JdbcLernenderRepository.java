package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Liest und speichert Lernende in der H2-Datenbank. */
public class JdbcLernenderRepository implements LernenderRepository {

    private final Connection connection;

    public JdbcLernenderRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Lernender> findById(int id) {
        String sql = "SELECT id, vorname, nachname FROM lernender WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(lesen(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lernender konnte nicht gelesen werden", e);
        }
    }

    @Override
    public List<Lernender> findAll() {
        String sql = "SELECT id, vorname, nachname FROM lernender ORDER BY id";
        List<Lernender> lernende = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                lernende.add(lesen(resultSet));
            }
            return lernende;
        } catch (SQLException e) {
            throw new RuntimeException("Lernende konnten nicht gelesen werden", e);
        }
    }

    @Override
    public Lernender save(Lernender lernender) {
        String sql = "INSERT INTO lernender (vorname, nachname) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, lernender.vorname());
            statement.setString(2, lernender.nachname());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return new Lernender(keys.getInt(1), lernender.vorname(), lernender.nachname());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lernender konnte nicht gespeichert werden", e);
        }
    }

    /** Erstellt einen Lernenden aus der aktuellen Zeile. */
    private Lernender lesen(ResultSet resultSet) throws SQLException {
        return new Lernender(
                resultSet.getInt("id"),
                resultSet.getString("vorname"),
                resultSet.getString("nachname"));
    }
}
