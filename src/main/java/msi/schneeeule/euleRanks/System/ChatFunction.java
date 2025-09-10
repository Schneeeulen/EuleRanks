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
        RankProvider.Ranks rank = RankProvider.Ranks.getRank(p);

        String formatedMessage = Eule.chatFormat;

        if (rank.getPrefix() == null) {
            formatedMessage = formatedMessage.replaceAll("\\[.*?\\]", "");
        } else formatedMessage = formatedMessage.replaceAll("\\[(.*?)\\]", "$1");

        formatedMessage = formatedMessage.replace("{RankColour}", rank.getColour());
        formatedMessage = formatedMessage.replace("{RankPrefix}", rank.getPrefix() != null ? rank.getPrefix() : "");
        formatedMessage = formatedMessage.replace("{RankName}", rank.getName());
        formatedMessage = formatedMessage.replace("{PlusOption}", RankProvider.getPlusOption(p));
        formatedMessage = formatedMessage.replace("{PlayerName}", p.getName());

        formatedMessage = Eule.translateColour(formatedMessage);

        formatedMessage = formatedMessage.replace("{Message}", message);

        if (p.hasPermission("owl.colour.chat")) formatedMessage = Eule.translateColour(formatedMessage);

        final String finalMessage = formatedMessage;
        Bukkit.getServer().getOnlinePlayers().forEach(t -> {
            t.sendMessage(finalMessage);
        });
    }

}
