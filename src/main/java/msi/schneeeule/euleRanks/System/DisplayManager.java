package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.Events.RankDisplayUpdateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

public class DisplayManager implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        setPlayerListName(event.getPlayer());
        registerTeam(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        unregisterTeam(event.getPlayer());
    }

    public static void updatePlayer(Player p) {
        unregisterTeam(p);
        registerTeam(p);
        setPlayerListName(p);
    }

    public static void registerTeam(Player p) {
        RankProvider.Ranks rank = RankProvider.Ranks.getRank(p);
        int priority = 1000 - RankProvider.Ranks.getRank(p).getPriority();
        if (p.hasPermission("owl.rank.plus")) priority = priority - RankProvider.plus_priority_boost;
        if (priority < 0) priority = 0;
        String teamname = String.format("%04d", priority) + "." + p.getName().toLowerCase();
        if (p.getScoreboard().getTeam(teamname) == null) {
            p.getScoreboard().registerNewTeam(teamname);
        }
        Team team = p.getScoreboard().getTeam(teamname);
        team.addPlayer(p);
        team.setColor(Eule.grayNametags ? ChatColor.GRAY : ChatColor.WHITE);
        if (Eule.nametagPrefixes && rank.getPrefix() != null) {
            team.prefix(rank.getPrefix().append(RankProvider.getPlusOption(p).append(RankProvider.spacer)));
        }
        Bukkit.getPluginManager().callEvent(new RankDisplayUpdateEvent(p, RankDisplayUpdateEvent.DisplayType.TEAM));
    }

    public static void unregisterTeam(Player p) {
        for (Team team : p.getScoreboard().getTeams()) {
            if (team.getName().endsWith("."+p.getName().toLowerCase())) {
                team.unregister();
                break;
            }
        }
    }

    public static void fixTeam(Player p) {
        unregisterTeam(p);
        registerTeam(p);
    }

    public static void setPlayerListName(Player p) {
        p.playerListName(getPlayerListName(p));
        Bukkit.getPluginManager().callEvent(new RankDisplayUpdateEvent(p, RankDisplayUpdateEvent.DisplayType.TABLIST));
    }

    public static Component getPlayerListName(Player p) {
        RankProvider.Ranks rank = RankProvider.Ranks.getRank(p);
        if (rank.getPrefix() == null) {
            return Component.text(p.getName(), Eule.whiteTabNames ? NamedTextColor.WHITE : rank.getColour());
        } else return rank.getPrefix().append(RankProvider.getPlusOption(p).append(RankProvider.spacer)
                .append(Component.text(p.getName(), Eule.whiteTabNames ? NamedTextColor.WHITE : rank.getColour())));
    }


}
