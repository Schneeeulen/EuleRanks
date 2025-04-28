package msi.schneeeule.euleRanks.System;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.time.Duration;

public class LuckPermsIntegration {

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
                    String groupName = inheritanceNode.getGroupName();
                    Group group = api.getGroupManager().getGroup(groupName);

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
                String groupName = inheritanceNode.getGroupName();
                Group group = api.getGroupManager().getGroup(groupName);

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

}
