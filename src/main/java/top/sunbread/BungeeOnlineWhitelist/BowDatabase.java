package top.sunbread.BungeeOnlineWhitelist;

import java.sql.*;
import java.util.UUID;

public final class BowDatabase {

    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS `{prefix}whitelist` (id INT AUTO_INCREMENT NOT NULL, uuid CHAR(32) NOT NULL UNIQUE, PRIMARY KEY (id)) DEFAULT CHARSET=utf8mb4";
    private static final String UUID_SELECT = "SELECT id FROM `{prefix}whitelist` WHERE uuid=?";
    private static final String UUID_INSERT = "INSERT INTO `{prefix}whitelist` (uuid) VALUES (?)";
    private static final String UUID_DELETE = "DELETE FROM `{prefix}whitelist` WHERE uuid=?";

    private Connection connection;
    private String prefix;

    public BowDatabase(String host, int port, String database, String username, String password, String prefix)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true",
                username, password);
        this.prefix = prefix;
        Statement statement = connection.createStatement();
        statement.executeUpdate(replacePrefix(TABLE_CREATE));
    }

    public boolean isInWhitelist(UUID uuid) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(replacePrefix(UUID_SELECT));
        statement.setString(1, BowUtils.UUID2String(uuid));
        ResultSet result = statement.executeQuery();
        int count = 0;
        while (result.next()) ++count;
        result.close();
        return count > 0;
    }

    public boolean addWhitelist(UUID uuid) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(replacePrefix(UUID_INSERT));
        statement.setString(1, BowUtils.UUID2String(uuid));
        int count = statement.executeUpdate();
        return count > 0;
    }

    public boolean removeWhitelist(UUID uuid) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(replacePrefix(UUID_DELETE));
        statement.setString(1, BowUtils.UUID2String(uuid));
        int count = statement.executeUpdate();
        return count > 0;
    }

    private String replacePrefix(String string) {
        return string.replace("{prefix}", prefix);
    }

}
