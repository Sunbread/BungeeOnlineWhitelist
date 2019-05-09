package top.sunbread.BungeeOnlineWhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class BowConfig {

    private Configuration config;
    private BaseComponent[] whitelistMessage;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private String tablePrefix;

    public BowConfig(File configFile) throws IOException {
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        whitelistMessage = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                config.getString("whitelist-message")));
        mysqlHost = config.getString("mysql-host");
        mysqlPort = config.getInt("mysql-port");
        mysqlDatabase = config.getString("mysql-database");
        mysqlUsername = config.getString("mysql-username");
        mysqlPassword = config.getString("mysql-password");
        tablePrefix = config.getString("table-prefix");
    }

    public BaseComponent[] getWhitelistMessage() {
        return whitelistMessage.clone();
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

}
