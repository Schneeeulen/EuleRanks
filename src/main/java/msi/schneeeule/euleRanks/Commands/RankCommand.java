package msi.schneeeule.euleRanks.Commands;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.System.LuckPermsIntegration;
import msi.schneeeule.euleRanks.System.RankProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7› Nur spieler können diesen Command benutzen");
            return true;
        }
        Player p = (Player) sender;

        if (!Eule.foundLuckPerms) {
            RankProvider.Ranks pRank = RankProvider.Ranks.getRank(p);
            p.sendMessage("§7› Du hast aktuell den Rang: "
                    + pRank.getName() + (p.hasPermission("owl.rank.plus") ?
                            "\n§7› Zudem hast du ein aktives " + pRank.getColourcode() + "Plus"
                            : "") + "\n§7› Die Laufzeiten konnten nicht berechnet werden!"
                    );
            return true;
        }

        if (RankProvider.Ranks.getRank(p) == RankProvider.Ranks.USER) {
            p.sendMessage("§7› Du verfügst aktuell über keinen Premium-Rang!");

            // Sollte nicht vorkommen
            if (p.hasPermission("owl.rank.plus")) {
                if (LuckPermsIntegration.hasLifetimePermission(p, "owl.rank.plus")) {
                    p.sendMessage("§7› Allerdings hast du ein §aPlus§7 ohne eingetragene Ablaufzeit");
                } else p.sendMessage("§7› Allerdings hast du noch ein §aPlus§7 für §f"
                        + LuckPermsIntegration.getPermissionTime(p, "owl.rank.plus"));
            }
            return true;
        }

        RankProvider.Ranks pRank = RankProvider.Ranks.getRank(p);
        StringBuilder builder = new StringBuilder();

        if (LuckPermsIntegration.hasLifetimePermission(p, pRank.getPermission())) {
            // Lifetime Premium
            builder.append("§7› Du hast ");
            if (p.hasPermission("owl.team.member") || p.hasPermission("owl.premium.special")) {
                // Durch speziellen Rang
            }  else {
                builder.append("dauerhaft ");
            }
            builder.append("den Rang: "+pRank.getName() + "\n");
        } else {
            // Begrenzt Premium
            builder.append("§7› Du hast noch §f"
                    + LuckPermsIntegration.getPermissionTime(p, pRank.getPermission()) +
                    " §7den Rang: " + pRank.getName()+"\n");

            for (RankProvider.Ranks rank : RankProvider.Ranks.values()) {
                if (rank.getPermission() == null) continue;
                if (LuckPermsIntegration.hasLifetimePermission(p, rank.getPermission()) && rank.getPriority() < 500) {
                    builder.append("§7› Zudem besitzt du dauerhaft "+rank.getName()+"\n");
                    break;
                }
            }

        }

        if (LuckPermsIntegration.hasLifetimePermission(p, "owl.rank.plus")) {
            // Dauerhaft Plus
            builder.append("§7› Dein " + pRank.getColourcode() + "Plus§7 hat keine eingetragene Ablaufzeit");
        } else if (p.hasPermission("owl.rank.plus")) {
            builder.append("§7› Dein " + pRank.getColourcode() +"Plus§7 hält noch §f"
                    + LuckPermsIntegration.getPermissionTime(p, "owl.rank.plus"));
        }

        p.sendMessage(builder.toString());

        return true;
    }
}
