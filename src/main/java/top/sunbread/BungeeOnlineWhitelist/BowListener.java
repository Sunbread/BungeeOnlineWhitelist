package top.sunbread.BungeeOnlineWhitelist;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.logging.Logger;

public final class BowListener implements Listener {

    private Logger logger;
    private BowConfig config;
    private BowDatabase database;

    public BowListener(Logger logger, BowConfig config, BowDatabase database) {
        this.logger = logger;
        this.config = config;
        this.database = database;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        try {
            if (!database.isInWhitelist(event.getPlayer().getUniqueId()))
                event.getPlayer().disconnect(config.getWhitelistMessage());
        } catch (SQLTimeoutException e) {
            event.getPlayer().disconnect(new TextComponent("Database is busy. Please try again later."));
            logger.warning("Timed out querying the database while verifying player " +
                    event.getPlayer().getName() + "...");
        } catch (SQLException e) {
            event.getPlayer().disconnect(new TextComponent("Something is wrong with the database. " +
                    "Please contact the server administrators."));
            logger.severe("Error querying the database while verifying player " +
                    event.getPlayer().getName());
            e.printStackTrace();
        }
    }

}
