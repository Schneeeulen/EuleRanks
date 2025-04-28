package msi.schneeeule.euleRanks;

import msi.schneeeule.euleRanks.Commands.PluginCommand;
import msi.schneeeule.euleRanks.Commands.RankCommand;
import msi.schneeeule.euleRanks.System.ChatFunction;
import msi.schneeeule.euleRanks.System.DisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Eule extends JavaPlugin {

    public static Eule instance;
    public static Boolean foundLuckPerms = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Commands
        getCommand("euleranks").setExecutor(new PluginCommand());
        getCommand("euleranks").setTabCompleter(new PluginCommand());
        getCommand("rank").setExecutor(new RankCommand());
        // Listener
        Bukkit.getServer().getPluginManager().registerEvents(new DisplayManager(), this);

        if (this.getConfig().getBoolean("chatfunction")) Bukkit.getServer().getPluginManager().registerEvents(new ChatFunction(), this);
        if (Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) foundLuckPerms = true;
        else this.getLogger().warning("LuckPerms ist nicht als Plugin auf dem Server vorhanden");

    }

    @Override
    public void onDisable() {

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            DisplayManager.unregisterTeam(p);
        }

    }
}
