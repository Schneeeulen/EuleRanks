package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Eule;
import org.bukkit.Bukkit;
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
        Bukkit.getServer().getOnlinePlayers().forEach(t -> t.sendMessage(
                (Eule.chatPrefixes && RankProvider.Ranks.getRank(p).getColouredPrefix() != null
                        ? RankProvider.Ranks.getRank(p).getColouredPrefix() + RankProvider.getPlusOption(p) + Eule.spacer
                        : ""
                ) + (Eule.whiteTabNames && Eule.chatPrefixes
                        ? "§f" + p.getName() : RankProvider.Ranks.getRank(p).getColour() + p.getName()
                ) + Eule.chatSpacer + "§r" + (p.hasPermission("owl.colour.chat")
                        ? Eule.translateColour(message)
                        : message
                )

        ));
    }

}
