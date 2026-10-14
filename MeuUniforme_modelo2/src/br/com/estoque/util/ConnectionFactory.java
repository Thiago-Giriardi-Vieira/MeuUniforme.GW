package br.com.estoque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fabrica de conexoes com o MySQL.
 * Os dados de acesso vem de variaveis de ambiente (nao ficam mais no codigo):
 *   DB_URL      (padrao: jdbc:mysql://localhost:3306/meuuniformegw)
 *   DB_USER     (padrao: root)
 *   DB_PASSWORD (a senha do seu MySQL)
 */
public class ConnectionFactory {

    private static final String URL =
            System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/meuuniformegw");
    private static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", "104980thi");

    private ConnectionFactory() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com a base de dados!", e);
        }
    }
}
