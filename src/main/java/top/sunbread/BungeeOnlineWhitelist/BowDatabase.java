package top.sunbread.BungeeOnlineWhitelist;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BowDatabase {

    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS `{prefix}whitelist` (id BIGINT AUTO_INCREMENT NOT NULL, uuid CHAR(32) NOT NULL UNIQUE, PRIMARY KEY (id)) DEFAULT CHARSET=utf8mb4";
    private static final String UUID_SELECT = "SELECT id FROM `{prefix}whitelist` WHERE uuid=?";
    private static final String UUID_INSERT = "INSERT INTO `{prefix}whitelist` (uuid) VALUES (?)";
    private static final String UUID_DELETE = "DELETE FROM `{prefix}whitelist` WHERE uuid=?";

    private HikariDataSource ds;
    private String prefix;

    public BowDatabase(String host, int port, String database, String username, String password, String prefix)
            throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(5));
        config.setInitializationFailTimeout(-1);
        config.setPoolName("BungeeOnlineWhitelist-Hikari");
        ds = new HikariDataSource(config);
        this.prefix = prefix;
        try (Connection connection = ds.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(replacePrefix(TABLE_CREATE));
        } catch (SQLException e) {
            shutdown();
            throw e;
        }
    }

    public synchronized void shutdown() {
        ds.close();
        ds = null;
    }

    public synchronized boolean isInWhitelist(UUID uuid) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(replacePrefix(UUID_SELECT));
            statement.setString(1, BowUtils.UUID2String(uuid));
            ResultSet result = statement.executeQuery();
            int count = 0;
            while (result.next()) ++count;
            result.close();
            return count > 0;
        }
    }

    public synchronized boolean addWhitelist(UUID uuid) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(replacePrefix(UUID_INSERT));
            statement.setString(1, BowUtils.UUID2String(uuid));
            int count = statement.executeUpdate();
            connection.close();
            return count > 0;
        }
    }

    public synchronized boolean removeWhitelist(UUID uuid) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(replacePrefix(UUID_DELETE));
            statement.setString(1, BowUtils.UUID2String(uuid));
            int count = statement.executeUpdate();
            connection.close();
            return count > 0;
        }
    }

    private String replacePrefix(String string) {
        return string.replace("{prefix}", prefix);
    }

}
