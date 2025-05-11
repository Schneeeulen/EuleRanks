package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Eule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFunction implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        event.setCancelled(true);
        Component playerNameComponent = (Eule.chatPrefixes && RankProvider.Ranks.getRank(event.getPlayer()).getPrefix() != null
                ? RankProvider.Ranks.getRank(event.getPlayer()).getPrefix().append(Eule.spacer) : Component.empty())
                .append(Component.text(event.getPlayer().getName(),
                                Eule.whiteTabNames && Eule.chatPrefixes
                                        ? NamedTextColor.WHITE : RankProvider.Ranks.getRank(event.getPlayer()).getColour())
                .append(Component.text(Eule.chatSpacer)));
        Component chatMessage = playerNameComponent.append(Component.text(
                event.getPlayer().hasPermission("owl.chat.colourcodes") ?
                        ChatColor.translateAlternateColorCodes('&', message) : message, NamedTextColor.WHITE));

        Bukkit.getServer().getOnlinePlayers().forEach(p -> p.sendMessage(chatMessage));
    }
}
