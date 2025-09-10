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
        if (ChatColor.getByChar(Eule.teamColour) != null) team.setColor(ChatColor.getByChar(Eule.teamColour));

        if (Eule.nametagPrefixFormat != null) {
            String formated = Eule.nametagPrefixFormat;

            if (rank.getPrefix() == null) {
                formated = formated.replaceAll("\\[.*?\\]", "");
            } else formated = formated.replaceAll("\\[(.*?)\\]", "$1");

            formated = formated.replace("{RankColour}", rank.getColour());
            formated = formated.replace("{RankPrefix}", rank.getPrefix() != null ? rank.getPrefix() : "");
            formated = formated.replace("{RankName}", rank.getName());
            formated = formated.replace("{PlusOption}", RankProvider.getPlusOption(p));
            team.setPrefix(Eule.translateColour(formated));
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

        String formated = Eule.tablistFormat;

        if (rank.getPrefix() == null) {
            formated = formated.replaceAll("\\[.*?\\]", "");
        } else formated = formated.replaceAll("\\[(.*?)\\]", "$1");

        formated = formated.replace("{RankColour}", rank.getColour());
        formated = formated.replace("{RankPrefix}", rank.getPrefix() != null ? rank.getPrefix() : "");
        formated = formated.replace("{RankName}", rank.getName());
        formated = formated.replace("{PlusOption}", RankProvider.getPlusOption(p));
        formated = formated.replace("{PlayerName}", p.getName());

        return Eule.translateColour(formated);
    }


}
