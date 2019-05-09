package top.sunbread.BungeeOnlineWhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Arrays;
import java.util.UUID;

public final class BowCommand extends Command {

    private static final String COMMAND_NAME = "bungeeonlinewhitelist";
    private static final String COMMAND_PERMISSION = "bungeeonlinewhitelist.admin";
    private static final String COMMAND_ALIAS = "bow";

    private BowDatabase database;

    public BowCommand(BowDatabase database) {
        super(COMMAND_NAME, COMMAND_PERMISSION, COMMAND_ALIAS);
        this.database = database;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Available commands:").color(ChatColor.GOLD).create());
            sender.sendMessage(new ComponentBuilder("/bow add <targets>").color(ChatColor.GOLD).create());
            sender.sendMessage(new ComponentBuilder("/bow remove <targets>").color(ChatColor.GOLD).create());
            sender.sendMessage(new ComponentBuilder("/bow verify <targets>").color(ChatColor.GOLD).create());
            return;
        }
        switch (args[0]) {
            case "add":
                add(sender, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "remove":
                remove(sender, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "verify":
                verify(sender, Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                sender.sendMessage(new ComponentBuilder("Wrong command. Type /bow for a list of commands.").
                        color(ChatColor.RED).create());
                break;
        }
    }

    private void add(CommandSender sender, String[] targetNames) {
        if (targetNames.length == 0) {
            sender.sendMessage(new ComponentBuilder("Please specify target players.").
                    color(ChatColor.RED).create());
            return;
        }
        for (String targetName : targetNames) {
            if (!BowUtils.isPlayerNameValid(targetName)) {
                sender.sendMessage(new ComponentBuilder("Playername " + targetName + " is not valid").
                        color(ChatColor.RED).create());
                continue;
            }
            UUID targetUUID = BowUtils.getUUIDbyName(targetName);
            if (targetUUID == null) {
                sender.sendMessage(new ComponentBuilder("Player " + targetName + " does not exist").
                        color(ChatColor.RED).create());
                continue;
            }
            try {
                if (!database.isInWhitelist(targetUUID)) {
                    if (database.addWhitelist(targetUUID))
                        sender.sendMessage(new ComponentBuilder("Added player " + targetName +
                                " to the whitelist").color(ChatColor.GREEN).create());
                    else
                        sender.sendMessage(new ComponentBuilder("Unable to add " + targetName +
                                " to the whitelist").color(ChatColor.RED).create());
                } else
                    sender.sendMessage(new ComponentBuilder("Player " + targetName + " is already whitelisted").
                            color(ChatColor.RED).create());
            } catch (SQLTimeoutException e) {
                sender.sendMessage(new ComponentBuilder("Timed out querying the database " +
                        "while adding player " + targetName).color(ChatColor.RED).create());
            } catch (SQLException e) {
                sender.sendMessage(new ComponentBuilder("Error querying the database " +
                        "while adding player " + targetName).color(ChatColor.RED).create());
                e.printStackTrace();
            }
        }
    }

    private void remove(CommandSender sender, String[] targetNames) {
        if (targetNames.length == 0) {
            sender.sendMessage(new ComponentBuilder("Please specify target players.").
                    color(ChatColor.RED).create());
            return;
        }
        for (String targetName : targetNames) {
            if (!BowUtils.isPlayerNameValid(targetName)) {
                sender.sendMessage(new ComponentBuilder("Playername " + targetName + " is not valid").
                        color(ChatColor.RED).create());
                continue;
            }
            UUID targetUUID = BowUtils.getUUIDbyName(targetName);
            if (targetUUID == null) {
                sender.sendMessage(new ComponentBuilder("Player " + targetName + " does not exist").
                        color(ChatColor.RED).create());
                continue;
            }
            try {
                if (database.isInWhitelist(targetUUID)) {
                    if (database.removeWhitelist(targetUUID))
                        sender.sendMessage(new ComponentBuilder("Removed player " + targetName +
                                " from the whitelist").color(ChatColor.GREEN).create());
                    else
                        sender.sendMessage(new ComponentBuilder("Unable to remove " + targetName +
                                " from the whitelist").color(ChatColor.RED).create());
                } else
                    sender.sendMessage(new ComponentBuilder("Player " + targetName + " is not whitelisted").
                            color(ChatColor.RED).create());
            } catch (SQLTimeoutException e) {
                sender.sendMessage(new ComponentBuilder("Timed out querying the database " +
                        "while removing player " + targetName).color(ChatColor.RED).create());
            } catch (SQLException e) {
                sender.sendMessage(new ComponentBuilder("Error querying the database " +
                        "while removing player " + targetName).color(ChatColor.RED).create());
                e.printStackTrace();
            }
        }
    }

    private void verify(CommandSender sender, String[] targetNames) {
        if (targetNames.length == 0) {
            sender.sendMessage(new ComponentBuilder("Please specify target players.").
                    color(ChatColor.RED).create());
            return;
        }
        for (String targetName : targetNames) {
            if (!BowUtils.isPlayerNameValid(targetName)) {
                sender.sendMessage(new ComponentBuilder("Playername " + targetName + " is not valid").
                        color(ChatColor.RED).create());
                continue;
            }
            UUID targetUUID = BowUtils.getUUIDbyName(targetName);
            if (targetUUID == null) {
                sender.sendMessage(new ComponentBuilder("Player " + targetName + " does not exist").
                        color(ChatColor.RED).create());
                continue;
            }
            try {
                if (database.isInWhitelist(targetUUID))
                    sender.sendMessage(new ComponentBuilder("Player " + targetName + " is ").
                            color(ChatColor.GOLD).append("in whitelist").color(ChatColor.GREEN).create());
                else
                    sender.sendMessage(new ComponentBuilder("Player " + targetName + " is ").
                            color(ChatColor.GOLD).append("not in whitelist").color(ChatColor.RED).create());
            } catch (SQLTimeoutException e) {
                sender.sendMessage(new ComponentBuilder("Timed out querying the database " +
                        "while verifying player " + targetName).color(ChatColor.RED).create());
            } catch (SQLException e) {
                sender.sendMessage(new ComponentBuilder("Error querying the database " +
                        "while verifying player " + targetName).color(ChatColor.RED).create());
                e.printStackTrace();
            }
        }
    }

}
