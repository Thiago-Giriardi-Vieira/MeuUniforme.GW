import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public Connection getConnection() {
        try {
            // O caminho deve ir direto da porta (3306) para o nome da base de dados (meuuniformegw)
            String url = "jdbc:mysql://localhost:3306/meuuniformegw";
            String usuario = "root";
            String senha = "104980thi";

            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com a base de dados!", e);
        }
    }
}
