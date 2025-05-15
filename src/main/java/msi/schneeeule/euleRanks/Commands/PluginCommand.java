package msi.schneeeule.euleRanks.Commands;

import msi.schneeeule.euleRanks.System.DisplayManager;
import msi.schneeeule.euleRanks.System.RankProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
            for (Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
                team.unregister();
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                DisplayManager.registerTeam(player);
            }
            sender.sendMessage("§7› Verbuggte Teams sind nun gelöscht");
            return true;
        }

        if (args[0].equals("listranks")) {
            TextComponent.Builder builder = Component.text();
            builder.append(Component.text("§7› Es folgt eine Auflistung aller Ränge:"));
            for (RankProvider.Ranks rank : RankProvider.Ranks.values()) {
                if (rank == RankProvider.Ranks.FALLBACK) continue;
                builder.append(Component.text("\n§7› "))
                        .append(rank.getColouredName())
                        .append(Component.text("§7 ➟ §f" + rank.getPermission())
                        );
            }
            sender.sendMessage(builder);
            return true;
        }


        sender.sendMessage("§7› Dieser Unterbefehl wurde nicht gefunden!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = Arrays.asList("fixdisplays", "updateplayer", "clearteams", "listranks");
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
