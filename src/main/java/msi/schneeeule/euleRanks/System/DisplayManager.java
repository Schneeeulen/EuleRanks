package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.Events.RankDisplayUpdateEvent;
import org.bukkit.Bukkit;
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
        team.setColor(rank.getColour());
        if (Eule.nametagPrefixes) team.setPrefix(rank.getPrefix() + RankProvider.getPlusOption(p) + RankProvider.spacer);
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
        RankProvider.Ranks rank = RankProvider.Ranks.getRank(p);
        p.setPlayerListName((rank.getPrefix() == null ? "" :
                rank.getPrefix() + RankProvider.getPlusOption(p) + RankProvider.spacer)
                + rank.getColourcode() + p.getName());
        Bukkit.getPluginManager().callEvent(new RankDisplayUpdateEvent(p, RankDisplayUpdateEvent.DisplayType.TABLIST));
    }

    public static String getPlayerListName(Player p) {
        RankProvider.Ranks rank = RankProvider.Ranks.getRank(p);
        return (rank.getPrefix() == null ? "" :
                rank.getPrefix() + RankProvider.getPlusOption(p) + RankProvider.spacer)
                + rank.getColourcode() + p.getName();
    }


}
