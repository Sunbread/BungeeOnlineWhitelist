package top.sunbread.BungeeOnlineWhitelist;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;

public final class BungeeOnlineWhitelist extends Plugin {

    private BowConfig config;
    private BowDatabase database;
    private boolean loaded = false;

    @Override
    public void onEnable() {
        if (loaded) return;
        if (!getProxy().getConfig().isOnlineMode()) {
            getLogger().severe("The proxy isn't running in online mode! The plugin will not work.");
            return;
        }
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            if (copyStreamToNewFile(getClass().getResourceAsStream("/config.yml"), configFile)) {
                getLogger().warning("The config file is not exist. Now it has been created.");
                getLogger().warning("The plugin won't work. Please modify the MySQL config before reloading.");
                return;
            } else {
                getLogger().severe("Unable to create new config file. The plugin will not work.");
                return;
            }
        }
        try {
            config = new BowConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("A fatal error occurred. The plugin will not work.");
            return;
        }
        try {
            database = new BowDatabase(config.getMysqlHost(), config.getMysqlPort(), config.getMysqlDatabase(),
                    config.getMysqlUsername(), config.getMysqlPassword(), config.getTablePrefix());
        } catch (SQLException e) {
            getLogger().severe("Unable to connect to MySQL server. The plugin will not work.");
            return;
        }
        getProxy().getPluginManager().registerListener(this, new BowListener(getLogger(), config, database));
        getProxy().getPluginManager().registerCommand(this, new BowCommand(database));
        loaded = true;
    }

    @Override
    public void onDisable() {
        if (!loaded) return;
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().unregisterCommands(this);
        config = null;
        database = null;
        loaded = false;
    }

    private boolean copyStreamToNewFile(InputStream in, File file) {
        try {
            if (file.exists()) {
                if (!file.delete()) return false;
                if (!file.createNewFile()) return false;
            }
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
