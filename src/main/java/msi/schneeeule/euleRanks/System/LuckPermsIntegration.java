package msi.schneeeule.euleRanks.System;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsIntegration {
    public static void eventBus(JavaPlugin plugin) {
        LuckPermsProvider.get().getEventBus().subscribe(plugin, UserDataRecalculateEvent.class, event -> {
            Player p = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (p != null) {
                DisplayManager.updatePlayer(p);
            }
        });
    }



    public static boolean hasLifetimePermission(Player player, String permission) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return false;
        }

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                if (!node.hasExpiry()) {
                    return true;
                } else {
                    continue;
                }
            }

            if (node instanceof InheritanceNode inheritanceNode) {
                if (!inheritanceNode.hasExpiry()) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission) && !groupNode.hasExpiry()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static String getPermissionTime(Player player, String permission) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return null;
        }

        Duration finalDuration = null;

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                if (!node.hasExpiry()) {
                    return null;
                } else {
                    Duration duration = node.getExpiryDuration();
                    if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                        finalDuration = duration;
                    }
                }
            }

            if (node instanceof InheritanceNode inheritanceNode) {
                Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                if (group != null) {
                    for (Node groupNode : group.getNodes()) {
                        if (groupNode.getKey().equalsIgnoreCase(permission)) {
                            if (!groupNode.hasExpiry()) {
                                if (!inheritanceNode.hasExpiry()) {
                                    return null;
                                } else {
                                    Duration duration = inheritanceNode.getExpiryDuration();
                                    if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                        finalDuration = duration;
                                    }
                                }
                            } else {
                                Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                    finalDuration = groupPermissionDuration;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (finalDuration != null) {
            long days = finalDuration.toDays();
            long hours = finalDuration.toHours() % 24;
            long minutes = finalDuration.toMinutes() % 60;
            long seconds = finalDuration.getSeconds() % 60;

            StringBuilder builder = new StringBuilder();
            if (days > 0) builder.append(days).append("d ");
            if (hours > 0 || days > 0) builder.append((hours > 9 ? hours : "0" + hours)).append("h ");
            if (minutes > 0 || builder.length() > 0) builder.append((minutes > 9 ? minutes : "0" + minutes)).append("m ");
            builder.append((seconds > 9 ? seconds : "0" + seconds)).append("s");

            return builder.toString().trim();
        }

        return null;
    }

    public static CompletableFuture<Boolean> hasLifetimePermission(UUID uuid, String permission) {
        LuckPerms api = LuckPermsProvider.get();

        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    if (!node.hasExpiry()) {
                        return true;
                    }
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    if (!inheritanceNode.hasExpiry()) {
                        Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());
                        if (group != null) {
                            for (Node groupNode : group.getNodes()) {
                                if (groupNode.getKey().equalsIgnoreCase(permission) && !groupNode.hasExpiry()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return false;
        });
    }

    public static CompletableFuture<String> getPermissionTime(UUID uuid, String permission) {
        LuckPerms api = LuckPermsProvider.get();

        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) {
                return null;
            }

            Duration finalDuration = null;

            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    if (!node.hasExpiry()) {
                        return null;
                    } else {
                        Duration duration = node.getExpiryDuration();
                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                            finalDuration = duration;
                        }
                    }
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission)) {
                                if (!groupNode.hasExpiry()) {
                                    if (!inheritanceNode.hasExpiry()) {
                                        return null;
                                    } else {
                                        Duration duration = inheritanceNode.getExpiryDuration();
                                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                            finalDuration = duration;
                                        }
                                    }
                                } else {
                                    Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                    if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                        finalDuration = groupPermissionDuration;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (finalDuration != null) {
                long days = finalDuration.toDays();
                long hours = finalDuration.toHours() % 24;
                long minutes = finalDuration.toMinutes() % 60;
                long seconds = finalDuration.getSeconds() % 60;

                StringBuilder builder = new StringBuilder();
                if (days > 0) builder.append(days).append("d ");
                if (hours > 0 || days > 0) builder.append(String.format("%02dh ", hours));
                if (minutes > 0 || builder.length() > 0) builder.append(String.format("%02dm ", minutes));
                builder.append(String.format("%02ds", seconds));

                return builder.toString().trim();
            }

            return null;
        });
    }

    public static CompletableFuture<Boolean> hasPermission(UUID uuid, String permission) {
        LuckPerms api = LuckPermsProvider.get();

        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) {
                return false;
            }

            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    return true;
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());
                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission)) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        });
    }


}
