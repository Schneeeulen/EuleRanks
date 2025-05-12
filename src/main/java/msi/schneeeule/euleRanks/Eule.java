package msi.schneeeule.euleRanks;

import msi.schneeeule.euleRanks.Commands.PluginCommand;
import msi.schneeeule.euleRanks.Commands.RankCommand;
import msi.schneeeule.euleRanks.System.ChatFunction;
import msi.schneeeule.euleRanks.System.DisplayManager;
import msi.schneeeule.euleRanks.System.LuckPermsIntegration;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Eule extends JavaPlugin {

    public static Eule instance;
    public static Boolean foundLuckPerms = false;

    // Configs
    public static Boolean chatfunction, chatPrefixes, tabPrefixes, whiteTabNames, nametagPrefixes, grayNametags, graySpacer;
    public static String chatSpacer;
    public static Component spacer;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        chatfunction = this.getConfig().getBoolean("chatfunction");
        chatPrefixes = this.getConfig().getBoolean("chatPrefixes");
        tabPrefixes = this.getConfig().getBoolean("tabPrefixes");
        whiteTabNames = this.getConfig().getBoolean("whiteTabNames");
        nametagPrefixes = this.getConfig().getBoolean("nametagPrefixes");
        grayNametags = this.getConfig().getBoolean("grayNametags");
        graySpacer = this.getConfig().getBoolean("graySpacer");
        chatSpacer = this.getConfig().getString("chatSpacer");
        spacer = Component.text(this.getConfig().getString("spacer"));

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
}
