package msi.schneeeule.euleRanks.System;

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

        Bukkit.broadcastMessage(RankProvider.Ranks.getRank(event.getPlayer()).getColour()
                + event.getPlayer().getName() + "§7› §f" + (event.getPlayer().hasPermission("owl.chat.colourcodes")
                ? ChatColor.translateAlternateColorCodes('&', message) : message));
    }
}
