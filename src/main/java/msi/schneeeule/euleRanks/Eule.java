package msi.schneeeule.euleRanks;

import msi.schneeeule.euleRanks.Commands.PluginCommand;
import msi.schneeeule.euleRanks.Commands.RankCommand;
import msi.schneeeule.euleRanks.System.ChatFunction;
import msi.schneeeule.euleRanks.System.DisplayManager;
import msi.schneeeule.euleRanks.System.LuckPermsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Eule extends JavaPlugin {

    public static Eule instance;
    public static Boolean foundLuckPerms = false;
    public static Scoreboard teamScoreboard;

    // Configs
    public static Boolean chatfunction, chatPrefixes, tabPrefixes, whiteTabNames, nametagPrefixes, grayNametags, graySpacer;
    public static String chatSpacer, spacer;

    @Override
    public void onEnable() {
        instance = this;
        teamScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        saveDefaultConfig();
        chatfunction = this.getConfig().getBoolean("chatfunction");
        chatPrefixes = this.getConfig().getBoolean("chatPrefixes");
        tabPrefixes = this.getConfig().getBoolean("tabPrefixes");
        whiteTabNames = this.getConfig().getBoolean("whiteTabNames");
        nametagPrefixes = this.getConfig().getBoolean("nametagPrefixes");
        grayNametags = this.getConfig().getBoolean("grayNametags");
        graySpacer = this.getConfig().getBoolean("graySpacer");
        chatSpacer = this.getConfig().getString("chatSpacer");
        spacer = this.getConfig().getString("spacer");

        // Commands
        getCommand("euleranks").setExecutor(new PluginCommand());
        getCommand("euleranks").setTabCompleter(new PluginCommand());
        getCommand("rank").setExecutor(new RankCommand());

        // EventHandler
        Bukkit.getServer().getPluginManager().registerEvents(new DisplayManager(), this);
        if (chatfunction) Bukkit.getServer().getPluginManager().registerEvents(new ChatFunction(), this);

        // LuckPerms
        if (Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            foundLuckPerms = true;
            LuckPermsIntegration.eventBus(this);
        } else this.getLogger().warning("LuckPerms ist nicht als Plugin auf dem Server vorhanden");

    }

    @Override
    public void onDisable() {
        this.getLogger().info("Auf Wiedersehen!");
    }

    public static String translateColour(String message) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color.substring(1)).toString());
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
