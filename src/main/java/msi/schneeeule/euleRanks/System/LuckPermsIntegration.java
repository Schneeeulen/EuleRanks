package msi.schneeeule.euleRanks.System;

import msi.schneeeule.euleRanks.Events.LuckPermsNodeChangeEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
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
        EventBus eventBus = LuckPermsProvider.get().getEventBus();

        eventBus.subscribe(plugin, UserDataRecalculateEvent.class, event -> {
            Player p = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (p != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    DisplayManager.updatePlayer(p);
                });
            }
        });

        eventBus.subscribe(plugin, NodeAddEvent.class, event -> {
            if (event.getTarget() instanceof User) {
                User user = (User) event.getTarget();

                if (event.getNode() instanceof Group) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.GROUP_ADD, event.getNode().getKey().toString()
                        ));
                    });


                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.ADD, event.getNode().getKey().toString()
                        ));
                    });
                }
            }
        });

        eventBus.subscribe(plugin, NodeRemoveEvent.class, event -> {
            if (event.getTarget() instanceof User) {
                User user = (User) event.getTarget();

                if (event.getNode() instanceof Group) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.GROUP_REMOVE, event.getNode().getKey().toString()
                        ));
                    });
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.REMOVE, event.getNode().getKey().toString()
                        ));
                    });
                }
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
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) return false;
            return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        });
    }

    public static RankProvider.Ranks getRank(UUID uuid) {
        CompletableFuture<User> userFuture = LuckPermsProvider.get().getUserManager().loadUser(uuid);
        try {
            User user = userFuture.get();
            if (user == null) return null;

            for (RankProvider.Ranks rank : RankProvider.Ranks.values()) {
                if (rank.getPermission() == null) continue;

                boolean hasPermission = user.getCachedData().getPermissionData().checkPermission(rank.getPermission()).asBoolean();
                if (hasPermission) {
                    return rank;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RankProvider.Ranks.FALLBACK;
    }


}
