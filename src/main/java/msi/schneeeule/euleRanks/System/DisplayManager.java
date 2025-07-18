package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.Events.RankDisplayUpdateEvent;
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
        String teamname = RankProvider.getFormattedPriority(p) + "." + p.getName().toLowerCase();
        if (Eule.teamScoreboard.getTeam(teamname) == null) {
            Eule.teamScoreboard.registerNewTeam(teamname);
        }
        Team team = Eule.teamScoreboard.getTeam(teamname);
        team.addPlayer(p);
        team.setColor(Eule.grayNametags ? ChatColor.GRAY : ChatColor.WHITE);
        if (Eule.nametagPrefixes && rank.getColouredPrefix() != null) {
            team.setPrefix(rank.getColouredPrefix() + RankProvider.getPlusOption(p) +
                    Eule.rankPrefixFormat
                            .replace("{RankColour}", rank.getColour())
                            .replace("{RankPrefix}", rank.getColouredPrefix())
                            .replace("{PlusOption}", RankProvider.getPlusOption(p))
            );
        }
        Bukkit.getPluginManager().callEvent(new RankDisplayUpdateEvent(p, RankDisplayUpdateEvent.DisplayType.TEAM));
    }

    public static void unregisterTeam(Player p) {
        for (Team team : Eule.teamScoreboard.getTeams()) {
            if (team == null) continue;
            if (team.getName().endsWith("."+p.getName().toLowerCase())) {
                team.unregister();
            }
        }
    }

    public static void fixTeam(Player p) {
        unregisterTeam(p);
        registerTeam(p);
    }

    public static void setPlayerListName(Player p) {
        p.setPlayerListName(getPlayerListName(p));
        Bukkit.getPluginManager().callEvent(new RankDisplayUpdateEvent(p, RankDisplayUpdateEvent.DisplayType.TABLIST));
    }

    public static String getPlayerListName(Player p) {
        RankProvider.Ranks rank = RankProvider.Ranks.getRank(p);
        if (!Eule.tabPrefixes) return rank.getColour() + p.getName();
        if (rank.getColouredPrefix() == null) {
            return Eule.whiteTabNames ? p.getName() : rank.getColour() + p.getName();
        } else return Eule.rankPrefixFormat
                .replace("{RankColour}", rank.getColour())
                .replace("{RankPrefix}", rank.getColouredPrefix())
                .replace("{PlusOption}", RankProvider.getPlusOption(p))
                + (Eule.whiteTabNames ? "§r" + p.getName() : rank.getColour() + p.getName());
    }


}
