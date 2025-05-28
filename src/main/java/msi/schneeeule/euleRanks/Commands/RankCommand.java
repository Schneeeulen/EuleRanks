package msi.schneeeule.euleRanks.Commands;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.System.LuckPermsIntegration;
import msi.schneeeule.euleRanks.System.RankProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§7› Nur spieler können diesen Command benutzen");
            return true;
        }

        if (!Eule.foundLuckPerms) {
            RankProvider.Ranks pRank = RankProvider.Ranks.getRank(p);
            p.sendMessage("§7› Du hast aktuell den Rang: "
                    + pRank.getColouredName() + (p.hasPermission("owl.rank.plus") ?
                    "\n§7› Zudem hast du ein aktives " + pRank.getColour() + "Plus"
                    : "") + "\n§7› Die Laufzeiten konnten nicht berechnet werden!"
            );
            return true;
        }

        if (args.length > 0 && sender.hasPermission("owl.commands.rank.other")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            UUID uuid = target.getUniqueId();
            if (!target.hasPlayedBefore()) {
                sender.sendMessage("§7› Der angegebene Spieler ist nicht auf dem Server registriert");
                return true;
            }

            if (LuckPermsIntegration.getRank(uuid) == RankProvider.Ranks.FALLBACK) {
                sender.sendMessage("\n§7› " + Bukkit.getOfflinePlayer(uuid).getName() + " hat keinen Premium-Rang!\n");
                return true;
            }

            StringBuilder builder = new StringBuilder();

            builder.append("\n§7› " + Bukkit.getOfflinePlayer(uuid).getName() + " hat den Rang:\n"
                    + "§7› " + LuckPermsIntegration.getRank(uuid).getColouredName());

            LuckPermsIntegration.hasPermission(uuid, "owl.rank.plus").thenAccept(hasPermission -> {
                if (hasPermission) builder.append("\n§7› Das " + LuckPermsIntegration.getRank(uuid).getColour()
                        + "Plus §7ist aktiv\n ");
                else builder.append("\n ");

                sender.sendMessage(builder.toString());
            });
            return true;
        }

        if (RankProvider.Ranks.getRank(p) == RankProvider.Ranks.USER
                || RankProvider.Ranks.getRank(p) == RankProvider.Ranks.FALLBACK) {
            p.sendMessage("§7› Du verfügst aktuell über keinen Premium-Rang!");

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
            builder.append("den Rang: " + pRank.getColouredName() + "\n");
        } else {
            // Begrenzt Premium
            builder.append("§7› Du hast noch §f"
                    + LuckPermsIntegration.getPermissionTime(p, pRank.getPermission()) +
                    " §7den Rang: " + pRank.getColour() + "\n");

            for (RankProvider.Ranks rank : RankProvider.Ranks.values()) {
                if (rank.getPermission() == null) continue;
                if (LuckPermsIntegration.hasLifetimePermission(p, rank.getPermission()) && rank.getPriority() < 500
                        && rank != RankProvider.Ranks.USER && rank != RankProvider.Ranks.FALLBACK) {
                    builder.append("§7› Zudem besitzt du dauerhaft " + rank.getColouredName() + "\n");
                    break;
                }
            }

        }

        if (LuckPermsIntegration.hasLifetimePermission(p, "owl.rank.plus")) {
            // Dauerhaft Plus
            builder.append("§7› Dein " + pRank.getColour() + "Plus§7 hat keine eingetragene Ablaufzeit");
        } else if (p.hasPermission("owl.rank.plus")) {
            builder.append("§7› Dein " + pRank.getColour() + "Plus§7 hält noch §f"
                    + LuckPermsIntegration.getPermissionTime(p, "owl.rank.plus"));
        }

        p.sendMessage(builder.toString());

        return true;
    }
}
