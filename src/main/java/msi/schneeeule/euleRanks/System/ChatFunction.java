package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Eule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFunction implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        sendMessageAsPlayer(event.getPlayer(), event.getMessage());
        event.setCancelled(true);
    }

    public static void sendMessageAsPlayer(Player p, String message) {
        Component formattedMessage = (Eule.chatPrefixes && RankProvider.Ranks.getRank(p).getPrefix() != null
                ? RankProvider.Ranks.getRank(p).getPrefix().append(RankProvider.getPlusOption(p)).append(Eule.spacer)
                : Component.empty())
                .append(Component.text(p.getName(),
                                Eule.whiteTabNames && Eule.chatPrefixes
                                        ? NamedTextColor.WHITE : RankProvider.Ranks.getRank(p).getColour())
                        .append(Component.text(Eule.chatSpacer))
                        .append(Component.text(p.hasPermission("owl.colour.chat")
                                ? ChatColor.translateAlternateColorCodes('&', message)
                                : message, NamedTextColor.WHITE)));

        Bukkit.getServer().getOnlinePlayers().forEach(t -> t.sendMessage(formattedMessage));
    }

}
