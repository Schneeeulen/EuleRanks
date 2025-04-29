package msi.schneeeule.euleRanks.System;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RankProvider {
    public enum Ranks {
        ADMINISTRATION("owl.rank.administration", "Administration", "Admin", "§f", ChatColor.WHITE, 1000),
        MANAGEMENT("owl.rank.management", "Management", "Manage", "§9", ChatColor.BLUE, 950),
        MODERATION("owl.rank.moderation", "Moderation", "Mod", "§4", ChatColor.DARK_RED, 900),
        DEVELOPMENT_PLUS("owl.rank.development+", "Development+", "Dev+", "§2", ChatColor.DARK_GREEN, 850),
        DEVELOPMENT("owl.rank.development", "Development", "Dev", "§2", ChatColor.DARK_GREEN, 800),
        SUPPORT_PLUS("owl.rank.support+", "Support+", "Sup+", "§e", ChatColor.YELLOW, 750),
        SUPPORT("owl.rank.support", "Support", "Sup", "§e", ChatColor.YELLOW, 700),
        DESIGN_PLUS("owl.rank.design+", "Design+", "Design+", "§3", ChatColor.DARK_AQUA, 650),
        DESIGN("owl.rank.design", "Design", "Design", "§3", ChatColor.DARK_AQUA, 600),
        PARTNER("owl.rank.partner", "Partner", "Partner", "§5", ChatColor.DARK_PURPLE, 550),
        VIP("owl.rank.vip", "VIP", "VIP", "§d", ChatColor.LIGHT_PURPLE, 500),
        MVP("owl.rank.mvp", "MVP", "MVP", "§b", ChatColor.AQUA, 400),
        PREMIUM("owl.rank.premium", "Premium", "Premium", "§a", ChatColor.GREEN, 300),
        USER("owl.rank.user", "User", null, "§7", ChatColor.GRAY, 100),
        FALLBACK(null, "User", null, "§7", ChatColor.GRAY, 0);


        private final String permission;
        private final String name;
        private final String whiteName;
        private final String prefix;
        private final String colourcode;
        private final ChatColor colour;
        private final int priority;

        Ranks(String permission, String name, String prefix, String colourcode, ChatColor colour, int priority) {
            this.permission = permission;
            this.name = colourcode + name;
            this.whiteName = name;
            this.prefix = (prefix == null ? null : colourcode + prefix);
            this.colourcode = colourcode;
            this.colour = colour;
            this.priority = priority;
        }

        public String getPermission() {return permission;}
        public String getName() {return name;}
        public String getWhiteName() {return whiteName;}
        public String getPrefix() {return prefix;}
        public String getColourcode() {return colourcode;}
        public ChatColor getColour() {return colour;}
        public int getPriority() {return priority;}

        public static Ranks getRank(Player p) {
            for (Ranks rank : Ranks.values()) {
                if (rank.permission == null) continue;
                if (p.hasPermission(rank.permission)) {
                    return rank;
                }
            }
            return FALLBACK;
        }
    }

    public static String spacer = "§7 ¦ ";
    public static int plus_priority_boost = 10;

    public static String getPlusOption(Player p) {
        if (p.hasPermission("owl.rank.plus") && Ranks.getRank(p).getPriority() < 500) return "+";
        return "";
    }

    public static String getColouredPlusOption(Player p) {
        if (p.hasPermission("owl.rank.plus") && Ranks.getRank(p).getPriority() < 500)
            return Ranks.getRank(p).getColourcode() + "+";
        return "";
    }


}
