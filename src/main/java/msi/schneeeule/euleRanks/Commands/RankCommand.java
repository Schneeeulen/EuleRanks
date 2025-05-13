package msi.schneeeule.euleRanks.Commands;

import msi.schneeeule.euleRanks.Eule;
import msi.schneeeule.euleRanks.System.LuckPermsIntegration;
import msi.schneeeule.euleRanks.System.RankProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

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
            Component msg = Component.textOfChildren(
                    text("› Du hast aktuell den Rang: ", NamedTextColor.GRAY),
                    pRank.getColouredName(),
                    p.hasPermission("owl.rank.plus") ?
                            Component.textOfChildren(
                                    Component.newline(),
                                    text("› Zudem hast du ein aktives ", NamedTextColor.GRAY),
                                    text("Plus", pRank.getColour())
                            ) : Component.empty(),
                    Component.newline(),
                    text("› Die Laufzeiten konnten nicht berechnet werden!", NamedTextColor.GRAY)
            );
            p.sendMessage(msg);
            return true;
        }

        if (args.length > 0 && sender.hasPermission("owl.commands.rank.other")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            UUID uuid = target.getUniqueId();
            if (!target.hasPlayedBefore()) {
                sender.sendMessage("§7› Der angegebene Spieler ist nicht auf dem Server registriert");
                return true;
            }

            TextComponent.Builder builder = Component.text();
            builder.appendNewline();
            builder.append(text("› " + target.getName() + " hat den Rang:", NamedTextColor.GRAY));
            builder.appendNewline();
            builder.append(text("› ", NamedTextColor.GRAY))
                    .append(LuckPermsIntegration.getRank(uuid).getColouredName());

            LuckPermsIntegration.hasPermission(uuid, "owl.rank.plus").thenAccept(hasPermission -> {
                if (hasPermission) {
                    builder.appendNewline()
                            .append(text("› Das ", NamedTextColor.GRAY))
                            .append(text("Plus", LuckPermsIntegration.getRank(uuid).getColour()))
                            .append(text(" ist aktiv", NamedTextColor.GRAY));
                }
                builder.appendNewline();
                sender.sendMessage(builder.build());
            });
            return true;
        }

        if (RankProvider.Ranks.getRank(p) == RankProvider.Ranks.USER) {
            p.sendMessage(text("› Du verfügst aktuell über keinen Premium-Rang!", NamedTextColor.GRAY));

            if (p.hasPermission("owl.rank.plus")) {
                if (LuckPermsIntegration.hasLifetimePermission(p, "owl.rank.plus")) {
                    p.sendMessage(text("› Allerdings hast du ein ", NamedTextColor.GRAY)
                            .append(text("Plus", NamedTextColor.GREEN))
                            .append(text(" ohne eingetragene Ablaufzeit", NamedTextColor.GRAY)));
                } else {
                    p.sendMessage(Component.textOfChildren(
                            text("› Allerdings hast du noch ein ", NamedTextColor.GRAY),
                            text("Plus", NamedTextColor.GREEN),
                            text(" für ", NamedTextColor.GRAY),
                            text(LuckPermsIntegration.getPermissionTime(p, "owl.rank.plus"), NamedTextColor.WHITE)
                    ));
                }
            }
            return true;
        }

        RankProvider.Ranks pRank = RankProvider.Ranks.getRank(p);
        TextComponent.Builder builder = Component.text();

        if (LuckPermsIntegration.hasLifetimePermission(p, pRank.getPermission())) {
            builder.append(text("› Du hast ", NamedTextColor.GRAY));
            if (!p.hasPermission("owl.team.member") && !p.hasPermission("owl.premium.special")) {
                builder.append(text("dauerhaft ", NamedTextColor.GRAY));
            }
            builder.append(text("den Rang: ", NamedTextColor.GRAY))
                    .append(pRank.getColouredName());
        } else {
            builder.append(text("› Du hast noch ", NamedTextColor.GRAY))
                    .append(text(LuckPermsIntegration.getPermissionTime(p, pRank.getPermission()), NamedTextColor.WHITE))
                    .append(text(" den Rang: ", NamedTextColor.GRAY))
                    .append(pRank.getColouredName());

            for (RankProvider.Ranks rank : RankProvider.Ranks.values()) {
                if (rank.getPermission() == null) continue;
                if (LuckPermsIntegration.hasLifetimePermission(p, rank.getPermission()) && rank.getPriority() < 500
                        && rank != RankProvider.Ranks.USER && rank != RankProvider.Ranks.FALLBACK) {
                    builder.appendNewline()
                            .append(text("› Zudem besitzt du dauerhaft ", NamedTextColor.GRAY))
                            .append(rank.getColouredName());
                    break;
                }
            }
        }

        if (LuckPermsIntegration.hasLifetimePermission(p, "owl.rank.plus")) {
            builder.appendNewline()
                    .append(text("› Dein ", NamedTextColor.GRAY))
                    .append(text("Plus", pRank.getColour()))
                    .append(text(" hat keine eingetragene Ablaufzeit", NamedTextColor.GRAY));
        } else if (p.hasPermission("owl.rank.plus")) {
            builder.appendNewline()
                    .append(text("› Dein ", NamedTextColor.GRAY))
                    .append(text("Plus", pRank.getColour()))
                    .append(text(" hält noch ", NamedTextColor.GRAY))
                    .append(text(LuckPermsIntegration.getPermissionTime(p, "owl.rank.plus"), NamedTextColor.WHITE));
        }

        p.sendMessage(builder.build());
        return true;
    }
}
