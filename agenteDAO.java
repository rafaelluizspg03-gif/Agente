package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class agenteDAO implements AutoCloseable {
    private final Connection conexao;

    public agenteDAO(String nomeBanco) {
        try {
            Path caminho = Path.of("db", nomeBanco + ".db").toAbsolutePath();
            Files.createDirectories(caminho.getParent());
            conexao = DriverManager.getConnection("jdbc:sqlite:" + caminho);
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Falha ao abrir o banco", e);
        }

        try {
            criarTabela();
        } catch (RuntimeException erro) {
            try {
                conexao.close();
            } catch (SQLException falhaAoFechar) {
                erro.addSuppressed(falhaAoFechar);
            }
            throw erro;
        }
    }

    private void criarTabela() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Agentes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                idade INTEGER NOT NULL CHECK(idade >= 0),
                altura REAL NOT NULL CHECK(altura > 0)
            )
            """;
        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao criar a tabela", e);
        }
    }

    public int inserirAgente(agente agente) {
        String sql = "INSERT INTO Agentes (nome, idade, altura) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, agente.getNome());
            ps.setInt(2, agente.getIdade());
            ps.setDouble(3, agente.getAltura());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    int id = chaves.getInt(1);
                    agente.setId(id);
                    return id;
                }
            }
            throw new IllegalStateException("ID do agente não retornado.");
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao inserir agente", e);
        }
    }

    public agente buscarPorId(int id) {
        String sql = "SELECT id, nome, idade, altura FROM Agentes WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearAgente(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao buscar agente", e);
        }
    }

    public List<agente> listarTodas() {
        List<agente> agentes = new ArrayList<>();
        String sql = "SELECT id, nome, idade, altura FROM Agentes ORDER BY id";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                agentes.add(mapearAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao listar agentes", e);
        }
    }

    private agente mapearAgente(ResultSet rs) throws SQLException {
        return new agente(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getInt("idade"),
            rs.getDouble("altura")
        );
    }

    public boolean atualizarAgente(int id, String nome, int idade, double altura) {
        String sql = "UPDATE Agentes SET nome = ?, idade = ?, altura = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, idade);
            ps.setDouble(3, altura);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao atualizar agente", e);
        }
    }

    public boolean excluirAgente(int id) {
        String sql = "DELETE FROM Agentes WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao excluir agente", e);
        }
    }

    @Override
    public void close() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao fechar a conexão com o banco", e);
        }
    }
}
