package msi.schneeeule.euleRanks.Commands;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.System.DisplayManager;
import msi.schneeeule.euleRanks.System.LuckPermsIntegration;
import msi.schneeeule.euleRanks.System.RankProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7› Elektroeule Rank Provider"); //Todo
            return false;
        }

        if (args[0].equals("fixdisplays")) {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                DisplayManager.updatePlayer(player);
            });
            sender.sendMessage("§7› Die Ränge aller Spieler wurden neu geladen");
            return true;
        }

        if (args[0].equals("updateplayer")) {
            if (args.length != 2) {
                sender.sendMessage("§7› Benutze /euleranks updateplayer <target>");
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§7› "+args[1]+" konnte nicht als Spieler geladen werden");
                return false;
            }
            DisplayManager.updatePlayer(target);
            sender.sendMessage("§7› Der Rang von "+target.getName()+" wurde neu geladen");
            return true;
        }

        if (args[0].equals("clearteams")) {
            for (Team team : Eule.teamScoreboard.getTeams()) {
                team.unregister();
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                DisplayManager.registerTeam(player);
            }
            sender.sendMessage("§7› Verbuggte Teams sind nun gelöscht");
            return true;
        }

        if (args[0].equals("listranks")) {
            StringBuilder builder = new StringBuilder();
            builder.append("§7› Es folgt eine Auflistung aller Ränge:");

            for (RankProvider.Ranks rank : RankProvider.Ranks.values()) {
                if (rank == RankProvider.Ranks.FALLBACK) continue;
                builder.append(("\n§7› ") + rank.getColouredName() + "§7 ➟ §f" + rank.getPermission()
                        + "§7 (" + rank.getPriority() + ")");
            }
            sender.sendMessage(builder.toString());
            return true;
        }

        if (args[0].equals("checkperms")) {
            if (!Eule.foundLuckPerms) {
                sender.sendMessage("§7› Dieser Unterbefehl ist nur mit LuckPerms verfügbar!");
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage("§7› Benutze /euleranks checkperms <permission>");
                return true;
            }

            Pair requiredRankByGroup = LuckPermsIntegration.getRequiredRankByGroup(args[1]);

            if (requiredRankByGroup == null) {
                sender.sendMessage("§7› Die Permission " + args[1] + " ist nicht über Rang-Gruppen verfügbar!");
            } else {
                sender.sendMessage("§7› Die Permission §f" + args[1] + "§7 ist durch die Gruppe §f"
                        + requiredRankByGroup.getKey() + "§7 mit dem Rang " + requiredRankByGroup.getValue() + "§7 verfügbar!");
            }

            return true;
        }


        sender.sendMessage("§7› Dieser Unterbefehl wurde nicht gefunden!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = Arrays.asList("fixdisplays", "updateplayer", "clearteams", "listranks", "checkperms");
            List<String> result = new ArrayList<>();
            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(suggestion);
                }
            }
            return result;
        }
        return null;
    }

}
