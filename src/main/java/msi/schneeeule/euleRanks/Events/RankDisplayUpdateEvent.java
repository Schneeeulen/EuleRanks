package msi.schneeeule.euleRanks.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankDisplayUpdateEvent extends Event {

    public enum DisplayType {
        TABLIST,
        TEAM
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final DisplayType displayType;

    public RankDisplayUpdateEvent(Player player, DisplayType displayType) {
        this.player = player;
        this.displayType = displayType;
    }

    public Player getPlayer() {
        return player;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;

    }
}