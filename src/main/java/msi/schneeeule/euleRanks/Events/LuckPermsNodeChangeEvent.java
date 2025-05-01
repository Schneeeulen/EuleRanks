package msi.schneeeule.euleRanks.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class LuckPermsNodeChangeEvent extends Event {

    public enum Context {
        GROUP_ADD,
        GROUP_REMOVE,
        ADD,
        REMOVE
    }

    private final UUID uuid;
    private final Context context;
    private final String key;

    private static final HandlerList HANDLERS = new HandlerList();

    public LuckPermsNodeChangeEvent(UUID uuid, Context context, String key) {
        this.uuid = uuid;
        this.context = context;
        this.key = key;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Context getContext() {
        return context;
    }

    public String getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;

    }
}
